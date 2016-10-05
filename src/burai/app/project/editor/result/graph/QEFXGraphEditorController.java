/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.result.graph;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import burai.app.project.QEFXProjectController;
import burai.app.project.editor.result.QEFXResultEditorController;
import burai.app.project.viewer.result.graph.GraphProperty;
import burai.app.project.viewer.result.graph.QEFXGraphViewerController;
import burai.app.project.viewer.result.graph.SeriesProperty;
import burai.com.graphic.ToggleGraphics;
import burai.com.math.Calculator;

public class QEFXGraphEditorController extends QEFXResultEditorController<QEFXGraphViewerController> {

    private static final String TOGGLE_STYLE = "-fx-base: transparent";
    private static final double TOGGLE_WIDTH = 94.0;
    private static final double TOGGLE_HEIGHT = 24.0;
    private static final String TOGGLE_TEXT_YES = "yes";
    private static final String TOGGLE_TEXT_NO = "no";
    private static final String TOGGLE_STYLE_YES = "toggle-graphic-on";
    private static final String TOGGLE_STYLE_NO = "toggle-graphic-off";

    protected GraphProperty property;

    @FXML
    private TextField titleField;

    @FXML
    private TextField xTitleField;

    @FXML
    private TextField xUpperField;

    @FXML
    private TextField xLowerField;

    @FXML
    private TextField xTickField;

    @FXML
    private TextField yTitleField;

    @FXML
    private TextField yUpperField;

    @FXML
    private TextField yLowerField;

    @FXML
    private TextField yTickField;

    @FXML
    private ComboBox<SeriesProperty> seriesCombo;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private ToggleButton symbolToggle;

    @FXML
    private ComboBox<Double> widthCombo;

    @FXML
    private ComboBox<Integer> typeCombo;

    public QEFXGraphEditorController(QEFXProjectController projectController, QEFXGraphViewerController viewerController) {
        super(projectController, viewerController);

        this.property = null;
        if (this.viewerController != null) {
            this.property = this.viewerController.getProperty();
        }
    }

    @Override
    protected void setupFXComponents() {
        this.setupTitleField();
        this.setupXField();
        this.setupYField();
        this.setupSeriesCombo();
        this.setupColorPicker();
        this.setupSymbolToggle();
        this.setupWidthCombo();
        this.setupTypeCombo();
    }

    private void setupTitleField() {
        if (this.titleField != null) {
            String title = this.property == null ? null : this.property.getTitle();
            if (title != null) {
                this.titleField.setText(title);
            } else {
                this.titleField.setText("");
            }

            this.titleField.textProperty().addListener(o -> {
                String text = this.titleField.getText();
                if (this.property != null) {
                    this.property.setTitle(text);
                }
                if (this.viewerController != null) {
                    this.viewerController.reloadProperty();
                }
            });
        }
    }

    private void setupXField() {
        if (this.xTitleField != null) {
            String title = this.property == null ? null : this.property.getXLabel();
            if (title != null) {
                this.xTitleField.setText(title);
            } else {
                this.xTitleField.setText("");
            }

            this.xTitleField.textProperty().addListener(o -> {
                String text = this.xTitleField.getText();
                if (this.property != null) {
                    this.property.setXLabel(text);
                }
                if (this.viewerController != null) {
                    this.viewerController.reloadProperty();
                }
            });
        }

        if (this.xUpperField != null) {
            if (this.property != null && (!property.isXAuto())) {
                double value = this.property.getXUpper();
                this.xUpperField.setText(Double.toString(value));
            } else {
                this.xUpperField.setText("");
            }

            this.xUpperField.setOnAction(event -> this.updateXUpperField());
            this.xUpperField.focusedProperty().addListener(o -> {
                if (!this.xUpperField.isFocused()) {
                    this.updateXUpperField();
                }
            });
        }

        if (this.xLowerField != null) {
            if (this.property != null && (!property.isXAuto())) {
                double value = this.property.getXLower();
                this.xLowerField.setText(Double.toString(value));
            } else {
                this.xLowerField.setText("");
            }

            this.xLowerField.setOnAction(event -> this.updateXLowerField());
            this.xLowerField.focusedProperty().addListener(o -> {
                if (!this.xLowerField.isFocused()) {
                    this.updateXLowerField();
                }
            });
        }

        if (this.xTickField != null) {
            double value = this.property == null ? -1.0 : this.property.getXTick();
            if (value > 0.0) {
                this.xTickField.setText(Double.toString(value));
            } else {
                this.xTickField.setText("");
            }

            this.xTickField.setOnAction(event -> this.updateXTickField());
            this.xTickField.focusedProperty().addListener(o -> {
                if (!this.xTickField.isFocused()) {
                    this.updateXTickField();
                }
            });
        }
    }

