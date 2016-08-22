/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.inputfile;

import java.io.IOException;

import burai.app.QEFXAppComponent;
import burai.app.project.QEFXProjectController;
import burai.project.Project;

public class QEFXInputFile extends QEFXAppComponent<QEFXInputFileController> {

    public QEFXInputFile(QEFXProjectController projectController, Project project) throws IOException {
        super("QEFXInputFile.fxml", new QEFXInputFileController(projectController, project));
    }

}
