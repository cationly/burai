/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project;

import java.io.IOException;

import javafx.scene.input.KeyCode;
import burai.app.QEFXAppComponent;
import burai.app.QEFXMainController;
import burai.app.project.editor.EditorActions;
import burai.app.project.editor.input.InputEditorActions;
import burai.app.project.viewer.ViewerActions;
import burai.project.Project;

public class QEFXProject extends QEFXAppComponent<QEFXProjectController> {

    private Project project;

    private ViewerActions viewerActions;

    private EditorActions editorActions;

    public QEFXProject(QEFXMainController mainController, Project project) throws IOException {
        super("QEFXProject.fxml", new QEFXProjectController(mainController));

        if (project == null) {
            throw new IllegalArgumentException("project is null.");
        }

        this.project = project;

        this.viewerActions = new ViewerActions(this.project, this.controller);
        this.viewerActions.actionInitially();
        this.controller.setViewerActions(this.viewerActions);

        this.editorActions = new InputEditorActions(this.project, this.controller);
        this.editorActions.actionInitially();
        this.controller.setEditorActions(this.editorActions);

        this.setupKeyPressed();
    }

    private void setupKeyPressed() {
        this.node.setOnKeyPressed(event -> {
            if (event == null) {
                return;
            }

            if (event.isControlDown() && KeyCode.S.equals(event.getCode())) {
                // Ctrl + S
                if ((!this.controller.isResultExplorerMode()) && (!this.controller.isResultViewerMode())) {
                    this.controller.saveFile();
                }

            } else if (KeyCode.PRINTSCREEN.equals(event.getCode())) {
                // PrintScreen
                if (!this.controller.isResultExplorerMode()) {
                    this.controller.sceenShot();
                }
            }
        });
    }
}
