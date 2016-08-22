/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.model.event;

import burai.atoms.model.Atom;
import burai.atoms.model.Bond;

public class CellEvent extends ModelEvent {

    private double[][] lattice;

    private Atom atom;

    private Bond bond;

    public CellEvent(Object source) {
        super(source);
        this.lattice = null;
        this.atom = null;
        this.bond = null;
    }

    public void setLattice(double[][] lattice) {
        this.lattice = lattice;
    }

    public double[][] getLattice() {
        return this.lattice;
    }

    public void setAtom(Atom atom) {
        this.atom = atom;
    }

    public Atom getAtom() {
        return this.atom;
    }

    public void setBond(Bond bond) {
        this.bond = bond;
    }

    public Bond getBond() {
        return this.bond;
    }
}
