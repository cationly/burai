/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.project.property;

public class ProjectBandFactory {

    private ProjectBand band;

    public ProjectBandFactory() {
        this.band = null;
    }

    public ProjectBand getProjectBand(String path, String prefix) {
        if (path == null || path.isEmpty() || prefix == null || prefix.isEmpty()) {
            this.band = null;

        } else if (this.band != null && path.equals(this.band.getPath()) && prefix.equals(this.band.getPrefix())) {
            this.band.reload();

        } else {
            this.band = new ProjectBand(path, prefix);
        }

        return this.band;
    }
}
