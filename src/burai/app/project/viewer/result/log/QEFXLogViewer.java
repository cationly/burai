/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.log;

import java.io.File;
import java.io.IOException;

import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.QEFXResultViewer;

public class QEFXLogViewer extends QEFXResultViewer<QEFXLogViewerController> {

    public QEFXLogViewer(QEFXProjectController projectController, File file) throws IOException {
        super("QEFXLogViewer.fxml", new QEFXLogViewerController(projectController, file));
    }

}