    private void setupYField() {
        if (this.yTitleField != null) {
            String title = this.property == null ? null : this.property.getYLabel();
            if (title != null) {
                this.yTitleField.setText(title);
            } else {
                this.yTitleField.setText("");
            }

            this.yTitleField.textProperty().addListener(o -> {
                String text = this.yTitleField.getText();
                if (this.property != null) {
                    this.property.setYLabel(text);
                }
                if (this.viewerController != null) {
                    this.viewerController.reloadProperty();
                }
            });
        }

        if (this.yUpperField != null) {
            if (this.property != null && (!property.isYAuto())) {
                double value = this.property.getYUpper();
                this.yUpperField.setText(Double.toString(value));
            } else {
                this.yUpperField.setText("");
            }

            this.yUpperField.setOnAction(event -> this.updateYUpperField());
            this.yUpperField.focusedProperty().addListener(o -> {
                if (!this.yUpperField.isFocused()) {
                    this.updateYUpperField();
                }
            });
        }

        if (this.yLowerField != null) {
            if (this.property != null && (!property.isYAuto())) {
                double value = this.property.getYLower();
                this.yLowerField.setText(Double.toString(value));
            } else {
                this.yLowerField.setText("");
            }

            this.yLowerField.setOnAction(event -> this.updateYLowerField());
            this.yLowerField.focusedProperty().addListener(o -> {
                if (!this.yLowerField.isFocused()) {
                    this.updateYLowerField();
                }
            });
        }

        if (this.yTickField != null) {
            double value = this.property == null ? -1.0 : this.property.getYTick();
            if (value > 0.0) {
                this.yTickField.setText(Double.toString(value));
            } else {
                this.yTickField.setText("");
            }

            this.yTickField.setOnAction(event -> this.updateYTickField());
            this.yTickField.focusedProperty().addListener(o -> {
                if (!this.yTickField.isFocused()) {
                    this.updateYTickField();
                }
            });
        }
    }

    private void updateXUpperField() {
        try {
            String text = this.xUpperField == null ? null : this.xUpperField.getText();
            text = text == null ? null : text.trim();
            if (text == null || text.isEmpty()) {
                if (this.property != null) {
                    this.property.setXUpper(0.0);
                }
                return;
            }

            double value = Calculator.expr(text);
            if (this.property != null) {
                this.property.setXUpper(value);
            }

        } catch (NumberFormatException e) {
            if (this.property != null) {
                this.property.setXUpper(0.0);
            }

        } finally {
            this.updateXAuto();
            if (this.viewerController != null) {
                this.viewerController.reloadProperty();
            }
        }
    }

    private void updateXLowerField() {
        try {
            String text = this.xLowerField == null ? null : this.xLowerField.getText();
            text = text == null ? null : text.trim();
            if (text == null || text.isEmpty()) {
                if (this.property != null) {
                    this.property.setXLower(0.0);
                }
                return;
            }

            double value = Calculator.expr(text);
            if (this.property != null) {
                this.property.setXLower(value);
            }

        } catch (NumberFormatException e) {
            if (this.property != null) {
                this.property.setXLower(0.0);
            }

        } finally {
            this.updateXAuto();
            if (this.viewerController != null) {
                this.viewerController.reloadProperty();
            }
        }
    }

