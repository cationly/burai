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
import burai.project.property.ProjectProperty;

public class QEFXStressViewer extends QEFXGraphViewer<QEFXStressViewerController> {

    public QEFXStressViewer(QEFXProjectController projectController,
            ProjectProperty projectProperty, boolean mdMode) throws IOException {

        super(new QEFXStressViewerController(projectController, projectProperty, mdMode));
    }

}
