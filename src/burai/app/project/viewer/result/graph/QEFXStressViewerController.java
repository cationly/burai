/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.graph;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import burai.app.project.QEFXProjectController;
import burai.com.consts.Constants;
import burai.com.math.Matrix3D;
import burai.project.property.ProjectGeometry;
import burai.project.property.ProjectGeometryList;
import burai.project.property.ProjectProperty;
import burai.project.property.ProjectStatus;

public class QEFXStressViewerController extends QEFXGraphViewerController {

    private boolean mdMode;

    private ProjectStatus projectStatus;

    private ProjectGeometryList projectGeometryList;

    public QEFXStressViewerController(
            QEFXProjectController projectController, ProjectProperty projectProperty, boolean mdMode) {

        super(projectController, Pos.BOTTOM_RIGHT);

        if (projectProperty == null) {
            throw new IllegalArgumentException("projectProperty is null.");
        }

        this.projectStatus = projectProperty.getStatus();

        if (mdMode) {
            this.projectGeometryList = projectProperty.getMdList();
        } else {
            this.projectGeometryList = projectProperty.getOptList();
        }

        this.mdMode = mdMode;
    }

    @Override
    protected int getCalculationID() {
        if (this.projectStatus == null) {
            return 0;
        }

        int offset = 0;
        if (this.projectGeometryList != null && !(this.projectGeometryList.isConverged())) {
            offset = 1;
        }

        if (this.mdMode) {
            return offset + this.projectStatus.getMdCount();
        } else {
            return offset + this.projectStatus.getOptCount();
        }
    }

    @Override
    protected GraphProperty createProperty() {
        GraphProperty property = new GraphProperty();

        if (this.mdMode) {
            property.setTitle("Molecular Dynamics");
            property.setXLabel("Time / ps");

        } else {
            property.setTitle("Geometric Optimization");
            property.setXLabel("# Iterations");
        }

        property.setYLabel("Stress tensor / kbar");

        final int numSeries = 6;
        final String[] names = { "XX", "YY", "ZZ", "XY", "XZ", "YZ" };
        final String[] colors = { "red", "blue", "green", "magenta", "yellow", "cyan" };
        for (int i = 0; i < numSeries; i++) {
            SeriesProperty seriesProperty = new SeriesProperty();
            seriesProperty.setName(names[i]);
            seriesProperty.setColor(colors[i]);
            seriesProperty.setDash(SeriesProperty.DASH_NULL);
            seriesProperty.setWithSymbol(true);
            seriesProperty.setWidth(1.5);
            property.addSeries(seriesProperty);
        }

        return property;
    }

    @Override
    protected void reloadData(LineChart<Number, Number> lineChart) {
        if (lineChart == null) {
            return;
        }

        if (this.projectGeometryList == null) {
            lineChart.getData().clear();
            return;
        }

        ProjectGeometryList projectGeometryList = this.projectGeometryList.copyGeometryList();
        if (projectGeometryList == null) {
            lineChart.getData().clear();
            return;
        }

        int numConverged = 0;
        double lastStress = 0.0;

        final int numSeries = 6;
        List<Series<Number, Number>> seriesList = new ArrayList<Series<Number, Number>>();
        for (int i = 0; i < numSeries; i++) {
            seriesList.add(new Series<Number, Number>());
        }

        for (int i = 0; i < projectGeometryList.numGeometries(); i++) {
            ProjectGeometry projectGeometry = projectGeometryList.getGeometry(i);
            boolean converged = projectGeometry == null ? false : projectGeometry.isConverged();
            double[][] stress = projectGeometry == null ? null : projectGeometry.getStress();
            if (stress == null || stress.length < 3
                    || stress[0] == null || stress[0].length < 3
                    || stress[1] == null || stress[1].length < 3
                    || stress[2] == null || stress[2].length < 3) {
                stress = Matrix3D.zero();
            }

            if (converged) {
                numConverged++;
                lastStress = (stress[0][0] + stress[1][1] + stress[2][2]) * Constants.RY_KBAR / 3.0;

                Number xValue = null;
                if (this.mdMode) {
                    double time = projectGeometry == null ? 0.0 : projectGeometry.getTime();
                    xValue = time;
                } else {
                    xValue = (i + 1);
                }

                seriesList.get(0).getData().add(new Data<Number, Number>(xValue, stress[0][0] * Constants.RY_KBAR));
                seriesList.get(1).getData().add(new Data<Number, Number>(xValue, stress[1][1] * Constants.RY_KBAR));
                seriesList.get(2).getData().add(new Data<Number, Number>(xValue, stress[2][2] * Constants.RY_KBAR));
                seriesList.get(3).getData().add(new Data<Number, Number>(xValue, stress[0][1] * Constants.RY_KBAR));
                seriesList.get(4).getData().add(new Data<Number, Number>(xValue, stress[0][2] * Constants.RY_KBAR));
                seriesList.get(5).getData().add(new Data<Number, Number>(xValue, stress[1][2] * Constants.RY_KBAR));
            }
        }

        lineChart.getData().clear();
        lineChart.getData().addAll(seriesList);

        if (!this.mdMode) {
            int iteration = numConverged;
            String strIteration = iteration + " iteration" + (iteration > 1 ? "s were" : " was") + " done.";

            boolean converged = projectGeometryList.isConverged();
            String strConverged = "Optimization is " + (converged ? "" : "not ") + "converged.";

            String strStress = null;
            if (numConverged > 0) {
                strStress = "Total stress = " + String.format("%.2f", lastStress) + " kbar";
            }

            Node note = null;
            if (strStress != null) {
                note = this.getNote(strIteration, strConverged, strStress);
            } else {
                note = this.getNote(strIteration, strConverged);
            }

            if (note != null) {
                this.stackNode(note, Pos.TOP_RIGHT);
            }
        }
    }
}
