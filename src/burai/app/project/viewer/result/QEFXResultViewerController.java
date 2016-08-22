/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result;

import burai.app.QEFXAppController;
import burai.app.project.QEFXProjectController;

public abstract class QEFXResultViewerController extends QEFXAppController {

    protected QEFXProjectController projectController;

    public QEFXResultViewerController(QEFXProjectController projectController) {
        super(projectController == null ? null : projectController.getMainController());

        if (projectController == null) {
            throw new IllegalArgumentException("projectController is null.");
        }

        this.projectController = projectController;
    }

    public abstract void reload();
}
