/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.band;

import javafx.scene.chart.LineChart;
import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.graph.GraphProperty;
import burai.app.project.viewer.result.graph.QEFXGraphViewerController;
import burai.project.property.ProjectBand;
import burai.project.property.ProjectEnergies;

public class QEFXBandViewerController extends QEFXGraphViewerController {

    private ProjectEnergies projectEnergies;

    private ProjectBand projectBand;

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
        // TODO
        return null;
    }

    @Override
    protected void reloadData(LineChart<Number, Number> lineChart) {
        // TODO
    }
}
