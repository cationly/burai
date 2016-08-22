/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.onclose;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import burai.app.QEFXMain;
import burai.project.Project;

public class QEFXSavingDialog extends Dialog<List<Project>> {

    private ListView<ProjectSaver> listView;

    public QEFXSavingDialog(List<Project> projects) {
        super();

        DialogPane dialogPane = this.getDialogPane();
        QEFXMain.initializeStyleSheets(dialogPane.getStylesheets());
        QEFXMain.initializeDialogOwner(this);

        this.setResizable(false);
        this.setTitle("Save projects");
        this.setHeaderText("The following projects are not saved. Do you save them ?");

        dialogPane.getButtonTypes().clear();
        dialogPane.getButtonTypes().add(ButtonType.YES);
        dialogPane.getButtonTypes().add(ButtonType.NO);
        dialogPane.getButtonTypes().add(ButtonType.CANCEL);

        if (projects != null && (!projects.isEmpty())) {
            this.createListView(projects);
        }

        Node node = this.createContent();
        if (node != null) {
            dialogPane.setContent(node);
        }

        this.setResultConverter(buttonType -> {
            List<Project> savingProjects = new ArrayList<Project>();

            if (ButtonType.YES.equals(buttonType)) {
                if (this.listView != null) {
                    List<ProjectSaver> savers = this.listView.getItems();
                    for (ProjectSaver saver : savers) {
                        if (saver != null && saver.isToSave()) {
                            savingProjects.add(saver.getProject());
                        }
                    }
                }
            }

            if (!ButtonType.CANCEL.equals(buttonType)) {
                return savingProjects;
            }

            return null;
        });
    }

    public boolean hasProjects() {
        if (this.listView == null) {
            return false;
        }

        return !this.listView.getItems().isEmpty();
    }

    public boolean showAndSave() {
        Optional<List<Project>> optProjects = this.showAndWait();
        if (optProjects == null || (!optProjects.isPresent())) {
            return false;
        }

        List<Project> projects = optProjects.get();
        if (projects == null) {
            return false;
        }

        for (Project project : projects) {
            if (project != null) {
                project.saveQEInputs();
            }
        }

        return true;
    }

    private Node createContent() {
        BorderPane pane = new BorderPane();

        Label label = new Label("Checked projects will be saved.");
        BorderPane.setAlignment(label, Pos.BOTTOM_RIGHT);
        pane.setBottom(label);

        if (this.listView != null) {
            pane.setCenter(this.listView);
        } else {
            pane.setCenter(new Label("NO PROJECTS"));
        }

        return pane;
    }

    private void createListView(List<Project> projects) {
        if (projects == null || projects.isEmpty()) {
            return;
        }

        this.listView = new ListView<ProjectSaver>();
        this.listView.setCellFactory(listView_ -> {
            return new ProjectCell();
        });

        this.listView.getItems().clear();

        for (Project project : projects) {
            if (project != null && project.isQEInputChanged()) {
                ProjectSaver saver = new ProjectSaver(project);
                if (project.getDirectoryPath() != null) {
                    saver.setToSave(true);
                } else {
                    saver.setToSave(false);
                }

                this.listView.getItems().add(saver);
            }
        }
    }

    private static class ProjectSaver {

        private Project project;

        private boolean toSave;

        public ProjectSaver(Project project) {
            if (project == null) {
                throw new IllegalArgumentException("project is null.");
            }

            this.project = project;
            this.toSave = false;
        }

        public Project getProject() {
            return this.project;
        }

        public void setToSave(boolean toSave) {
            this.toSave = toSave;
        }

        public boolean isToSave() {
            return this.toSave;
        }
    }

    private static class ProjectCell extends ListCell<ProjectSaver> {

        public ProjectCell() {
            // NOP
        }

        @Override
        protected void updateItem(ProjectSaver saver, boolean empty) {
            super.updateItem(saver, empty);
            if (saver == null || empty) {
                this.setGraphic(null);
                return;
            }

            Project project = saver.getProject();
            String name = project == null ? "" : project.getRelatedFilePath();

            CheckBox checkBox = new CheckBox();
            checkBox.setSelected(saver.isToSave());
            checkBox.setText(name);

            if (project != null && project.getDirectoryPath() != null) {
                checkBox.setDisable(false);
            } else {
                checkBox.setDisable(true);
            }

            this.setGraphic(checkBox);
        }
    }
}
