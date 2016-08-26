/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.graph.tools;

import java.io.IOException;

import javafx.scene.Node;
import burai.app.QEFXAppComponent;
import burai.app.project.QEFXProjectController;

public class QEFXGraphNote extends QEFXAppComponent<QEFXGraphNoteController> {

    public QEFXGraphNote(QEFXProjectController projectController, Node content) throws IOException {
        super("QEFXGraphNote.fxml", new QEFXGraphNoteController(projectController, content));
    }

}
