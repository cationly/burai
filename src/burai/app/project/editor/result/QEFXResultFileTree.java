/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.result;

import java.io.IOException;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import burai.app.QEFXAppComponent;
import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.QEFXResultExplorer;
import burai.com.keys.PriorKeyEvent;
import burai.project.Project;

public class QEFXResultFileTree extends QEFXAppComponent<QEFXResultFileTreeController> {

    public QEFXResultFileTree(QEFXProjectController projectController, Project project) throws IOException {
        super("QEFXResultFileTree.fxml", new QEFXResultFileTreeController(projectController, project));

        if (this.node != null) {
            this.node.setOnMouseReleased(event -> this.node.requestFocus());
            this.setupCtrlFKey(this.node);
        }
    }

    public void setResultExplorer(QEFXResultExplorer explorer) {
        this.controller.setResultExplorer(explorer);
    }

    public void reload() {
        this.controller.reload();
    }

    private void setupCtrlFKey(Node node) {
        if (node == null) {
            return;
        }

        node.setOnKeyPressed(event -> {
            if (event == null) {
                return;
            }

            if (PriorKeyEvent.isPriorKeyEvent(event)) {
                return;
            }

            if (KeyCode.F5.equals(event.getCode())) {
                // F5
                this.controller.reloadAll();
            }
        });
    }
}