    private void updateXTickField() {
        try {
            String text = this.xTickField == null ? null : this.xTickField.getText();
            text = text == null ? null : text.trim();
            if (text == null || text.isEmpty()) {
                if (this.property != null) {
                    this.property.setXTick(-1.0);
                }
                return;
            }

            double value = Calculator.expr(text);
            if (this.property != null) {
                this.property.setXTick(value);
            }

        } catch (NumberFormatException e) {
            if (this.property != null) {
                this.property.setXTick(-1.0);
            }

        } finally {
            this.updateXAuto();
            if (this.viewerController != null) {
                this.viewerController.reloadProperty();
            }
        }
    }

    private void updateYUpperField() {
        try {
            String text = this.yUpperField == null ? null : this.yUpperField.getText();
            text = text == null ? null : text.trim();
            if (text == null || text.isEmpty()) {
                if (this.property != null) {
                    this.property.setYUpper(0.0);
                }
                return;
            }

            double value = Calculator.expr(text);
            if (this.property != null) {
                this.property.setYUpper(value);
            }

        } catch (NumberFormatException e) {
            if (this.property != null) {
                this.property.setYUpper(0.0);
            }

        } finally {
            this.updateYAuto();
            if (this.viewerController != null) {
                this.viewerController.reloadProperty();
            }
        }
    }

    private void updateYLowerField() {
        try {
            String text = this.yLowerField == null ? null : this.yLowerField.getText();
            text = text == null ? null : text.trim();
            if (text == null || text.isEmpty()) {
                if (this.property != null) {
                    this.property.setYLower(0.0);
                }
                return;
            }

            double value = Calculator.expr(text);
            if (this.property != null) {
                this.property.setYLower(value);
            }

        } catch (NumberFormatException e) {
            if (this.property != null) {
                this.property.setYLower(0.0);
            }

        } finally {
            this.updateYAuto();
            if (this.viewerController != null) {
                this.viewerController.reloadProperty();
            }
        }
    }

    private void updateYTickField() {
        try {
            String text = this.yTickField == null ? null : this.yTickField.getText();
            text = text == null ? null : text.trim();
            if (text == null || text.isEmpty()) {
                if (this.property != null) {
                    this.property.setYTick(-1.0);
                }
                return;
            }

            double value = Calculator.expr(text);
            if (this.property != null) {
                this.property.setYTick(value);
            }

        } catch (NumberFormatException e) {
            if (this.property != null) {
                this.property.setYTick(-1.0);
            }

        } finally {
            this.updateYAuto();
            if (this.viewerController != null) {
                this.viewerController.reloadProperty();
            }
        }
    }

    private void updateXAuto() {
        if (this.isToBeAuto(this.xUpperField, this.xLowerField)) {
            if (this.property != null) {
                this.property.setXAuto(true);
            }
        } else {
            if (this.property != null) {
                this.property.setXAuto(false);
            }
        }
    }

    private void updateYAuto() {
        if (this.isToBeAuto(this.yUpperField, this.yLowerField)) {
            if (this.property != null) {
                this.property.setYAuto(true);
            }
        } else {
            if (this.property != null) {
                this.property.setYAuto(false);
            }
        }
    }

    private boolean isToBeAuto(TextField upperField, TextField lowerField) {
        if (upperField == null) {
            return true;
        }
        if (lowerField == null) {
            return true;
        }

        String strUpper = upperField.getText();
        if (strUpper == null || strUpper.isEmpty()) {
            return true;
        }

        String strLower = lowerField.getText();
        if (strLower == null || strLower.isEmpty()) {
            return true;
        }

        double upper = 0.0;
        try {
            upper = Calculator.expr(strUpper);
        } catch (NumberFormatException e) {
            return true;
        }

        double lower = 0.0;
        try {
            lower = Calculator.expr(strLower);
        } catch (NumberFormatException e) {
            return true;
        }

        if ((upper - lower) <= 0.0) {
            return true;
        }

        return false;
    }

