/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.band;

import java.io.IOException;

import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.QEFXResultViewer;
import burai.project.property.ProjectBand;
import burai.project.property.ProjectBandPaths;
import burai.project.property.ProjectEnergies;

public class QEFXBandViewer extends QEFXResultViewer<QEFXBandViewerController> {

    public QEFXBandViewer(QEFXProjectController projectController,
            ProjectEnergies projectEnergies, ProjectBand projectBand, ProjectBandPaths projectBandPaths) throws IOException {

        super("QEFXBandViewer.fxml",
                new QEFXBandViewerController(projectController, projectEnergies, projectBand, projectBandPaths));
    }

}
