/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.graph;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.LineChart.SortingPolicy;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.QEFXResultViewerController;

public abstract class QEFXGraphViewerController extends QEFXResultViewerController {

    private static final String NOTE_CLASS = "result-graph-note";
    private static final double NOTE_SPACING = 4.0;
    private static final double NOTE_INSET1 = 8.0;
    private static final double NOTE_INSET2 = 20.0;

    private StackPane stackPane;

    private LineChart<Number, Number> lineChart;

    private GraphProperty property;

    @FXML
    private BorderPane basePane;

    public QEFXGraphViewerController(QEFXProjectController projectController) {
        super(projectController);

        this.stackPane = null;
        this.lineChart = null;
        this.property = null;
    }

    protected abstract GraphProperty createProperty();

    public GraphProperty getProperty() {
        if (this.property == null) {
            this.property = this.createProperty();
        }

        return this.property;
    }

    protected void clearStackedNodes() {
        if (this.stackPane != null) {
            this.stackPane.getChildren().clear();
            if (this.lineChart != null) {
                this.stackPane.getChildren().add(this.lineChart);
            }
        }
    }

    protected void stackNode(Node node, Pos pos) {
        if (node == null) {
            return;
        }

        if (pos != null) {
            StackPane.setAlignment(node, pos);
        }

        if (this.stackPane != null) {
            this.stackPane.getChildren().add(node);
        }
    }

    protected Node getNote(String... texts) {
        VBox vbox = new VBox();
        vbox.setSpacing(NOTE_SPACING);
        if (texts != null) {
            for (String text : texts) {
                if (text != null) {
                    vbox.getChildren().add(new Label(text));
                }
            }
        }

        BorderPane pane = new BorderPane();
        pane.getStyleClass().add(NOTE_CLASS);

        BorderPane.setMargin(vbox, new Insets(NOTE_INSET1, NOTE_INSET2, NOTE_INSET1, NOTE_INSET2));
        pane.setCenter(vbox);

        return new Group(pane);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupLineChart();
        this.setupStackPane();
        this.setupBasePane();
        this.reload();
    }

    private void setupBasePane() {
        if (this.basePane == null) {
            return;
        }

        if (this.stackPane != null) {
            this.basePane.setCenter(this.stackPane);
        }
    }

    private void setupStackPane() {
        this.stackPane = new StackPane();

        if (this.lineChart != null) {
            this.stackPane.getChildren().add(this.lineChart);
        }
    }

    private void setupLineChart() {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setForceZeroInRange(false);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(false);

        this.lineChart = new LineChart<Number, Number>(xAxis, yAxis);
        this.lineChart.setAxisSortingPolicy(SortingPolicy.X_AXIS);
    }

    @Override
    public void reload() {
        this.reloadData();
        this.reloadProperty();
    }

    protected abstract void reloadData(LineChart<Number, Number> lineChart);

    public final void reloadData() {
        this.reloadData(this.lineChart);
    }

    public final void reloadProperty() {
        if (this.lineChart == null) {
            return;
        }

        GraphProperty property = this.getProperty();
        if (property == null) {
            return;
        }

        String title = property.getTitle();
        title = title == null ? "" : title;
        this.lineChart.setTitle(title);

        Axis<Number> xAxis = this.lineChart.getXAxis();
        String xLabel = property.getXLabel();
        xLabel = xLabel == null ? "" : xLabel;
        xAxis.setLabel(xLabel);
        xAxis.setAutoRanging(property.isXAuto());
        if (xAxis instanceof NumberAxis) {
            ((NumberAxis) xAxis).setLowerBound(property.getXLower());
            ((NumberAxis) xAxis).setUpperBound(property.getXUpper());
            double tick = property.getXTick();
            if (tick > 0.0) {
                ((NumberAxis) xAxis).setTickUnit(tick);
            }
        }

        Axis<Number> yAxis = this.lineChart.getYAxis();
        String yLabel = property.getYLabel();
        yLabel = yLabel == null ? "" : yLabel;
        yAxis.setLabel(yLabel);
        yAxis.setAutoRanging(property.isYAuto());
        if (yAxis instanceof NumberAxis) {
            ((NumberAxis) yAxis).setLowerBound(property.getYLower());
            ((NumberAxis) yAxis).setUpperBound(property.getYUpper());
            double tick = property.getYTick();
            if (tick > 0.0) {
                ((NumberAxis) yAxis).setTickUnit(tick);
            }
        }

        int numSeries = property.numSeries();
        for (int i = 0; i < numSeries; i++) {
            SeriesProperty seriesProperty = property.getSeries(i);
            if (seriesProperty == null) {
                continue;
            }

            Series<Number, Number> series = null;
            if (i < this.lineChart.getData().size()) {
                series = this.lineChart.getData().get(i);
            }
            if (series != null) {
                String name = seriesProperty.getName();
                name = name == null ? "" : name;
                series.setName(name);
            }

            Node lineNode = this.lineChart.lookup(".default-color" + i + ".chart-series-line");
            if (lineNode != null) {
                String color = seriesProperty.getColor();
                color = color == null ? null : color.trim();
                String styleColor = null;
                if (color != null && (!color.isEmpty())) {
                    styleColor = "-fx-stroke:" + color.trim();
                }

                double width = seriesProperty.getWidth();
                String styleWidth = null;
                if (width > 0.0) {
                    styleWidth = "-fx-stroke-width:" + width + "px";
                }

                String dash = seriesProperty.getDashStyle();
                dash = dash == null ? null : dash.trim();
                String styleDash = null;
                if (dash != null && (!dash.isEmpty())) {
                    styleDash = "-fx-stroke-dash-array:" + dash;
                }

                String style = null;

                if (styleColor != null) {
                    style = styleColor;
                }
                if (styleWidth != null) {
                    if (style != null) {
                        style = style + ";" + styleWidth;
                    } else {
                        style = styleWidth;
                    }
                }
                if (styleDash != null) {
                    if (style != null) {
                        style = style + ";" + styleDash;
                    } else {
                        style = styleDash;
                    }
                }

                lineNode.setStyle(style);
            }

            Set<Node> symbolNodes = this.lineChart.lookupAll(".default-color" + i + ".chart-line-symbol");
            if (symbolNodes != null) {
                if (seriesProperty.isWithSymbol()) {
                    String color = seriesProperty.getColor();
                    color = color == null ? null : color.trim();
                    if (color != null && (!color.isEmpty())) {
                        for (Node symbolNode : symbolNodes) {
                            symbolNode.setStyle("-fx-background-color: " + color + ", white");
                        }
                    }

                } else {
                    for (Node symbolNode : symbolNodes) {
                        symbolNode.setStyle("-fx-background-color: transparent, transparent");
                    }
                }
            }
        }
    }
}
