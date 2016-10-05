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
import burai.project.property.ProjectBandPaths;
import burai.project.property.ProjectEnergies;

public class QEFXBandViewerController extends QEFXGraphViewerController {

    private static final int NUM_LOADING_THREADS = Math.max(1, Environments.getNumCUPs() - 1);

    private static final String XAXIS_CLASS = "invisible-axis";

    private static final double DELTA_COORD = 0.1;
    private static final double DELTA_ENERGY = 0.1;
    private static final double VLINE_BUFFER = 1.0; // eV

    private ProjectEnergies projectEnergies;

    private ProjectBand projectBand;

    private ProjectBandPaths projectBandPaths;

    @FXML
    private AnchorPane coordPane;

    public QEFXBandViewerController(
            QEFXProjectController projectController,
            ProjectEnergies projectEnergies, ProjectBand projectBand, ProjectBandPaths projectBandPaths) {

        super(projectController, null);

        if (projectEnergies == null) {
            throw new IllegalArgumentException("projectEnergies is null.");
        }

        if (projectBand == null) {
            throw new IllegalArgumentException("projectBand is null.");
        }

        if (projectBandPaths == null) {
            throw new IllegalArgumentException("projectBandPaths is null.");
        }

        this.projectEnergies = projectEnergies;
        this.projectBand = projectBand;
        this.projectBandPaths = projectBandPaths;
    }

    @Override
    protected GraphProperty createProperty() {
        ProjectEnergies projectEnergies = this.projectEnergies.copyEnergies();
        ProjectBandPaths projectBandPaths = this.projectBandPaths.copyBandPaths();
        BandData bandData1 = this.projectBand.getBandData(true);
        BandData bandData2 = this.projectBand.getBandData(false);

        GraphProperty property = new GraphProperty();

        property.setTitle("Band structure");
        property.setXLabel("");
        property.setYLabel("Energy / eV");

        if (bandData1 != null) {
            this.createBandProperty(property, true);
        }

        if (bandData1 != null && bandData2 != null) {
            this.createBandProperty(property, false);
        }

        if (projectBandPaths != null && projectBandPaths.numPoints() > 0) {
            this.createCoordProperty(property, projectBandPaths);
        }

        if (projectEnergies != null && projectEnergies.numEnergies() > 0) {
            double fermi = projectEnergies.getEnergy(projectEnergies.numEnergies() - 1);
            this.createEnergyProperty(property, bandData1, bandData2, fermi);
        }

        return property;
    }

