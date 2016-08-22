/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.icon;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import burai.project.Project;
import burai.run.RunningNode;
import burai.run.RunningStatus;
import burai.run.RunningStatusChanged;
import burai.run.RunningType;

public class QEFXRunningIcon extends QEFXProjectIcon implements RunningStatusChanged {

    private static final String LABEL_TEXT_CLASS = "icon-run-text";
    private static final String LABEL_BASE_CLASS = "icon-run-base";
    private static final double LABEL_X_SIZE = 0.45;
    private static final double LABEL_Y_SIZE = 0.32 * LABEL_X_SIZE;
    private static final double LABEL_INSET = 0.33 * LABEL_Y_SIZE;
    private static final double LABEL_FONT_SIZE = 0.076;

    private double labelFontSize;

    private Label labelText;

    private BorderPane labelBase;

    private RunningNode runningNode;

    public QEFXRunningIcon(Project project, RunningNode runningNode) {
        super(project);

        if (runningNode == null) {
            throw new IllegalArgumentException("runningNode is null.");
        }

        this.labelFontSize = 0.0;
        this.labelText = null;
        this.labelBase = null;
        this.runningNode = runningNode;
        this.runningNode.addOnStatusChanged(this);
    }

    public RunningNode getRunningNode() {
        return this.runningNode;
    }

    @Override
    public void detach() {
        super.detach();

        this.runningNode.removeOnStatusChanged(this);

        this.labelText = null;
        this.labelBase = null;
        this.runningNode = null;
    }

    @Override
    public Node getFigure(double size) {
        if (size <= 0.0) {
            return null;
        }

        StackPane stackPane = new StackPane();

        Node figure = super.getFigure(size);
        if (figure != null) {
            StackPane.setAlignment(figure, Pos.CENTER);
            stackPane.getChildren().add(figure);
        }

        Node label = this.getFigureLabel(size);
        if (label != null) {
            StackPane.setAlignment(label, Pos.BOTTOM_RIGHT);
            stackPane.getChildren().add(label);
        }

        if (stackPane.getChildren().isEmpty()) {
            return null;
        }

        return stackPane;
    }

    private Node getFigureLabel(double size) {
        if (size <= 0.0) {
            return null;
        }

        double xSize = LABEL_X_SIZE * size;
        double ySize = LABEL_Y_SIZE * size;
        double insetSize = LABEL_INSET * size;
        double fontSize = LABEL_FONT_SIZE * size;

        RunningType runningType = this.runningNode.getType();
        String runningName = runningType == null ? null : runningType.toString();
        if (runningName == null || runningName.isEmpty()) {
            return null;
        }

        this.labelFontSize = fontSize;

        this.labelText = new Label();
        this.labelText.getStyleClass().add(LABEL_TEXT_CLASS);
        this.labelText.setText(runningName);

        this.labelBase = new BorderPane();
        this.labelBase.getStyleClass().add(LABEL_BASE_CLASS);
        this.labelBase.setPrefWidth(xSize);
        this.labelBase.setPrefHeight(ySize);

        BorderPane.setAlignment(this.labelText, Pos.CENTER);
        this.labelBase.setCenter(this.labelText);
        this.updateLabelStyle();

        Group group = new Group(this.labelBase);
        StackPane.setMargin(group, new Insets(insetSize));
        return group;
    }

    private void updateLabelStyle() {
        if (this.labelFontSize <= 0.0 || this.labelText == null || this.labelBase == null) {
            return;
        }

        String strFontSize = "-fx-font-size: " + this.labelFontSize + "; ";
        RunningStatus runningStatus = this.runningNode.getStatus();

        if (RunningStatus.IDLE.equals(runningStatus)) {
            this.labelText.setStyle(strFontSize + "-fx-text-fill: derive(lightgray, 20.0%)");
            this.labelBase.setStyle("-fx-background-color: dimgray");

        } else if (RunningStatus.QUEUED.equals(runningStatus)) {
            this.labelText.setStyle(strFontSize + "-fx-text-fill: derive(black, 20.0%)");
            this.labelBase.setStyle("-fx-background-color: derive(yellow, 5.0%)");

        } else if (RunningStatus.RUNNING.equals(runningStatus)) {
            this.labelText.setStyle(strFontSize + "-fx-text-fill: derive(lightgray, 20.0%)");
            this.labelBase.setStyle("-fx-background-color: derive(red, -10.0%)");

        } else if (RunningStatus.DONE.equals(runningStatus)) {
            this.labelText.setStyle(strFontSize + "-fx-text-fill: derive(black, 20.0%)");
            this.labelBase.setStyle("-fx-background-color: derive(lime, 10.0%)");

        } else {
            this.labelText.setStyle(strFontSize + "-fx-text-fill: derive(lightgray, 20.0%)");
            this.labelBase.setStyle("-fx-background-color: dimgray");
        }
    }

    @Override
    protected String initSubCaption() {
        String caption = super.initSubCaption();

        RunningStatus runningStatus = this.runningNode.getStatus();
        if (runningStatus == null) {
            return caption;
        }

        RunningType runningType = this.runningNode.getType();
        String runningName = runningType == null ? null : runningType.toString();
        if (runningName == null || runningName.isEmpty()) {
            return caption;
        }

        int numProcess = this.runningNode.getNumProcesses();
        if (numProcess < 1) {
            return caption;
        }

        int numThread = this.runningNode.getNumThreads();
        if (numThread < 1) {
            return caption;
        }

        String runningCaption = "Calculation is " + runningStatus + ". ( ";
        runningCaption = runningCaption + "Job: " + runningName + ", ";
        runningCaption = runningCaption + "#process: " + numProcess + ", ";
        runningCaption = runningCaption + "#thread: " + numThread + " )";

        caption = caption + System.lineSeparator() + runningCaption;
        return caption;
    }

    @Override
    public void onRunningStatusChanged(RunningStatus runningStatus) {
        Platform.runLater(() -> {
            this.updateLabelStyle();

            StringProperty caption = this.subCaptionProperty();
            caption.set(this.initSubCaption());
        });
    }

}
