/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.brillouin.model;

import java.util.ArrayList;
import java.util.List;

public class BrillouinFace {

    private List<double[]> vertices;

    public BrillouinFace() {
        this.vertices = new ArrayList<double[]>();
    }

    public void addVertex(double x, double y, double z) {
        this.vertices.add(new double[] { x, y, z });
    }

}
