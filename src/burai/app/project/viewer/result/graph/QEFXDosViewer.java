/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.graph;

import java.io.IOException;

import burai.app.project.QEFXProjectController;
import burai.project.property.ProjectDos;
import burai.project.property.ProjectEnergies;

public class QEFXDosViewer extends QEFXGraphViewer<QEFXDosViewerController> {

    public QEFXDosViewer(QEFXProjectController projectController,
            ProjectEnergies projectEnergies, ProjectDos projectDos) throws IOException {

        super(new QEFXDosViewerController(projectController, projectEnergies, projectDos));
    }

}
