/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.graph;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import burai.app.project.QEFXProjectController;
import burai.project.property.ProjectGeometry;
import burai.project.property.ProjectGeometryList;

public class QEFXEnergyViewerController extends QEFXGraphViewerController {

    private boolean mdMode;

    private EnergyType energyType;

    private ProjectGeometryList projectGeometryList;

    public QEFXEnergyViewerController(QEFXProjectController projectController,
            ProjectGeometryList projectGeometryList, EnergyType energyType, boolean mdMode) {

        super(projectController);

        if (projectGeometryList == null) {
            throw new IllegalArgumentException("projectGeometryList is null.");
        }

        if (energyType == null) {
            throw new IllegalArgumentException("energyType is null.");
        }

        this.projectGeometryList = projectGeometryList;

        this.mdMode = mdMode;
        this.energyType = energyType;
    }

    @Override
    protected GraphProperty createProperty() {
        GraphProperty property = new GraphProperty();
        SeriesProperty seriesProperty = new SeriesProperty();

        if (this.mdMode) {
            property.setTitle("Molecular Dynamics");
            property.setXLabel("Time / ps");

        } else {
            property.setTitle("Geometric Optimization");
            property.setXLabel("# Iterations");
        }

        if (EnergyType.TOTAL.equals(this.energyType)) {
            property.setYLabel("Total energy / Ry");
            seriesProperty.setName("Total energy");

        } else if (EnergyType.KINETIC.equals(this.energyType)) {
            property.setYLabel("Kinetic energy / Ry");
            seriesProperty.setName("Kinetic energy");

        } else if (EnergyType.CONSTANT.equals(this.energyType)) {
            property.setYLabel("Total energy + Kinetic energy / Ry");
            seriesProperty.setName("Total energy + Kinetic energy");

        } else if (EnergyType.TEMPERATURE.equals(this.energyType)) {
            property.setYLabel("Temperature / K");
            seriesProperty.setName("Temperature");
        }

        seriesProperty.setColor("dodgerblue");
        seriesProperty.setDash(SeriesProperty.DASH_NULL);
        seriesProperty.setWithSymbol(true);
        seriesProperty.setWidth(2.0);
        property.addSeries(seriesProperty);

        return property;
    }

    @Override
    public void reloadData(LineChart<Number, Number> lineChart) {
        if (lineChart == null) {
            return;
        }

        ProjectGeometryList projectGeometryList = this.projectGeometryList.copyGeometryList();
        if (projectGeometryList == null) {
            lineChart.getData().clear();
            this.clearStackedNodes();
            return;
        }

        int numConverged = 0;
        double lastValue = 0.0;

        Series<Number, Number> series = new Series<Number, Number>();

        for (int i = 0; i < projectGeometryList.numGeometries(); i++) {
            ProjectGeometry projectGeometry = projectGeometryList.getGeometry(i);
            boolean converged = projectGeometry == null ? false : projectGeometry.isConverged();

            double value = 0.0;
            if (EnergyType.TOTAL.equals(this.energyType)) {
                double totEnergy = projectGeometry == null ? 0.0 : projectGeometry.getEnergy();
                value = totEnergy;

            } else if (EnergyType.KINETIC.equals(this.energyType)) {
                double kinEnergy = projectGeometry == null ? 0.0 : projectGeometry.getKinetic();
                value = kinEnergy;

            } else if (EnergyType.CONSTANT.equals(this.energyType)) {
                double totEnergy = projectGeometry == null ? 0.0 : projectGeometry.getEnergy();
                double kinEnergy = projectGeometry == null ? 0.0 : projectGeometry.getKinetic();
                value = totEnergy + kinEnergy;

            } else if (EnergyType.TEMPERATURE.equals(this.energyType)) {
                double temp = projectGeometry == null ? 0.0 : projectGeometry.getTemperature();
                value = temp;
            }

            if (converged) {
                numConverged++;
                lastValue = value;

                if (this.mdMode) {
                    double time = projectGeometry == null ? 0.0 : projectGeometry.getTime();
                    series.getData().add(new Data<Number, Number>(time, value));

                } else {
                    series.getData().add(new Data<Number, Number>(i + 1, value));
                }
            }
        }

        lineChart.getData().clear();
        lineChart.getData().add(series);

        if (!this.mdMode) {
            int iteration = numConverged;
            String strIteration = iteration + " iteration" + (iteration > 1 ? "s were" : " was") + " done.";

            boolean converged = projectGeometryList.isConverged();
            String strConverged = "Optimization is " + (converged ? "" : "not ") + "converged.";

            String strEnergy = null;
            if (numConverged > 0) {
                if (EnergyType.TOTAL.equals(this.energyType)) {
                    strEnergy = "Total energy = " + lastValue + " Ry";

                } else if (EnergyType.KINETIC.equals(this.energyType)) {
                    strEnergy = "Kinetic energy = " + lastValue + " Ry";

                } else if (EnergyType.CONSTANT.equals(this.energyType)) {
                    strEnergy = "Total + Kinetic energy = " + lastValue + " Ry";

                } else if (EnergyType.TEMPERATURE.equals(this.energyType)) {
                    strEnergy = "Temperature = " + lastValue + " K";
                }
            }

            this.clearStackedNodes();

            Node note = null;
            if (strEnergy != null) {
                note = this.getNote(strIteration, strConverged, strEnergy);
            } else {
                note = this.getNote(strIteration, strConverged);
            }

            if (note != null) {
                this.stackNode(note, Pos.TOP_RIGHT);
            }
        }
    }
}