    private void setupSeriesCombo() {
        if (this.seriesCombo == null) {
            return;
        }

        this.seriesCombo.getItems().clear();

        if (this.property != null) {
            for (int i = 0; i < this.property.numSeries(); i++) {
                SeriesProperty seriesProperty = this.property.getSeries(i);
                if (seriesProperty != null) {
                    this.seriesCombo.getItems().add(seriesProperty);
                }
            }
        }

        SeriesProperty seriesProperty = null;
        if (this.property != null && this.property.numSeries() > 0) {
            seriesProperty = this.property.getSeries(0);
        }
        if (seriesProperty != null) {
            this.seriesCombo.setValue(seriesProperty);
        }

        this.seriesCombo.setOnAction(event -> {
            this.initColorPicker();
            this.initSymbolToggle();
            this.initWidthCombo();
            this.initTypeCombo();
        });
    }

    private void setupColorPicker() {
        if (this.colorPicker == null) {
            return;
        }

        this.initColorPicker();

        this.colorPicker.setOnAction(event -> {
            Color color = this.colorPicker.getValue();
            String strColor = color == null ? null : color.toString();
            strColor = strColor == null ? null : strColor.replaceAll("0x", "#");
            if (strColor != null) {
                SeriesProperty seriesProperty = this.seriesCombo == null ? null : this.seriesCombo.getValue();
                if (seriesProperty != null) {
                    seriesProperty.setColor(strColor);
                }
            }

            if (this.viewerController != null) {
                this.viewerController.reloadProperty();
            }
        });
    }

