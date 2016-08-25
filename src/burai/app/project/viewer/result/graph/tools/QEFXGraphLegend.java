/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.graph.tools;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;
import burai.app.project.viewer.result.graph.GraphProperty;
import burai.app.project.viewer.result.graph.SeriesProperty;

public class QEFXGraphLegend {

    private static final double LINE_SIZE = 100.0;

    private static final String LEGEND_CLASS = "result-graph-legend";
    private static final String TEXT_CLASS = "result-graph-legend-text";

    private GraphProperty property;

    private GridPane basePane;

    public QEFXGraphLegend(GraphProperty property) {
        if (property == null) {
            throw new IllegalArgumentException("property is null.");
        }

        this.property = property;

        this.basePane = null;
        this.setupBasePane();
    }

    public Node getNode() {
        return this.basePane;
    }

    private void setupBasePane() {
        int numSeries = this.property.numSeries();
        if (numSeries < 1) {
            return;
        }

        this.basePane = new GridPane();
        this.basePane.getStyleClass().add(LEGEND_CLASS);

        for (int iSeries = 0; iSeries < numSeries; iSeries++) {
            SeriesProperty seriesProperty = this.property.getSeries(iSeries);
            if (seriesProperty != null) {
                this.setLegendItem(iSeries, seriesProperty);
            }
        }
    }

    private void setLegendItem(int i, SeriesProperty seriesProperty) {
        if (seriesProperty == null) {
            return;
        }

        String name = seriesProperty.getName();
        if (name == null || name.isEmpty()) {
            name = "NO NAME";
        }

        String color = seriesProperty.getColor();
        if (color == null || color.isEmpty()) {
            color = "black";
        }

        double width = seriesProperty.getWidth();
        if (width <= 0.0) {
            width = 1.0;
        }

        int dash = seriesProperty.getDash();
        String strDash = SeriesProperty.getDashStyle(dash);
        if (strDash == null || strDash.isEmpty()) {
            strDash = SeriesProperty.getDashStyle(SeriesProperty.DASH_NULL);
        }

        String styleColor = "-fx-stroke: " + color + ";";
        String styleWidth = "-fx-stroke-width: " + width + "px;";
        String styleDash = "-fx-stroke-dash-array: " + strDash + ";";

        Line line = new Line(0.0, 0.0, LINE_SIZE, 0.0);
        line.setStyle(styleColor + styleWidth + styleDash);
        BorderPane.setAlignment(line, Pos.CENTER);
        BorderPane linePane = new BorderPane(line);
        this.basePane.add(linePane, 0, i);

        Label label = new Label(name);
        label.getStyleClass().add(TEXT_CLASS);
        BorderPane.setAlignment(label, Pos.CENTER);
        BorderPane labelPane = new BorderPane(label);
        this.basePane.add(labelPane, 1, i);
    }
}
