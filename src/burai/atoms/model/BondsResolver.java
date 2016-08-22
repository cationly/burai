/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.model;

import java.util.List;

import burai.atoms.model.event.AtomEvent;
import burai.atoms.model.event.AtomEventListener;
import burai.atoms.model.event.CellEvent;
import burai.atoms.model.event.CellEventListener;
import burai.atoms.model.event.ModelEvent;

public class BondsResolver implements AtomEventListener, CellEventListener {

    private static final double BOND_SCALE1 = 0.50;

    private static final double BOND_SCALE2 = 1.15;

    private Cell cell;

    boolean auto;

    protected BondsResolver(Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        this.cell = cell;
        this.cell.addListener(this);

        this.auto = true;
    }

    protected void setAuto(boolean auto) {
        this.auto = auto;
    }

    protected boolean isAuto() {
        return this.auto;
    }

    protected void resolve() {
        this.removeNotUsedBonds();

        List<Atom> atoms = this.cell.getAtoms();
        if (atoms == null || atoms.isEmpty()) {
            return;
        }

        for (int i = 0; i < atoms.size(); i++) {
            Atom atom = atoms.get(i);
            this.resolve(atom, i);
        }
    }

    protected void resolve(Atom atom) {
        List<Atom> atoms = this.cell.getAtoms();
        if (atoms == null || atoms.isEmpty()) {
            return;
        }

        this.resolve(atom, atoms.size());
    }

    private void resolve(Atom atom1, int maxAtom) {
        if (atom1 == null) {
            throw new IllegalArgumentException("atom1 is null.");
        }

        List<Atom> atoms = this.cell.getAtoms();
        if (atoms == null || atoms.isEmpty()) {
            return;
        }

        double x1 = atom1.getX();
        double y1 = atom1.getY();
        double z1 = atom1.getZ();
        double rcov1 = atom1.getRadius();

        for (int i = 0; i < maxAtom; i++) {
            Atom atom2 = atoms.get(i);
            if (atom1 == atom2) {
                continue;
            }

            double x2 = atom2.getX();
            double y2 = atom2.getY();
            double z2 = atom2.getZ();
            double rcov2 = atom2.getRadius();

            double rr = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2);
            double rcov = rcov1 + rcov2;
            double rrcov = rcov * rcov;
            double rrmin = BOND_SCALE1 * BOND_SCALE1 * rrcov;
            double rrmax = BOND_SCALE2 * BOND_SCALE2 * rrcov;

            Bond bond = this.cell.pickBond(atom1, atom2);
            if (rrmin <= rr && rr <= rrmax) {
                if (bond == null) {
                    this.cell.addBond(new Bond(atom1, atom2));
                }
            } else {
                if (bond != null) {
                    this.cell.removeBond(bond);
                }
            }
        }
    }

    private void removeAllBondsLinkedWith(Atom atom) {
        if (atom == null) {
            throw new IllegalArgumentException("atom is null.");
        }

        Bond[] bonds = this.cell.listBonds();
        if (bonds == null || bonds.length < 1) {
            return;
        }

        for (Bond bond : bonds) {
            Atom atom1 = bond.getAtom1();
            Atom atom2 = bond.getAtom2();
            if (atom == atom1 || atom == atom2) {
                this.cell.removeBond(bond);
            }
        }
    }

    private void removeNotUsedBonds() {
        Bond[] bonds = this.cell.listBonds();
        if (bonds == null || bonds.length < 1) {
            return;
        }

        List<Atom> atoms = this.cell.getAtoms();
        if (atoms == null || atoms.isEmpty()) {
            for (Bond bond : bonds) {
                this.cell.removeBond(bond);
            }
            return;
        }

        for (Bond bond : bonds) {
            Atom atom1 = bond.getAtom1();
            Atom atom2 = bond.getAtom2();
            boolean hasAtom1 = false;
            boolean hasAtom2 = false;
            for (Atom atom : atoms) {
                if (hasAtom1 && hasAtom2) {
                    break;
                } else if (!hasAtom1) {
                    hasAtom1 = (atom == atom1);
                } else if (!hasAtom2) {
                    hasAtom2 = (atom == atom2);
                }
            }
            if (!(hasAtom1 && hasAtom2)) {
                this.cell.removeBond(bond);
            }
        }
    }

    @Override
    public boolean isToBeFlushed() {
        return false;
    }

    @Override
    public void onModelDisplayed(ModelEvent event) {
        // NOP
    }

    @Override
    public void onModelNotDisplayed(ModelEvent event) {
        // NOP
    }

    @Override
    public void onLatticeMoved(CellEvent event) {
        if (this.cell != event.getSource()) {
            return;
        }

        if (!this.auto) {
            return;
        }

        this.resolve();
    }

    @Override
    public void onAtomAdded(CellEvent event) {
        if (this.cell != event.getSource()) {
            return;
        }

        Atom atom = event.getAtom();
        if (atom == null) {
            return;
        }

        atom.addListener(this);

        if (!this.auto) {
            return;
        }

        this.resolve(atom);
    }

    @Override
    public void onAtomRemoved(CellEvent event) {
        if (this.cell != event.getSource()) {
            return;
        }

        Atom atom = event.getAtom();
        if (atom == null) {
            return;
        }

        if (!this.auto) {
            return;
        }

        this.removeAllBondsLinkedWith(atom);
    }

    @Override
    public void onBondAdded(CellEvent event) {
        // NOP
    }

    @Override
    public void onBondRemoved(CellEvent event) {
        // NOP
    }

    @Override
    public void onAtomRenamed(AtomEvent event) {
        if (!this.auto) {
            return;
        }

        Object obj = event.getSource();
        if (obj == null || !(obj instanceof Atom)) {
            return;
        }

        Atom atom = (Atom) obj;
        this.resolve(atom);
    }

    @Override
    public void onAtomMoved(AtomEvent event) {
        if (!this.auto) {
            return;
        }

        Object obj = event.getSource();
        if (obj == null || !(obj instanceof Atom)) {
            return;
        }

        Atom atom = (Atom) obj;
        this.resolve(atom);
    }
}
