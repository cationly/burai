/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.graph;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import burai.app.project.viewer.result.graph.tools.QEFXGraphLegend;
import burai.app.project.viewer.result.graph.tools.QEFXGraphNote;

public abstract class QEFXGraphViewerController extends QEFXResultViewerController {

    private static final double NOTE_SPACING = 4.0;
    private static final double NOTE_INSET1 = 8.0;
    private static final double NOTE_INSET2 = 20.0;
    private static final String NOTE_CLASS = "result-graph-note-text";

    private static final int NUM_POST_RELOADS = 4;
    private static final long SLEEP_BETWEEN_RELOADS = 500L;

    private LineChart<Number, Number> lineChart;

    private GraphProperty property;
    private File propertyFile;

    private Pos legendPos;
    private QEFXGraphLegend legendObj;

    @FXML
    private BorderPane basePane;

    public QEFXGraphViewerController(QEFXProjectController projectController, Pos legendPos) {
        super(projectController);

        this.lineChart = null;

        this.property = null;
        this.propertyFile = null;

        this.legendPos = legendPos;
        this.legendObj = null;
    }

    public void setPropertyFile(File propertyFile) {
        this.propertyFile = propertyFile;
    }

    protected abstract GraphProperty createProperty();

    public GraphProperty getProperty() {
        if (this.property == null) {
            this.property = this.createProperty();

            if (this.property != null && this.propertyFile != null) {
                try {
                    this.property.readFile(this.propertyFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return this.property;
    }

    private void clearStackedNodes() {
        this.legendObj = null;

        if (this.projectController != null) {
            this.projectController.clearStackedsOnViewerPane();
        }
    }

    protected void stackNode(Node node, Pos pos) {
        if (node == null) {
            return;
        }

        if (this.projectController != null) {
            try {
                QEFXGraphNote note = new QEFXGraphNote(this.projectController, node);
                Node noteNode = note.getNode();
                if (noteNode != null) {
                    if (pos != null) {
                        StackPane.setAlignment(noteNode, pos);
                    }
                    this.projectController.stackOnViewerPane(noteNode);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
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

        BorderPane.setMargin(vbox, new Insets(NOTE_INSET1, NOTE_INSET2, NOTE_INSET1, NOTE_INSET2));
        BorderPane pane = new BorderPane(vbox);
        pane.getStyleClass().add(NOTE_CLASS);
        return pane;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupLineChart();
        this.setupBasePane();
    }

    private void setupBasePane() {
        if (this.basePane == null) {
            return;
        }

        if (this.lineChart != null) {
            this.basePane.setCenter(this.lineChart);
        }
    }

    private void setupLineChart() {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setForceZeroInRange(false);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(false);

        this.lineChart = new LineChart<Number, Number>(xAxis, yAxis);
        this.lineChart.setAxisSortingPolicy(SortingPolicy.NONE);
    }

    @Override
    public void reload() {
        this.clearStackedNodes();
        this.reloadData();
        this.reloadProperty();

        Thread thread = new Thread(() -> {
            this.postReload();
        });

        thread.start();
    }

    private void postReload() {
        for (int i = 0; i < NUM_POST_RELOADS; i++) {
            synchronized (this) {
                try {
                    this.wait(SLEEP_BETWEEN_RELOADS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Platform.runLater(() -> {
                this.reloadProperty();
            });
        }
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
        this.lineChart.setAxisSortingPolicy(SortingPolicy.NONE);

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

        this.reloadLegend();

        if (this.property != null && this.propertyFile != null) {
            try {
                this.property.writeFile(this.propertyFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void reloadLegend() {
        if (this.legendPos == null) {
            return;
        }

        GraphProperty property = this.getProperty();
        if (property == null) {
            return;
        }

        if (this.legendObj == null) {
            this.legendObj = new QEFXGraphLegend(property);
            Node legendNode = this.legendObj.getNode();
            if (legendNode != null) {
                this.stackNode(legendNode, this.legendPos);
            }
        }

        this.legendObj.reload();
    }
}