    private void createBandProperty(GraphProperty property, boolean spin) {
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

    private void createCoordProperty(GraphProperty property, ProjectBandPaths projectBandPaths) {
        if (property == null) {
            return;
        }

        if (projectBandPaths == null || projectBandPaths.numPoints() < 1) {
            return;
        }

        double minCoord = Double.MAX_VALUE;
        double maxCoord = 0.0;

        double coordOld = -1.0;
        for (int i = 0; i < projectBandPaths.numPoints(); i++) {
            double coord = projectBandPaths.getCoordinate(i);
            if (Math.abs(coord - coordOld) < DELTA_COORD) {
                continue;
            }
            coordOld = coord;

            minCoord = Math.min(minCoord, coord);
            maxCoord = Math.max(maxCoord, coord);
        }

        if ((maxCoord - minCoord) >= DELTA_COORD) {
            property.setXAuto(false);
            property.setXLower(minCoord);
            property.setXUpper(maxCoord);
        }
    }

    private void createEnergyProperty(GraphProperty property, BandData bandData1, BandData bandData2, double fermi) {
        if (property == null) {
            return;
        }

        double[] energyRange = this.getEnergyRange(bandData1, bandData2, fermi);
        if (energyRange == null || energyRange.length < 2) {
            return;
        }

        double minEnergy = energyRange[0];
        double maxEnergy = energyRange[1];

        if ((maxEnergy - minEnergy) >= DELTA_ENERGY) {
            property.setYAuto(false);
            property.setYLower(minEnergy);
            property.setYUpper(maxEnergy);
        }
    }

    @Override
    protected void reloadData(LineChart<Number, Number> lineChart) {
        if (lineChart == null) {
            return;
        }

        lineChart.getData().clear();
        this.updateLineChart(lineChart);

        ProjectEnergies projectEnergies = this.projectEnergies.copyEnergies();
        ProjectBandPaths projectBandPaths = this.projectBandPaths.copyBandPaths();
        BandData bandData1 = this.projectBand.getBandData(true);
        BandData bandData2 = this.projectBand.getBandData(false);

        if (projectEnergies == null || projectEnergies.numEnergies() < 1) {
            return;
        }

        double fermi = projectEnergies.getEnergy(projectEnergies.numEnergies() - 1);

        if (bandData1 != null) {
            this.reloadBandData(lineChart, bandData1, bandData1, fermi);
        }

        if (bandData1 != null && bandData2 != null) {
            this.reloadBandData(lineChart, bandData1, bandData2, fermi);
        }

        if (projectBandPaths != null && projectBandPaths.numPoints() > 0) {
            this.reloadVLine(lineChart, projectBandPaths, bandData1, bandData2, fermi);
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
            this.coordPane.getChildren().clear();
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

    private void reloadVLine(LineChart<Number, Number> lineChart,
            ProjectBandPaths projectBandPaths, BandData bandData1, BandData bandData2, double fermi) {

        if (lineChart == null) {
            return;
        }

        if (projectBandPaths == null || projectBandPaths.numPoints() < 1) {
            return;
        }

        double[] energyRange = this.getEnergyRange(bandData1, bandData2, fermi);
        if (energyRange == null || energyRange.length < 2) {
            return;
        }

        double minEnergy = energyRange[0];
        double maxEnergy = energyRange[1];
        if ((maxEnergy - minEnergy) < DELTA_ENERGY) {
            return;
        }

        double coordOld = -1.0;
        for (int i = 0; i < projectBandPaths.numPoints(); i++) {
            double coord = projectBandPaths.getCoordinate(i);
            if (Math.abs(coord - coordOld) < DELTA_COORD) {
                continue;
            }
            coordOld = coord;

            Data<Number, Number> data1 = new Data<Number, Number>(coord, minEnergy);
            Data<Number, Number> data2 = new Data<Number, Number>(coord, maxEnergy);
            Series<Number, Number> series = new Series<Number, Number>();
            series.getData().add(data1);
            series.getData().add(data2);
            lineChart.getData().add(series);
        }
    }

    private double[] getEnergyRange(BandData bandData1, BandData bandData2, double fermi) {
        boolean empty1 = (bandData1 == null || bandData1.numPoints() < 1);
        boolean empty2 = (bandData2 == null || bandData2.numPoints() < 1);
        if (empty1 && empty2) {
            return null;
        }

        double minEnergy = +Double.MAX_VALUE;
        double maxEnergy = -Double.MAX_VALUE;

        if (!empty1) {
            for (int i = 0; i < bandData1.numPoints(); i++) {
                double energy = bandData1.getEnergy(i) * Constants.RYTOEV - fermi;
                minEnergy = Math.min(minEnergy, energy);
                maxEnergy = Math.max(maxEnergy, energy);
            }
        }

        if (!empty2) {
            for (int i = 0; i < bandData2.numPoints(); i++) {
                double energy = bandData2.getEnergy(i) * Constants.RYTOEV - fermi;
                minEnergy = Math.min(minEnergy, energy);
                maxEnergy = Math.max(maxEnergy, energy);
            }
        }

        minEnergy = Math.floor(minEnergy) - VLINE_BUFFER;
        maxEnergy = Math.ceil(maxEnergy) + VLINE_BUFFER;

        return new double[] { minEnergy, maxEnergy };
    }
}
