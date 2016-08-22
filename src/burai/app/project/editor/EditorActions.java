/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor;

import burai.app.project.ProjectAction;
import burai.app.project.ProjectActions;
import burai.app.project.QEFXProjectController;
import burai.project.Project;

public abstract class EditorActions extends ProjectActions<String> {

    public EditorActions(Project project, QEFXProjectController controller) {
        super(project, controller);

        this.setupOnEditorSelected();
    }

    public void attach() {
        this.setupOnEditorSelected();
    }

    public void detach() {
        this.controller.setOnEditorSelected(null);
    }

    private void setupOnEditorSelected() {
        if (this.controller == null) {
            return;
        }

        this.controller.setOnEditorSelected(text -> {
            if (text == null || text.isEmpty()) {
                return;
            }

            ProjectAction action = null;
            if (this.actions != null) {
                action = this.actions.get(text);
            }

            if (action != null && this.controller != null) {
                action.actionOnProject(this.controller);
            }
        });
    }
}
