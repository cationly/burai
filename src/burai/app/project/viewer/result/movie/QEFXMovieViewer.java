/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.movie;

import java.io.IOException;

import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.QEFXResultViewer;
import burai.project.property.ProjectGeometryList;

public class QEFXMovieViewer extends QEFXResultViewer<QEFXMovieViewerController> {

    public QEFXMovieViewer(QEFXProjectController projectController, ProjectGeometryList projectGeometryList) throws IOException {
        super("QEFXMovieViewer.fxml", new QEFXMovieViewerController(projectController, projectGeometryList));
    }

}
