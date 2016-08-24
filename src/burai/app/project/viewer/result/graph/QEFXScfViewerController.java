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
import burai.project.property.ProjectEnergies;

public class QEFXScfViewerController extends QEFXGraphViewerController {

    private ProjectEnergies projectEnergies;

    public QEFXScfViewerController(QEFXProjectController projectController, ProjectEnergies projectEnergies) {
        super(projectController);

        if (projectEnergies == null) {
            throw new IllegalArgumentException("projectEnergies is null.");
        }

        this.projectEnergies = projectEnergies;
    }

    @Override
    protected GraphProperty createProperty() {
        GraphProperty property = new GraphProperty();

        property.setTitle("Convergence of SCF");
        property.setXLabel("# Iterations");
        property.setYLabel("Total energy / Ry");

        SeriesProperty seriesProperty = new SeriesProperty();
        seriesProperty.setName("Total energy");
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

        ProjectEnergies projectEnergies = this.projectEnergies.copyEnergies();
        if (projectEnergies == null) {
            lineChart.getData().clear();
            this.clearStackedNodes();
            return;
        }

        Series<Number, Number> series = new Series<Number, Number>();
        for (int i = 0; i < projectEnergies.numEnergies(); i++) {
            series.getData().add(new Data<Number, Number>(i + 1, projectEnergies.getEnergy(i)));
        }

        lineChart.getData().clear();
        lineChart.getData().add(series);

        int iteration = projectEnergies.numEnergies();
        String strIteration = iteration + " iteration" + (iteration > 1 ? "s were" : " was") + " done.";

        boolean converged = projectEnergies.isConverged();
        String strConverged = "SCF is " + (converged ? "" : "not ") + "converged.";

        String strEnergy = null;
        if (projectEnergies.numEnergies() > 0) {
            double energy = projectEnergies.getEnergy(projectEnergies.numEnergies() - 1);
            strEnergy = "Total energy = " + String.format("%.8f", energy) + " Ry";
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
