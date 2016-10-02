/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.result.band;

import burai.app.project.QEFXProjectController;
import burai.app.project.editor.result.graph.QEFXGraphEditorController;
import burai.app.project.viewer.result.graph.QEFXGraphViewerController;

public class QEFXBandEditorController extends QEFXGraphEditorController {

    public QEFXBandEditorController(QEFXProjectController projectController, QEFXGraphViewerController viewerController) {
        super(projectController, viewerController);
    }

}
