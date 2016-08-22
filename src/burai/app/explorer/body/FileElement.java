/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.explorer.body;

import burai.project.Project;
import burai.run.RunningNode;

public class FileElement {

    private String name;

    private int position;

    private boolean swapping;

    private Project project;

    private RunningNode runningNode;

    protected FileElement(String name) {
        this(name, -1);
    }

    protected FileElement(String name, int position) {
        this(name, position, false);
    }

    protected FileElement(String name, int position, boolean swapping) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is empty.");
        }

        this.name = name;
        this.position = position;
        this.swapping = swapping;
        this.project = null;
        this.runningNode = null;
    }

    protected String getName() {
        return this.name;
    }

    protected int getPosition() {
        return this.position;
    }

    protected boolean isSwapping() {
        return this.swapping;
    }

    protected void setProject(Project project) {
        this.project = project;
    }

    protected Project getProject() {
        return this.project;
    }

    protected void setRunningNode(RunningNode runningNode) {
        this.runningNode = runningNode;
    }

    protected RunningNode getRunningNode() {
        return this.runningNode;
    }
}
