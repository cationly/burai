/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.project.property;

public interface DosInterface {

    public abstract DosType getType();

    public abstract boolean isSpinPolarized();

    public abstract int getAtomIndex();

    public abstract String getAtomName();

    public abstract int numPoints();

    public abstract double getEnergy(int i);

    public abstract double getDosUp(int i);

    public abstract double getDosDown(int i);

}