    private void initColorPicker() {
        if (this.colorPicker == null) {
            return;
        }

        SeriesProperty seriesProperty = this.seriesCombo == null ? null : this.seriesCombo.getValue();
        String strColor = seriesProperty == null ? null : seriesProperty.getColor();
        if (strColor != null && (!strColor.trim().isEmpty())) {
            try {
                this.colorPicker.setValue(Color.valueOf(strColor));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupSymbolToggle() {
        if (this.symbolToggle == null) {
            return;
        }

        this.symbolToggle.setText("");
        this.symbolToggle.setStyle(TOGGLE_STYLE);

        this.initSymbolToggle();

        this.symbolToggle.setOnAction(event -> {
            SeriesProperty seriesProperty = this.seriesCombo == null ? null : this.seriesCombo.getValue();
            if (seriesProperty != null) {
                seriesProperty.setWithSymbol(this.symbolToggle.isSelected());
            }

            this.redrawSymbolToggle();

            if (this.viewerController != null) {
                this.viewerController.reloadProperty();
            }
        });
    }

    private void initSymbolToggle() {
        if (this.symbolToggle == null) {
            return;
        }

        SeriesProperty seriesProperty = this.seriesCombo == null ? null : this.seriesCombo.getValue();
        if (seriesProperty != null) {
            this.symbolToggle.setSelected(seriesProperty.isWithSymbol());
        }

        this.redrawSymbolToggle();
    }

    private void redrawSymbolToggle() {
        if (this.symbolToggle == null) {
            return;
        }

        if (this.symbolToggle.isSelected()) {
            this.symbolToggle.setGraphic(ToggleGraphics.getGraphic(
                    TOGGLE_WIDTH, TOGGLE_HEIGHT, true, TOGGLE_TEXT_YES, TOGGLE_STYLE_YES));
        } else {
            this.symbolToggle.setGraphic(ToggleGraphics.getGraphic(
                    TOGGLE_WIDTH, TOGGLE_HEIGHT, false, TOGGLE_TEXT_NO, TOGGLE_STYLE_NO));
        }
    }

    private void setupWidthCombo() {
        if (this.widthCombo == null) {
            return;
        }

        this.widthCombo.getItems().clear();
        for (int i = 1; i <= 6; i++) {
            this.widthCombo.getItems().add(0.5 * ((double) i));
        }

        this.widthCombo.setButtonCell(new WidthCell());
        this.widthCombo.setCellFactory(listView -> {
            return new WidthCell();
        });

        this.initWidthCombo();

        this.widthCombo.setOnAction(event -> {
            Double width = this.widthCombo.getValue();
            if (width != null) {
                SeriesProperty seriesProperty = this.seriesCombo == null ? null : this.seriesCombo.getValue();
                if (seriesProperty != null) {
                    seriesProperty.setWidth(width);
                }
            }

            if (this.viewerController != null) {
                this.viewerController.reloadProperty();
            }
        });
    }

    private void initWidthCombo() {
        if (this.widthCombo == null) {
            return;
        }

        SeriesProperty seriesProperty = this.seriesCombo == null ? null : this.seriesCombo.getValue();
        if (seriesProperty != null) {
            this.widthCombo.setValue(seriesProperty.getWidth());
        }
    }

    private static class WidthCell extends ListCell<Double> {

        private static final double LINE_SIZE = 36.0;

        public WidthCell() {
            // NOP
        }

        @Override
        protected void updateItem(Double value, boolean empty) {
            super.updateItem(value, empty);
            if (value == null || empty) {
                this.setGraphic(null);
                return;
            }

            Line line = new Line(0.0, 0.0, LINE_SIZE, 0.0);
            BorderPane.setAlignment(line, Pos.CENTER);
            line.setStyle("-fx-stroke: black; -fx-stroke-width: " + value + "px");

            Label label = new Label(" " + value + "px ");
            BorderPane.setAlignment(label, Pos.CENTER_LEFT);
            label.setStyle("-fx-text-fill: black");

            BorderPane pane = new BorderPane();
            pane.setCenter(line);
            pane.setRight(label);
            this.setGraphic(pane);
        }
    }

    private void setupTypeCombo() {
        if (this.typeCombo == null) {
            return;
        }

        this.typeCombo.getItems().clear();
        this.typeCombo.getItems().add(SeriesProperty.DASH_NULL);
        this.typeCombo.getItems().add(SeriesProperty.DASH_SMALL);
        this.typeCombo.getItems().add(SeriesProperty.DASH_LARGE);
        this.typeCombo.getItems().add(SeriesProperty.DASH_HYBRID);

        this.typeCombo.setButtonCell(new TypeCell());
        this.typeCombo.setCellFactory(listView -> {
            return new TypeCell();
        });

        this.initTypeCombo();

        this.typeCombo.setOnAction(event -> {
            Integer dash = this.typeCombo.getValue();
            if (dash != null) {
                SeriesProperty seriesProperty = this.seriesCombo == null ? null : this.seriesCombo.getValue();
                if (seriesProperty != null) {
                    seriesProperty.setDash(dash);
                }
            }

            if (this.viewerController != null) {
                this.viewerController.reloadProperty();
            }
        });
    }

    private void initTypeCombo() {
        if (this.typeCombo == null) {
            return;
        }

        SeriesProperty seriesProperty = this.seriesCombo == null ? null : this.seriesCombo.getValue();
        if (seriesProperty != null) {
            this.typeCombo.setValue(seriesProperty.getDash());
        }
    }

    private static class TypeCell extends ListCell<Integer> {

        private static final double LINE_SIZE = 60.0;
        private static final double LINE_WIDTH = 1.0;

        public TypeCell() {
            // NOP
        }

        @Override
        protected void updateItem(Integer value, boolean empty) {
            super.updateItem(value, empty);
            if (value == null || empty) {
                this.setGraphic(null);
                return;
            }

            String strokeStyle = "-fx-stroke: black; -fx-stroke-width: " + LINE_WIDTH + "px;";
            String dashStyle = SeriesProperty.getDashStyle(value);

            Line line = new Line(0.0, 0.0, LINE_SIZE, 0.0);
            BorderPane.setAlignment(line, Pos.CENTER);
            if (dashStyle != null) {
                line.setStyle(strokeStyle + "-fx-stroke-dash-array: " + dashStyle);
            } else {
                line.setStyle(strokeStyle);
            }

            BorderPane pane = new BorderPane();
            pane.setCenter(line);
            this.setGraphic(pane);
        }
    }
}
