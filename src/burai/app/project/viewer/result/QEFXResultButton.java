/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result;

import java.io.IOException;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import burai.app.project.QEFXProjectController;
import burai.app.project.editor.result.QEFXResultEditor;

public abstract class QEFXResultButton<V extends QEFXResultViewer<?>, E extends QEFXResultEditor<?>> {

    private static final double BUTTON_HEIGHT = 105.0;
    private static final double BUTTON_WIDTH = 160.0;

    private static final String ICON_CLASS = "result-icon";
    private static final String LABEL_CLASS = "result-icon-label";
    private static final String SUBLABEL_CLASS = "result-icon-sublabel";

    private BorderPane iconPane;

    private Label titleLabel;

    private Label subTitleLabel;

    private V resultViewer;

    private E resultEditor;

    protected QEFXProjectController projectController;

    protected QEFXResultButton(QEFXProjectController projectController, String title, String subTitle) {
        if (projectController == null) {
            throw new IllegalArgumentException("projectController is null.");
        }

        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("title is empty.");
        }

        this.projectController = projectController;
        this.resultViewer = null;
        this.resultEditor = null;
        this.createIcon(title, subTitle);
    }

    public Node getNode() {
        return this.iconPane;
    }

    protected abstract V createResultViewer() throws IOException;

    protected abstract E createResultEditor(V resultViewer) throws IOException;

    protected void setIconStyle(String style) {
        if (this.iconPane != null) {
            this.iconPane.setStyle(style);
        }
    }

    public void setLabelStyle(String style) {
        if (this.titleLabel != null) {
            this.titleLabel.setStyle(style);
        }

        if (this.subTitleLabel != null) {
            this.subTitleLabel.setStyle(style);
        }
    }

    private void createIcon(String title, String subTitle) {
        BorderPane innerPane = new BorderPane();
        this.iconPane = new BorderPane(new Group(innerPane));
        this.iconPane.getStyleClass().add(ICON_CLASS);
        this.iconPane.setPrefHeight(BUTTON_HEIGHT);
        this.iconPane.setPrefWidth(BUTTON_WIDTH);
        this.iconPane.setOnMousePressed(event -> this.updateResults());

        this.titleLabel = new Label(title);
        this.titleLabel.getStyleClass().add(LABEL_CLASS);
        BorderPane.setAlignment(this.titleLabel, Pos.CENTER);
        innerPane.setCenter(this.titleLabel);

        if (subTitle != null) {
            this.subTitleLabel = new Label(subTitle);
            this.subTitleLabel.getStyleClass().add(SUBLABEL_CLASS);
            BorderPane.setAlignment(this.subTitleLabel, Pos.TOP_RIGHT);
            innerPane.setBottom(this.subTitleLabel);
            innerPane.setTop(new Label("  "));
            innerPane.setRight(new Label("  "));
            innerPane.setLeft(new Label("  "));
        }
    }

    private void updateResults() {
        if (this.resultViewer == null) {
            try {
                this.resultViewer = this.createResultViewer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (this.resultEditor == null) {
            try {
                this.resultEditor = this.createResultEditor(this.resultViewer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Node viewerNode = this.resultViewer == null ? null : this.resultViewer.getNode();
        Node editorNode = this.resultEditor == null ? null : this.resultEditor.getNode();

        if (viewerNode != null && editorNode != null) {
            this.projectController.setResultViewerMode();
            this.projectController.clearStackedsOnViewerPane();
            this.projectController.setViewerPane(viewerNode);
            this.projectController.setEditorPane(editorNode);

            Platform.runLater(() -> {
                this.resultViewer.reload();
            });
        }
    }
}
