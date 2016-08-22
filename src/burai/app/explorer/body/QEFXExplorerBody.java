/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.explorer.body;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import burai.app.QEFXMain;
import burai.app.icon.IconAction;
import burai.app.icon.IconSearcher;
import burai.app.icon.QEFXFolderIcon;
import burai.app.icon.QEFXIcon;
import burai.app.icon.QEFXProjectIcon;
import burai.app.icon.QEFXRunningIcon;
import burai.app.icon.QEFXWebIcon;
import burai.com.env.Environments;
import burai.com.file.FileTools;
import burai.com.keys.KeyCodeConverter;
import burai.matapi.MaterialsAPILoader;
import burai.matapi.MaterialsAPIQueue;
import burai.project.Project;
import burai.run.RunningNode;

public abstract class QEFXExplorerBody {

    private static final int STARTING_INDEX_TO_SLEEP = 8;
    private static final int NUMBER_OF_ICONS_A_FXQUEUE = 16;
    private static final long SLEEP_TIME_TO_SHOW_ICON = 25L;

    public static final String CODE_HEAD = "#";
    public static final String CODE_RECENTLY_USED = CODE_HEAD + "RecentlyUsed";
    public static final String CODE_CALCULATING = CODE_HEAD + "Calculating";
    public static final String CODE_SEARCHED = CODE_HEAD + "Searched";
    public static final String CODE_WEB = CODE_HEAD + "Web";

    private String directoryName;

    private FileQueue fileQueue;

    private IconSearcher iconSearcher;

    private IconAction onIconSelected;

    private IconAction onIconOpensTab;

    private IconAction onIconCopied;

    private QEFXIcon clippedIcon;

    private boolean alive;

    public QEFXExplorerBody(String directoryName,
            List<Project> shownProjects, MaterialsAPILoader matApiLoader) throws IOException {

        if (directoryName == null || directoryName.isEmpty()) {
            throw new IllegalArgumentException("directoryName is empty.");
        }

        this.directoryName = directoryName;

        MaterialsAPIQueue matApiQueue = null;
        if (this.isSearchedMode() && matApiLoader != null) {
            matApiQueue = matApiLoader.getQueue();
        }

        if (matApiQueue != null) {
            this.fileQueue = new SearchedQueue(matApiQueue, shownProjects);

        } else if (this.isCalculatingMode()) {
            this.fileQueue = new CalculatingQueue();

        } else {
            this.fileQueue = new ExplorerQueue(this, shownProjects);
        }

        this.fileQueue.setOnFileElementDeleted(fileElement -> this.deleteFileElement(fileElement));

        this.iconSearcher = new IconSearcher();
        this.onIconSelected = null;
        this.onIconOpensTab = null;
        this.onIconCopied = null;
        this.clippedIcon = null;
        this.alive = true;
    }

    private synchronized boolean isAlive() {
        return this.alive;
    }

    public synchronized void detachFromParent() {
        this.alive = false;
        this.fileQueue.stopFileElements();

        this.iconSearcher.clear();
        this.onIconSelected = null;
        this.onIconOpensTab = null;
        this.onIconCopied = null;
        this.clippedIcon = null;
    }

    public abstract Node getNode();

    protected abstract int indexOfIcon(QEFXIcon icon);

    protected abstract int indexOfIcon(Project project);

    protected abstract int[] indexOfAllIcons(Project project);

    protected abstract int indexOfIcon(RunningNode runningNode);

    protected abstract QEFXIcon getIconAt(int position);

    protected abstract void onIconShowing(QEFXIcon icon, int position, boolean swapping);

    protected abstract void onIconDeleted(QEFXIcon icon);

    protected abstract void onIconSearched(QEFXIcon icon);

    public void setOnIconSelected(IconAction onIconSelected) {
        this.onIconSelected = onIconSelected;
    }

    public void setOnIconOpensTab(IconAction onIconOpensTab) {
        this.onIconOpensTab = onIconOpensTab;
    }

    public void setOnIconCopied(IconAction onIconCopied) {
        this.onIconCopied = onIconCopied;
    }

