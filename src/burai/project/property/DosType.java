/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.project.property;

public enum DosType {
    TOTAL(-1, ""),
    PDOS_S(0, "s"),
    PDOS_P(1, "p"),
    PDOS_D(2, "d"),
    PDOS_F(3, "f");

    private int momentum;

    private String orbital;

    private DosType(int momentum, String orbital) {
        this.momentum = momentum;
        this.orbital = orbital;
    }

    public int getMomentum() {
        return this.momentum;
    }

    public String getOrbital() {
        return this.orbital;
    }
}
