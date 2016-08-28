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
    TOTAL(-1),
    PDOS_S(0),
    PDOS_P(1),
    PDOS_D(2),
    PDOS_F(3);

    private int momentum;

    private DosType(int momentum) {
        this.momentum = momentum;
    }

    public int getMomentum() {
        return this.momentum;
    }
}