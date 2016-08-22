/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result;

import java.io.IOException;

import javafx.scene.Node;
import burai.app.project.QEFXProjectController;
import burai.app.project.editor.result.QEFXResultFileTree;
import burai.project.Project;

public class ResultAction {

    private Project project;

    private QEFXProjectController controller;

    private QEFXResultExplorer explorer;

    private QEFXResultFileTree fileTree;

    public ResultAction(Project project, QEFXProjectController controller) {
        if (project == null) {
            throw new IllegalArgumentException("project is null.");
        }

        if (controller == null) {
            throw new IllegalArgumentException("controller is null.");
        }

        this.project = project;
        this.controller = controller;

        this.explorer = null;
        this.fileTree = null;
    }

    public QEFXProjectController getController() {
        return this.controller;
    }

    public void showResult() {
        if (this.explorer == null || this.fileTree == null) {
            this.initializeResult();
            return;
        }

        this.controller.setResultExplorerMode();
    }

    private void initializeResult() {
        this.explorer = new QEFXResultExplorer(this.controller, this.project);

        try {
            this.fileTree = new QEFXResultFileTree(this.controller, this.project);
            this.fileTree.setResultExplorer(this.explorer);
        } catch (IOException e) {
            this.fileTree = null;
            e.printStackTrace();
        }

        if (this.explorer != null && this.fileTree != null) {
            this.controller.setResultExplorerMode(controller2 -> {
                this.explorer.reload();
                this.fileTree.reload();
            });

            this.controller.clearStackedsOnViewerPane();

            Node explorerNode = this.explorer.getNode();
            if (explorerNode != null) {
                this.controller.stackOnViewerPane(explorerNode);
            }

            Node fileTreeNode = this.fileTree.getNode();
            if (fileTreeNode != null) {
                this.controller.setEditorPane(fileTreeNode);
            }
        }
    }
}
