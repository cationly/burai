/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.project.property;

public class ProjectDosFactory {

    private String path;

    private String prefix;

    private ProjectDos dos;

    public ProjectDosFactory() {
        this.path = null;
        this.prefix = null;
        this.dos = null;
    }

    protected void setPath(String path, String prefix) {
        this.path = path;
        this.prefix = prefix;
    }

    public ProjectDos getProjectDos() {
        if (this.path == null || this.path.isEmpty() || this.prefix == null || this.prefix.isEmpty()) {
            this.dos = null;

        } else if (this.dos != null && this.path.equals(this.dos.getPath()) && this.prefix.equals(this.dos.getPrefix())) {
            this.dos.reload();

        } else {
            this.dos = new ProjectDos(this.path, this.prefix);
        }

        return this.dos;
    }
}
