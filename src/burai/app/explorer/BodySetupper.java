/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.explorer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import burai.app.QEFXMain;
import burai.app.QEFXMainController;
import burai.app.explorer.body.QEFXExplorerBody;
import burai.app.explorer.body.QEFXExplorerListView;
import burai.app.explorer.body.QEFXExplorerTileView;
import burai.app.fileview.QEFXFileViewDialog;
import burai.app.icon.QEFXFolderIcon;
import burai.app.icon.QEFXIcon;
import burai.app.icon.QEFXProjectIcon;
import burai.app.icon.QEFXUPFIcon;
import burai.app.icon.QEFXWebIcon;
import burai.app.icon.web.WebEngineWrapper;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;
import burai.com.life.Life;
import burai.matapi.MaterialsAPILoader;
import burai.project.Project;

public class BodySetupper extends ExplorerSetupper {

    private static final double GRAPHIC_SIZE = 20.0;

    private static final int EXPLORER_TYPE_LIST = 0;
    private static final int EXPLORER_TYPE_SMALL_TILES = 1;
    private static final int EXPLORER_TYPE_MEDIUM_TILES = 2;
    private static final int EXPLORER_TYPE_LARGE_TILES = 3;

    private static final double TILE_SIZE_SMALL = 100.0;
    private static final double TILE_SIZE_MEDIUM = 150.0;
    private static final double TILE_SIZE_LARGE = 200.0;

    private MenuButton menuButton;

    private int explorerType;
    private QEFXExplorerBody explorerBody;

    private QEFXIcon clippedIcon;

    private MaterialsAPILoader matApiLoader;

    protected BodySetupper(QEFXExplorerController controller) {
        super(controller);

        this.menuButton = null;

        this.explorerType = EXPLORER_TYPE_MEDIUM_TILES;
        this.explorerBody = null;

        this.clippedIcon = null;

        this.matApiLoader = null;

        Life.getInstance().addOnDead(() -> {
            if (this.explorerBody != null) {
                this.explorerBody.detachFromParent();
            }
        });
    }

    protected QEFXExplorerBody getExplorerBody() {
        return this.explorerBody;
    }

    protected void setMaterialsAPILoader(MaterialsAPILoader matApiLoader) {
        this.matApiLoader = matApiLoader;
    }

    protected void setupBodyPane(BorderPane bodyPane) {
        if (bodyPane == null) {
            return;
        }

        this.updateExplorerBody();
        this.updateMenuButton();
    }

    protected void setupMenuButton(MenuButton menuButton) {
        if (menuButton == null) {
            return;
        }

        menuButton.setText("");
        this.menuButton = menuButton;
    }

    protected void setupMenuItemList(MenuItem menuItem) {
        this.setupMenuItem(menuItem, EXPLORER_TYPE_LIST);
    }

    protected void setupMenuItemSmallTile(MenuItem menuItem) {
        this.setupMenuItem(menuItem, EXPLORER_TYPE_SMALL_TILES);
    }

    protected void setupMenuItemMediumTile(MenuItem menuItem) {
        this.setupMenuItem(menuItem, EXPLORER_TYPE_MEDIUM_TILES);
    }

    protected void setupMenuItemLargeTile(MenuItem menuItem) {
        this.setupMenuItem(menuItem, EXPLORER_TYPE_LARGE_TILES);
    }

    private void setupMenuItem(MenuItem menuItem, int explorerType) {
        if (menuItem == null) {
            return;
        }

        menuItem.setOnAction(event -> {
            this.explorerType = explorerType;
            this.updateMenuButton();
            this.updateExplorerBody();
        });
    }

    private void updateMenuButton() {
        if (this.menuButton == null) {
            return;
        }

        if (this.explorerType == EXPLORER_TYPE_LIST) {
            this.menuButton.setText(" List");
            this.menuButton.setGraphic(SVGLibrary.getGraphic(SVGData.LIST, GRAPHIC_SIZE));

        } else if (this.explorerType == EXPLORER_TYPE_SMALL_TILES) {
            this.menuButton.setText(" Small");
            this.menuButton.setGraphic(SVGLibrary.getGraphic(SVGData.TILES, GRAPHIC_SIZE));

        } else if (this.explorerType == EXPLORER_TYPE_MEDIUM_TILES) {
            this.menuButton.setText(" Medium");
            this.menuButton.setGraphic(SVGLibrary.getGraphic(SVGData.TILES, GRAPHIC_SIZE));

        } else if (this.explorerType == EXPLORER_TYPE_LARGE_TILES) {
            this.menuButton.setText(" Large");
            this.menuButton.setGraphic(SVGLibrary.getGraphic(SVGData.TILES, GRAPHIC_SIZE));
        }
    }

    protected void updateExplorerBody() {
        this.updateExplorerBody(false);
    }

    protected void updateExplorerBody(boolean asUndo) {
        String location = this.controller.getLocationField().getText();
        if (location == null) {
            return;
        }

        location = location.trim();
        if (location.isEmpty()) {
            return;
        }

        QEFXExplorerBody newExplorerBody = this.createExplorerBody(location);
        if (newExplorerBody == null) {
            return;
        }

        if (this.explorerBody != null) {
            this.explorerBody.detachFromParent();
            this.explorerBody = null;
            Platform.runLater(() -> System.gc());
        }

        this.explorerBody = newExplorerBody;

        BorderPane bodyPane = this.controller.getBodyPane();
        if (bodyPane != null) {
            bodyPane.setCenter(this.explorerBody.getNode());
        }

        String locationStored = this.controller.getPreviousLocation();
        if (!location.equals(locationStored)) {
            this.controller.storeLocation(location);
        }

        if (!asUndo) {
            this.controller.clearRedoLocations();
        }

        Node center = bodyPane.getCenter();
        if (center != null) {
            center.requestFocus();
        }
    }

