/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.result;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import burai.app.QEFXAppController;
import burai.app.QEFXMain;
import burai.app.fileview.QEFXFileViewDialog;
import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.QEFXResultExplorer;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;
import burai.project.Project;

public class QEFXResultFileTreeController extends QEFXAppController {

    private static final double GRAPHIC_SIZE = 20.0;
    private static final String GRAPHIC_CLASS = "piclight-button";

    private static final String HEAD_TO_AVOID = ".";

    private Project project;

    private QEFXResultExplorer explorer;

    @FXML
    private Button reloadButton;

    @FXML
    private Button exitButton;

    @FXML
    private TreeView<File> treeView;

    public QEFXResultFileTreeController(QEFXProjectController projectController, Project project) {
        super(projectController == null ? null : projectController.getMainController());

        if (projectController == null) {
            throw new IllegalArgumentException("projectController is null.");
        }

        if (project == null) {
            throw new IllegalArgumentException("project is null.");
        }

        this.project = project;
        this.explorer = null;
    }

    public void setResultExplorer(QEFXResultExplorer explorer) {
        this.explorer = explorer;
    }

    public void reload() {
        if (this.treeView != null) {
            this.treeView.setRoot(null);
        }

        this.updateTreeView();
    }

    public void reloadAll() {
        this.reload();

        if (this.explorer != null) {
            this.explorer.reload();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupReloadButton();
        this.setupExitButton();
        this.setupTreeView();
    }

    private void setupReloadButton() {
        if (this.reloadButton == null) {
            return;
        }

        this.reloadButton.setText("");
        this.reloadButton.setGraphic(SVGLibrary.getGraphic(SVGData.ARROW_ROUND, GRAPHIC_SIZE, null, GRAPHIC_CLASS));
        this.reloadButton.setOnAction(event -> this.reloadAll());
    }

    private void setupExitButton() {
        if (this.exitButton == null) {
            return;
        }

        this.exitButton.setText("");
        this.exitButton.setGraphic(SVGLibrary.getGraphic(SVGData.STOP, GRAPHIC_SIZE, null, GRAPHIC_CLASS));
        this.exitButton.setOnAction(event -> this.exitAction());
    }

    private void exitAction() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        QEFXMain.initializeDialogOwner(alert);
        alert.setHeaderText("Calculation will be stopped.");
        Optional<ButtonType> optButtonType = alert.showAndWait();
        if (optButtonType == null || !optButtonType.isPresent()) {
            return;
        }
        if (!ButtonType.OK.equals(optButtonType.get())) {
            return;
        }

        String dirPath = this.project.getDirectoryPath();
        if (dirPath == null || dirPath.isEmpty()) {
            return;
        }

        String exitName = this.project.getExitFileName();
        if (exitName == null || exitName.isEmpty()) {
            return;
        }

        try {
            File exitFile = new File(dirPath, exitName);
            exitFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.reloadAll();
    }

    private void setupTreeView() {
        if (this.treeView == null) {
            return;
        }

        this.treeView.setEditable(false);
        this.treeView.setCellFactory(treeView2 -> {
            return new FileTreeCell(this);
        });

        this.treeView.setOnKeyPressed(event -> {
            if (event == null || (!KeyCode.ENTER.equals(event.getCode()))) {
                return;
            }

            MultipleSelectionModel<TreeItem<File>> selectionModel = this.treeView.getSelectionModel();

            TreeItem<File> item = null;
            if (selectionModel != null) {
                item = selectionModel.getSelectedItem();
            }

            if (item != null) {
                if (item.isLeaf()) {
                    File file = item.getValue();
                    if (file != null) {
                        this.showDialog(file);
                    }

                } else {
                    item.setExpanded(!item.isExpanded());
                }
            }
        });

        this.updateTreeView();
    }

    private static class FileTreeCell extends TreeCell<File> {

        private QEFXResultFileTreeController root;

        public FileTreeCell(QEFXResultFileTreeController root) {
            if (root == null) {
                throw new IllegalArgumentException("root is null.");
            }

            this.root = root;
        }

        @Override
        public void updateItem(File file, boolean empty) {
            super.updateItem(file, empty);
            if (file == null || empty) {
                this.setText(null);
                this.setGraphic(null);
                this.setOnMouseClicked(null);
                return;
            }

            this.setText(file.getName());
            this.setGraphic(null);
            this.setOnMouseClicked(event -> {
                if (event != null && event.getClickCount() >= 2) {
                    this.root.showDialog(file);
                }
            });
        }
    }

    private void showDialog(File file) {
        if (file == null) {
            return;
        }

        try {
            if (file.isFile()) {
                QEFXFileViewDialog fileDialog = new QEFXFileViewDialog(file);
                fileDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTreeView() {
        if (this.treeView == null) {
            return;
        }

        String dirPath = this.project.getDirectoryPath();

        File dirFile = null;
        if (dirPath != null && (!dirPath.isEmpty())) {
            dirFile = new File(dirPath);
        }

        TreeItem<File> rootItem = null;
        if (dirFile != null) {
            rootItem = this.getTreeItem(dirFile, true);
        }

        if (rootItem != null) {
            rootItem.setExpanded(true);
            this.treeView.setRoot(rootItem);
        }
    }

    private TreeItem<File> getTreeItem(File file, boolean root) {
        if (file == null) {
            return null;
        }

        try {
            if (file.isFile()) {
                return new TreeItem<File>(file);

            } else if (file.isDirectory()) {
                File[] files = null;
                if (!root) {
                    files = file.listFiles();

                } else {
                    files = file.listFiles((dir, name) -> {
                        if (name == null || name.startsWith(HEAD_TO_AVOID)) {
                            return false;
                        } else {
                            return true;
                        }
                    });
                }

                TreeItem<File> item = new TreeItem<File>(file);

                if (files != null) {
                    for (File file2 : files) {
                        TreeItem<File> item2 = null;
                        if (file2 != null) {
                            item2 = this.getTreeItem(file2, false);
                        }
                        if (item2 != null) {
                            item.getChildren().add(item2);
                        }
                    }
                }

                return item;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }
}
