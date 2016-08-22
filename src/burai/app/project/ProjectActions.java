/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project;

import java.util.HashMap;
import java.util.Map;

import burai.project.Project;

public abstract class ProjectActions<T> {

    protected Project project;

    protected QEFXProjectController controller;

    protected Map<T, ProjectAction> actions;

    public ProjectActions(Project project, QEFXProjectController controller) {
        if (project == null) {
            throw new IllegalArgumentException("project is null.");
        }

        if (controller == null) {
            throw new IllegalArgumentException("controller is null.");
        }

        this.project = project;
        this.controller = controller;
        this.actions = new HashMap<T, ProjectAction>();
    }

    public abstract void actionInitially();
}
