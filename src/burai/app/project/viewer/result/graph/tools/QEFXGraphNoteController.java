/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.graph.tools;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import burai.app.QEFXAppController;
import burai.app.project.QEFXProjectController;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;

public class QEFXGraphNoteController extends QEFXAppController {

    private static final double INSETS_SIZE = 6.0;

    private static final double GRAPHIC_SIZE = 16.0;
    private static final String GRAPHIC_CLASS = "piclight-button";

    private static final String NOTE_CLASS = "result-graph-note";

    private QEFXProjectController projectController;

    private Node content;

    @FXML
    private Group baseGroup;

    @FXML
    private BorderPane basePane;

    @FXML
    private Button screenButton;

    @FXML
    private Button minButton;

    private Button maxButton;

    public QEFXGraphNoteController(QEFXProjectController projectController, Node content) {
        super(projectController == null ? null : projectController.getMainController());

        if (projectController == null) {
            throw new IllegalArgumentException("projectController is null.");
        }

        if (content == null) {
            throw new IllegalArgumentException("content is null.");
        }

        this.projectController = projectController;
        this.content = content;

        this.maxButton = null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupBaseGroup();
        this.setupBasePane();
        this.setupScreenButton();
        this.setupMinButton();
        this.setupMaxButton();
    }

    private void setupBaseGroup() {
        if (this.baseGroup == null) {
            return;
        }

        StackPane.setMargin(this.baseGroup, new Insets(INSETS_SIZE));
    }

    private void setupBasePane() {
        if (this.basePane == null) {
            return;
        }

        this.basePane.setCenter(this.content);
    }

    private void setupScreenButton() {
        if (this.screenButton == null) {
            return;
        }

        this.screenButton.setText("");
        this.screenButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.CAMERA, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.screenButton.setTooltip(new Tooltip("screen-shot"));

        this.screenButton.setOnAction(event -> {
            if (this.content != null) {
                this.projectController.sceenShot(this.content);
            }
        });
    }

    private void setupMinButton() {
        if (this.minButton == null) {
            return;
        }

        this.minButton.setText("");
        this.minButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.MINIMIZE, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.minButton.setTooltip(new Tooltip("minimize"));

        this.minButton.setOnAction(event -> {
            if (this.baseGroup != null && this.maxButton != null) {
                ToolBar toolBar = new ToolBar(this.maxButton);
                toolBar.getStyleClass().add(NOTE_CLASS);
                this.baseGroup.getChildren().clear();
                this.baseGroup.getChildren().add(toolBar);
            }
        });
    }

    private void setupMaxButton() {
        this.maxButton = new Button();
        this.maxButton.getStyleClass().add(GRAPHIC_CLASS);

        this.maxButton.setText("");
        this.maxButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.MAXIMIZE, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.maxButton.setTooltip(new Tooltip("maximize"));

        this.maxButton.setOnAction(event -> {
            if (this.baseGroup != null && this.basePane != null) {
                this.baseGroup.getChildren().clear();
                this.baseGroup.getChildren().add(this.basePane);
            }
        });
    }
}
