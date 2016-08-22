/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.save;

import java.io.File;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import burai.app.QEFXMain;
import burai.app.QEFXMainController;
import burai.app.project.QEFXProjectController;
import burai.com.env.Environments;
import burai.project.Project;

public class SaveAction {

    private Project project;

    private QEFXProjectController controller;

    public SaveAction(Project project, QEFXProjectController controller) {
        if (project == null) {
            throw new IllegalArgumentException("project is null.");
        }

        if (controller == null) {
            throw new IllegalArgumentException("controller is null.");
        }

        this.project = project;
        this.controller = controller;
    }

    public boolean saveProject() {
        if (this.isThereDirectory()) {
            this.project.saveQEInputs();
            SaveText saveText = new SaveText(this.controller);
            saveText.playDecayingAnimation();
            return true;

        } else {
            return this.saveProjectAsNew();
        }
    }

    private boolean isThereDirectory() {
        String dirPath = this.project.getDirectoryPath();
        if (dirPath == null || dirPath.isEmpty()) {
            return false;
        }

        try {
            File dirFile = new File(dirPath);
            if (!dirFile.isDirectory()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean saveProjectAsNew() {
        File directory = null;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save project");
        String projPath = Environments.getProjectsPath();
        if (projPath != null) {
            fileChooser.setInitialDirectory(new File(projPath));
        }

        Stage stage = this.controller.getStage();
        if (stage != null) {
            directory = fileChooser.showSaveDialog(stage);
        }

        if (directory == null) {
            return false;
        }

        try {
            if (directory.exists()) {
                Alert alert = new Alert(AlertType.ERROR);
                QEFXMain.initializeDialogOwner(alert);
                DialogPane dialogPane = alert.getDialogPane();
                if (dialogPane != null) {
                    dialogPane.setHeaderText(directory.getName() + " already exists.");
                }

                alert.showAndWait();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        QEFXMainController mainController = this.controller.getMainController();
        if (mainController != null) {
            mainController.offerOnHomeTabSelected(explorerFacade -> {
                if (explorerFacade != null) {
                    explorerFacade.refreshProject(this.project);
                }
            });
        }

        this.project.saveQEInputs(directory.getPath());
        Environments.addRecentFilePath(this.project.getRelatedFilePath());

        if (mainController != null) {
            mainController.offerOnHomeTabSelected(explorerFacade -> {
                if (explorerFacade != null && explorerFacade.isRecentlyUsedMode()) {
                    explorerFacade.reloadLocation();
                }
            });
        }

        File parentFile = directory.getParentFile();
        if (mainController != null && parentFile != null) {
            mainController.offerOnHomeTabSelected(explorerFacade -> {
                String location = null;
                if (explorerFacade != null) {
                    location = explorerFacade.getLocation();
                }

                File locationFile = null;
                if (location != null && (!location.isEmpty())) {
                    locationFile = new File(location);
                }

                if (locationFile != null && locationFile.equals(parentFile)) {
                    explorerFacade.reloadLocation();
                }
            });
        }

        SaveText saveText = new SaveText(this.controller);
        saveText.playDecayingAnimation();
        return true;
    }
}