    public boolean hasClippedIcon() {
        if (this.clippedIcon != null) {
            File file = this.clippedIcon.getCorrespondingFile();
            try {
                if (file != null && file.exists()) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }

    public void setClippedIcon(QEFXIcon clippedIcon) {
        this.clippedIcon = clippedIcon;
    }

    public String getDirectoryName() {
        return this.directoryName;
    }

    public boolean isExplorerMode() {
        return !this.directoryName.startsWith(CODE_HEAD);
    }

    public boolean isRecentlyUsedMode() {
        return this.directoryName.equalsIgnoreCase(CODE_RECENTLY_USED);
    }

    public boolean isCalculatingMode() {
        return this.directoryName.equalsIgnoreCase(CODE_CALCULATING);
    }

    public boolean isSearchedMode() {
        return this.directoryName.equalsIgnoreCase(CODE_SEARCHED);
    }

    public boolean isWebMode() {
        return this.directoryName.equalsIgnoreCase(CODE_WEB);
    }

    protected void showIcons() {
        if (this.fileQueue.hasFileElements()) {
            this.showIconsKernel(STARTING_INDEX_TO_SLEEP);
        }

        Thread thread = new Thread(() -> {
            this.showIconsKernel(-1);
        });

        thread.start();
    }

    private void showIconsKernel(int numIcons) {
        while (this.isAlive()) {

            final int numIcons_;
            if (numIcons > 0) {
                numIcons_ = numIcons;
            } else {
                numIcons_ = NUMBER_OF_ICONS_A_FXQUEUE;
            }

            Platform.runLater(() -> {
                for (int i = 0; i < numIcons_; i++) {
                    if (!this.fileQueue.hasFileElements()) {
                        break;
                    }

                    FileElement fileElement = this.fileQueue.pollFileElement();
                    if (fileElement == null) {
                        break;
                    }

                    String fileName = fileElement.getName();
                    if (fileName == null || fileName.trim().isEmpty()) {
                        continue;
                    }

                    QEFXIcon icon = null;
                    Project project = fileElement.getProject();
                    RunningNode runningNode = fileElement.getRunningNode();

                    if (this.isExplorerMode()) {
                        File file = new File(this.directoryName, fileName);
                        if (runningNode == null) {
                            icon = QEFXIcon.getInstance(file, project);
                        } else {
                            icon = QEFXIcon.getInstance(file, runningNode);
                        }

                    } else {
                        if (runningNode == null) {
                            icon = QEFXIcon.getInstance(fileName, project);
                        } else {
                            icon = QEFXIcon.getInstance(fileName, runningNode);
                        }
                    }

                    if (icon != null) {
                        this.iconSearcher.addIcon(icon);
                    }

                    this.onIconShowing(icon, fileElement.getPosition(), fileElement.isSwapping());
                }
            });

            if (numIcons > 0) {
                break;
            }

            synchronized (this) {
                try {
                    this.wait(SLEEP_TIME_TO_SHOW_ICON);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            FileElement headElement = this.fileQueue.peekFileElement();
            if (headElement == null) {
                break;
            }
        }
    }

    protected void searchIcon(KeyCode code) {
        char codeChar = KeyCodeConverter.toCharacter(code);
        if (codeChar == 0) {
            return;
        }

        QEFXIcon icon = this.iconSearcher.search(Character.toString(codeChar));

        this.onIconSearched(icon);
    }

    public void selectIcon(QEFXIcon icon) {
        if (icon == null) {
            return;
        }

        if (this.onIconSelected != null) {
            this.onIconSelected.actionOnIcon(icon);
        }
    }

    public void openTabFromIcon(QEFXIcon icon) {
        if (icon == null) {
            return;
        }

        if (this.onIconOpensTab != null) {
            this.onIconOpensTab.actionOnIcon(icon);
        }
    }

    public void renameIcon(QEFXIcon icon) {
        if (icon == null) {
            return;
        }

        int index = this.indexOfIcon(icon);
        if (index < 0) {
            return;
        }

        if (this.isExplorerMode()) {
            File directory = null;
            if (this.directoryName != null && (!this.directoryName.isEmpty())) {
                directory = new File(this.directoryName);
            }
            if (directory == null) {
                return;
            }

            File srcFile = icon.getCorrespondingFile();
            if (srcFile == null) {
                return;
            }

            QEFXFileNameDialog dialog = null;
            if (icon instanceof QEFXFolderIcon) {
                dialog = new QEFXFileNameDialog("Rename directory: " + srcFile.getName() + ".", directory, true);
            } else {
                dialog = new QEFXFileNameDialog("Rename file: " + srcFile.getName() + ".", directory, false);
                dialog.setExtension(srcFile.getName());
            }

            Optional<File> optFile = dialog.showAndWait();
            if (optFile == null || (!optFile.isPresent())) {
                return;
            }
            File dstFile = optFile.get();
            if (dstFile == null) {
                return;
            }

            boolean fileStatus = false;
            try {
                fileStatus = srcFile.renameTo(dstFile);
            } catch (Exception e) {
                fileStatus = false;
            }

            if (fileStatus) {
                Project project = null;
                RunningNode runningNode = null;
                if (icon instanceof QEFXProjectIcon) {
                    project = ((QEFXProjectIcon) icon).getContent();
                }
                if (icon instanceof QEFXRunningIcon) {
                    runningNode = ((QEFXRunningIcon) icon).getRunningNode();
                }
                if (project != null) {
                    project.renameRelatedFile(dstFile.getPath());
                }

                QEFXIcon dstIcon = null;
                if (runningNode == null) {
                    dstIcon = QEFXIcon.getInstance(dstFile, project);
                } else {
                    dstIcon = QEFXIcon.getInstance(dstFile, runningNode);
                }
                if (dstIcon != null) {
                    this.iconSearcher.swapIcon(icon, dstIcon);
                    this.onIconShowing(dstIcon, index, true);
                }

            } else {
                Alert alert = new Alert(AlertType.ERROR);
                QEFXMain.initializeDialogOwner(alert);
                alert.setHeaderText("Cannot rename file: " + dstFile.getName() + ".");
                alert.showAndWait();
            }
        }
    }

    public void copyIcon(QEFXIcon icon) {
        if (icon == null) {
            return;
        }

        if (this.onIconCopied != null) {
            this.onIconCopied.actionOnIcon(icon);
        }
    }

    public void pasteIcon(QEFXIcon icon) {
        if (!this.hasClippedIcon()) {
            return;
        }

        int index = -1;
        if (icon != null) {
            index = this.indexOfIcon(icon);
        }

        if (this.isExplorerMode()) {
            File srcFile = this.clippedIcon.getCorrespondingFile();
            try {
                if (srcFile == null || (!srcFile.exists())) {
                    return;
                }
            } catch (Exception e) {
                return;
            }

            String dstName = srcFile.getName();
            if (dstName == null || dstName.isEmpty()) {
                return;
            }

            File dstFile = new File(this.directoryName, dstName);
            try {
                for (int i = 2; dstFile.exists(); i++) {
                    dstFile = new File(this.directoryName, this.modifyName(dstName, i));
                }
            } catch (Exception e) {
                return;
            }

            boolean fileStatus = false;
            try {
                fileStatus = FileTools.copyAllFiles(srcFile, dstFile);
            } catch (Exception e) {
                fileStatus = false;
            }

            if (fileStatus) {
                QEFXIcon newIcon = QEFXIcon.getInstance(dstFile);
                if (newIcon != null) {
                    if (icon != null) {
                        this.iconSearcher.insertIcon(icon, newIcon);
                    } else {
                        this.iconSearcher.addIcon(newIcon);
                    }
                    this.onIconShowing(newIcon, index, false);
                }

            } else {
                Alert alert = new Alert(AlertType.ERROR);
                QEFXMain.initializeDialogOwner(alert);
                alert.setHeaderText("Cannot paste file: " + dstFile.getName() + ".");
                alert.showAndWait();
            }
        }
    }

    private String modifyName(String name, int i) {
        if (name == null) {
            return null;
        }

        int index = name.lastIndexOf(".");

        if (index > -1) {
            String stemName = index == 0 ? "" : name.substring(0, index);
            String rimName = name.substring(index);
            return stemName + "#" + i + rimName;

        } else {
            return name + "#" + i;
        }
    }

    public synchronized void deleteFileElement(FileElement fileElement) {
        if (!this.alive) {
            return;
        }

        if (fileElement == null) {
            return;
        }

        RunningNode runningNode = fileElement.getRunningNode();
        if (runningNode != null) {
            int index = this.indexOfIcon(runningNode);
            QEFXIcon icon = index < 0 ? null : this.getIconAt(index);
            if (icon != null) {
                this.iconSearcher.removeIcon(icon);
                this.onIconDeleted(icon);
            }
            return;
        }

        Project project = fileElement.getProject();
        if (project != null) {
            int index = this.indexOfIcon(project);
            QEFXIcon icon = index < 0 ? null : this.getIconAt(index);
            if (icon != null) {
                this.iconSearcher.removeIcon(icon);
                this.onIconDeleted(icon);
            }
            return;
        }

        String fileName = fileElement.getName();
        if (fileName == null || fileName.trim().isEmpty()) {
            // TODO
        }
    }

    public void deleteIcon(QEFXIcon icon) {
        if (icon == null) {
            return;
        }

        if (this.isExplorerMode()) {
            this.deleteExplorer(icon);

        } else if (this.isRecentlyUsedMode()) {
            this.deleteRecentlyUsed(icon);

        } else if (this.isWebMode()) {
            this.deleteWeb(icon);
        }
    }

    private void deleteExplorer(QEFXIcon icon) {
        if (icon == null) {
            return;
        }

        File file = icon.getCorrespondingFile();
        if (file == null) {
            return;
        }

        Alert alert = new Alert(AlertType.CONFIRMATION);
        QEFXMain.initializeDialogOwner(alert);
        alert.setHeaderText("'" + file.getName() + "' will be deleted.");
        Optional<ButtonType> optButtonType = alert.showAndWait();
        if (optButtonType == null || (!optButtonType.isPresent())) {
            return;
        }
        if (!ButtonType.OK.equals(optButtonType.get())) {
            return;
        }

        boolean fileStatus = false;
        try {
            fileStatus = FileTools.deleteAllFiles(file);
        } catch (Exception e) {
            fileStatus = false;
        }

        if (fileStatus) {
            this.iconSearcher.removeIcon(icon);
            this.onIconDeleted(icon);

        } else {
            Alert alertError = new Alert(AlertType.ERROR);
            QEFXMain.initializeDialogOwner(alertError);
            alertError.setHeaderText("Cannot delete file: " + file.getName() + ".");
            alertError.showAndWait();
        }
    }

    private void deleteRecentlyUsed(QEFXIcon icon) {
        if (icon == null) {
            return;
        }

        String caption = icon.getCaption();
        if (caption != null) {
            caption = caption.trim();
        } else {
            caption = "";
        }

        Alert alert = new Alert(AlertType.CONFIRMATION);
        QEFXMain.initializeDialogOwner(alert);
        alert.setHeaderText("'" + caption + "' will be removed from this list.");
        Optional<ButtonType> optButtonType = alert.showAndWait();
        if (optButtonType == null || (!optButtonType.isPresent())) {
            return;
        }
        if (!ButtonType.OK.equals(optButtonType.get())) {
            return;
        }

        Project project = null;
        if (icon instanceof QEFXProjectIcon) {
            project = ((QEFXProjectIcon) icon).getContent();
        }

        String filePath = null;
        if (project != null) {
            filePath = project.getRelatedFilePath();
        }

        if (filePath != null && (!filePath.isEmpty())) {
            Environments.removeRecentFilePath(filePath);
            this.iconSearcher.removeIcon(icon);
            this.onIconDeleted(icon);

        } else {
            Alert alertError = new Alert(AlertType.ERROR);
            QEFXMain.initializeDialogOwner(alertError);
            alertError.setHeaderText("Cannot delete item: " + caption + ".");
            alertError.showAndWait();
        }
    }

    private void deleteWeb(QEFXIcon icon) {
        if (icon == null) {
            return;
        }

        String caption = icon.getCaption();
        if (caption != null) {
            caption = caption.trim();
        } else {
            caption = "";
        }

        Alert alert = new Alert(AlertType.CONFIRMATION);
        QEFXMain.initializeDialogOwner(alert);
        alert.setHeaderText("'" + caption + "' will be removed from this list.");
        Optional<ButtonType> optButtonType = alert.showAndWait();
        if (optButtonType == null || (!optButtonType.isPresent())) {
            return;
        }
        if (!ButtonType.OK.equals(optButtonType.get())) {
            return;
        }

        String url = null;
        if (icon instanceof QEFXWebIcon) {
            url = ((QEFXWebIcon) icon).getInitialURL();
        }

        if (url != null && (!url.isEmpty())) {
            Environments.removeWebsite(url);
            this.iconSearcher.removeIcon(icon);
            this.onIconDeleted(icon);

        } else {
            Alert alertError = new Alert(AlertType.ERROR);
            QEFXMain.initializeDialogOwner(alertError);
            alertError.setHeaderText("Cannot delete page: " + caption + ".");
            alertError.showAndWait();
        }
    }

    public void makeDirectory(QEFXIcon icon) {
        int index = -1;
        if (icon != null) {
            index = this.indexOfIcon(icon);
        }

        if (this.isExplorerMode()) {
            File directory = null;
            if (this.directoryName != null && (!this.directoryName.isEmpty())) {
                directory = new File(this.directoryName);
            }
            if (directory == null) {
                return;
            }

            QEFXFileNameDialog dialog = new QEFXFileNameDialog("Make directory.", directory, true);

            Optional<File> optFile = dialog.showAndWait();
            if (optFile == null || (!optFile.isPresent())) {
                return;
            }
            File newDirectory = optFile.get();
            if (newDirectory == null) {
                return;
            }

            boolean fileStatus = false;
            try {
                fileStatus = newDirectory.mkdir();
            } catch (Exception e) {
                fileStatus = false;
            }

            if (fileStatus) {
                QEFXIcon newIcon = QEFXIcon.getInstance(newDirectory);
                if (newIcon != null) {
                    if (icon != null) {
                        this.iconSearcher.insertIcon(icon, newIcon);
                    } else {
                        this.iconSearcher.addIcon(newIcon);
                    }
                    this.onIconShowing(newIcon, index, false);
                }

            } else {
                Alert alert = new Alert(AlertType.ERROR);
                QEFXMain.initializeDialogOwner(alert);
                alert.setHeaderText("Cannot create directory: " + newDirectory.getName() + ".");
                alert.showAndWait();
            }
        }
    }

    public void refreshProject(Project project) {
        if (project == null) {
            return;
        }

        int[] indexes = this.indexOfAllIcons(project);
        if (indexes == null || indexes.length < 1) {
            return;
        }

        for (int index : indexes) {
            QEFXIcon srcIcon = null;
            QEFXIcon dstIcon = null;
            File srcFile = null;
            RunningNode srcRunningNode = null;

            srcIcon = this.getIconAt(index);

            if (srcIcon != null) {
                if (this.fileQueue != null && (fileQueue instanceof CalculatingQueue)) {
                    String path = project.getRelatedFilePath();
                    srcFile = (path == null || path.isEmpty()) ? null : new File(path);

                    if (srcIcon instanceof QEFXRunningIcon) {
                        srcRunningNode = ((QEFXRunningIcon) srcIcon).getRunningNode();
                    }

                } else {
                    srcFile = srcIcon.getCorrespondingFile();
                }
            }

            if (srcFile != null) {
                dstIcon = QEFXIcon.getInstance(srcFile, srcRunningNode);
            }

            if (srcIcon != null && dstIcon != null) {
                this.iconSearcher.swapIcon(srcIcon, dstIcon);
                this.onIconShowing(dstIcon, index, true);
            }
        }
    }
}