    private QEFXExplorerBody createExplorerBody(String location) {
        final QEFXExplorerBody explorerBody;

        QEFXMainController mainController = this.controller.getMainController();

        List<Project> shownProjects = null;
        if (mainController != null) {
            shownProjects = mainController.getShownProjects();
        } else {
            shownProjects = new ArrayList<Project>();
        }

        try {
            if (this.explorerType == EXPLORER_TYPE_LIST) {
                explorerBody = new QEFXExplorerListView(location, shownProjects, this.matApiLoader);

            } else if (this.explorerType == EXPLORER_TYPE_SMALL_TILES) {
                explorerBody = new QEFXExplorerTileView(location, shownProjects, this.matApiLoader, TILE_SIZE_SMALL);

            } else if (this.explorerType == EXPLORER_TYPE_MEDIUM_TILES) {
                explorerBody = new QEFXExplorerTileView(location, shownProjects, this.matApiLoader, TILE_SIZE_MEDIUM);

            } else if (this.explorerType == EXPLORER_TYPE_LARGE_TILES) {
                explorerBody = new QEFXExplorerTileView(location, shownProjects, this.matApiLoader, TILE_SIZE_LARGE);

            } else {
                explorerBody = null;
            }

        } catch (IOException e) {
            this.showFolderIOException(location, e);
            String locationStored = this.controller.getPreviousLocation();
            if (locationStored != null) {
                this.controller.getLocationField().setText(locationStored);
            }
            return null;
        }

        if (explorerBody == null) {
            return null;
        }

        explorerBody.setOnIconSelected(icon -> {
            if (icon == null) {
                return;
            }

            if (icon instanceof QEFXProjectIcon) {
                this.openTabProject((QEFXProjectIcon) icon, mainController);

            } else if (icon instanceof QEFXWebIcon) {
                this.openTabWebPage((QEFXWebIcon) icon, mainController);

            } else if (icon instanceof QEFXUPFIcon) {
                this.openUPFFile((QEFXUPFIcon) icon);

            } else if (icon instanceof QEFXFolderIcon) {
                this.openNewFolder((QEFXFolderIcon) icon);
            }
        });

        explorerBody.setOnIconOpensTab(icon -> {
            if (icon == null) {
                return;
            }

            if (icon instanceof QEFXProjectIcon) {
                this.openTabProject((QEFXProjectIcon) icon, mainController);

            } else if (icon instanceof QEFXWebIcon) {
                this.openTabWebPage((QEFXWebIcon) icon, mainController);
            }
        });

        explorerBody.setClippedIcon(this.clippedIcon);
        explorerBody.setOnIconCopied(icon -> {
            this.clippedIcon = icon;
            explorerBody.setClippedIcon(this.clippedIcon);
        });

        return explorerBody;
    }

    private void openTabProject(QEFXProjectIcon icon, QEFXMainController mainController) {
        Project project = icon.getContent();
        if (project != null && project.isValid()) {
            if (mainController != null) {
                mainController.showProject(project);
            }
        } else {
            this.showInvaridProject(project);
        }
    }

    private void openTabWebPage(QEFXWebIcon icon, QEFXMainController mainController) {
        String webLocation = null;
        WebEngineWrapper webEngine = icon.getContent();
        if (webEngine != null) {
            webLocation = webEngine.getLocation();
        }
        if (webLocation != null && (!webLocation.trim().isEmpty())) {
            if (mainController != null) {
                mainController.showWebPage(webLocation);
            }
        }
    }

    private void openUPFFile(QEFXUPFIcon icon) {
        String filePath = icon.getContent();
        if (filePath != null && (!filePath.trim().isEmpty())) {
            QEFXFileViewDialog fileDialog = new QEFXFileViewDialog(filePath);
            fileDialog.show();
        }
    }

    private void openNewFolder(QEFXFolderIcon icon) {
        String dirPath = icon.getContent();
        if (dirPath != null && (!dirPath.trim().isEmpty())) {
            this.controller.getLocationField().setText(dirPath);
            this.updateExplorerBody();
        }
    }

    private void showFolderIOException(String location, IOException e) {
        Alert alert = new Alert(AlertType.ERROR);
        QEFXMain.initializeDialogOwner(alert);

        DialogPane dialogPane = alert.getDialogPane();
        if (dialogPane != null) {
            dialogPane.setHeaderText("Cannot open folder: " + location + ".");
            dialogPane.setContentText(e.getMessage());
        }

        alert.showAndWait();
    }

    private void showInvaridProject(Project project) {
        Alert alert = new Alert(AlertType.ERROR);
        QEFXMain.initializeDialogOwner(alert);

        DialogPane dialogPane = alert.getDialogPane();
        if (dialogPane != null) {
            String rootPath = null;
            String dirPath = null;
            if (project != null) {
                rootPath = project.getRootFilePath();
                dirPath = project.getDirectoryPath();
            }

            if (rootPath != null) {
                dialogPane.setHeaderText("Cannot open file.");
                dialogPane.setContentText(rootPath);
            } else if (dirPath != null) {
                dialogPane.setHeaderText("Cannot open project.");
                dialogPane.setContentText(dirPath);
            } else {
                dialogPane.setHeaderText("Incorrect project.");
            }
        }

        alert.showAndWait();
    }

    protected void refreshProject(Project project) {
        if (this.explorerBody != null) {
            this.explorerBody.refreshProject(project);
        }
    }
}
