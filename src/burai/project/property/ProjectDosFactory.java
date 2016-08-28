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

    private ProjectDos dos;

    protected ProjectDosFactory() {
        this.dos = null;
    }

    protected ProjectDos getProjectDos(String path, String prefix) {
        if (path == null || path.isEmpty() || prefix == null || prefix.isEmpty()) {
            this.dos = null;

        } else if (this.dos != null && path.equals(this.dos.getPath()) && prefix.equals(this.dos.getPrefix())) {
            // NOP

        } else {
            this.dos = new ProjectDos(path, prefix);
        }

        return this.dos;
    }
}
