/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.band;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.graph.GraphProperty;
import burai.app.project.viewer.result.graph.QEFXGraphViewerController;
import burai.app.project.viewer.result.graph.SeriesProperty;
import burai.com.consts.Constants;
import burai.com.env.Environments;
import burai.com.parallel.Parallel;
import burai.project.property.BandData;
import burai.project.property.ProjectBand;
import burai.project.property.ProjectEnergies;

public class QEFXBandViewerController extends QEFXGraphViewerController {

    private static final int NUM_LOADING_THREADS = Math.max(1, Environments.getNumCUPs() - 1);

    private static final String XAXIS_CLASS = "invisible-axis";

    private ProjectEnergies projectEnergies;

    private ProjectBand projectBand;

    @FXML
    private AnchorPane coordPane;

    public QEFXBandViewerController(
            QEFXProjectController projectController, ProjectEnergies projectEnergies, ProjectBand projectBand) {

        super(projectController, null);

        if (projectEnergies == null) {
            throw new IllegalArgumentException("projectEnergies is null.");
        }

        if (projectBand == null) {
            throw new IllegalArgumentException("projectBand is null.");
        }

        this.projectEnergies = projectEnergies;
        this.projectBand = projectBand;
    }

    @Override
    protected GraphProperty createProperty() {
        GraphProperty property = new GraphProperty();

        property.setTitle("Band structure");
        property.setXLabel("");
        property.setYLabel("Energy / eV");

        BandData bandData1 = this.projectBand.getBandData(true);
        BandData bandData2 = this.projectBand.getBandData(false);

        if (bandData1 != null) {
            this.createBandProperty(property, bandData1, true);
        }

        if (bandData1 != null && bandData2 != null) {
            this.createBandProperty(property, bandData2, false);
        }

        return property;
    }

    private void createBandProperty(GraphProperty property, BandData bandData, boolean spin) {
        if (property == null) {
            return;
        }

        SeriesProperty seriesProperty = new SeriesProperty();
        seriesProperty.setName(spin ? "up spin" : "down spin");
        seriesProperty.setColor(spin ? "blue" : "red");
        seriesProperty.setDash(spin ? SeriesProperty.DASH_NULL : SeriesProperty.DASH_SMALL);
        seriesProperty.setWithSymbol(false);
        seriesProperty.setWidth(1.0);
        property.addSeries(seriesProperty);
    }

    @Override
    protected void reloadData(LineChart<Number, Number> lineChart) {
        if (lineChart == null) {
            return;
        }

        this.updateLineChart(lineChart);

        ProjectEnergies projectEnergies = this.projectEnergies.copyEnergies();
        if (projectEnergies == null || projectEnergies.numEnergies() < 1) {
            lineChart.getData().clear();
            return;
        }

        BandData bandData1 = this.projectBand.getBandData(true);
        BandData bandData2 = this.projectBand.getBandData(false);
        if (bandData1 == null) {
            lineChart.getData().clear();
            return;
        }

        lineChart.getData().clear();

        double fermi = projectEnergies.getEnergy(projectEnergies.numEnergies() - 1);

        if (bandData1 != null) {
            this.reloadBandData(lineChart, bandData1, bandData1, fermi);
        }

        if (bandData1 != null && bandData2 != null) {
            this.reloadBandData(lineChart, bandData1, bandData2, fermi);
        }
    }

    private void updateLineChart(LineChart<Number, Number> lineChart) {
        // set xAsis to be invisible
        Axis<Number> xAsis = lineChart == null ? null : lineChart.getXAxis();
        if (xAsis != null && !(xAsis.getStyleClass().contains(XAXIS_CLASS))) {
            xAsis.getStyleClass().add(XAXIS_CLASS);
        }

        // set label of coordinate
        if (this.coordPane != null) {
            this.coordPane.getChildren().add(new Label("test"));
        }
    }

    private void reloadBandData(
            LineChart<Number, Number> lineChart, BandData bandData1, BandData bandData2, double fermi) {

        if (lineChart == null) {
            return;
        }

        if (bandData1 == null || bandData2 == null) {
            return;
        }

        int numData = bandData1.numPoints();
        if (numData != bandData2.numPoints()) {
            return;
        }

        Series<Number, Number> series = new Series<Number, Number>();

        Platform.runLater(() -> {
            @SuppressWarnings("unchecked")
            Data<Number, Number>[] dataList = new Data[numData];

            Integer[] indexes = new Integer[numData];
            for (int i = 0; i < indexes.length; i++) {
                indexes[i] = i;
            }

            Parallel<Integer, Object> parallel = new Parallel<Integer, Object>(indexes);
            parallel.setNumThreads(NUM_LOADING_THREADS);
            parallel.forEach(i -> {
                double coord = bandData1.getCoordinate(i);
                double energy = bandData2.getEnergy(i) * Constants.RYTOEV - fermi;
                synchronized (dataList) {
                    dataList[i] = new Data<Number, Number>(coord, energy);
                }
                return null;
            });

            for (Data<Number, Number> data : dataList) {
                if (data != null) {
                    series.getData().add(data);
                }
            }
        });

        lineChart.getData().add(series);
    }
}
