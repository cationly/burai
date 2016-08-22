/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.model;

import burai.atoms.model.event.BondEvent;
import burai.atoms.model.event.BondEventListener;

public class Bond extends Model<BondEvent, BondEventListener> {

    private Atom atom1;
    private Atom atom2;

    public Bond(Atom atom1, Atom atom2) {
        super();

        if (atom1 == null) {
            throw new IllegalArgumentException("atom1 is null.");
        }
        if (atom2 == null) {
            throw new IllegalArgumentException("atom2 is null.");
        }
        if (atom1.equals(atom2)) {
            throw new IllegalArgumentException("atom1 equals to atom2.");
        }

        this.atom1 = atom1;
        this.atom2 = atom2;

        if (!this.atom1.addBond(this)) {
            throw new IllegalArgumentException("atom1 does not accept the bond.");
        }
        if (!this.atom2.addBond(this)) {
            throw new IllegalArgumentException("atom2 does not accept the bond.");
        }
    }

    @Override
    protected BondEvent createEvent() {
        return new BondEvent(this);
    }

    public boolean isSlaveBond() {
        return this.atom1.isSlaveAtom() || this.atom2.isSlaveAtom();
    }

    public Atom getAtom1() {
        return this.atom1;
    }

    public Atom getAtom2() {
        return this.atom2;
    }

    protected void detachFromAtoms() {
        boolean removeStat = true;
        removeStat = removeStat && this.atom1.removeBond(this);
        removeStat = removeStat && this.atom2.removeBond(this);

        if (removeStat) {
            this.notDisplay();
        }
    }

    private int getLinkedAtom(BondEvent event) {
        Atom atom = event.getAtom();
        if (atom == null) {
            throw new IllegalArgumentException("atom is null.");
        }

        int linkedAtom = BondEvent.LINKED_ATOM_NULL;
        if (atom == this.atom1) {
            linkedAtom = BondEvent.LINKED_ATOM1;
        } else if (atom == this.atom2) {
            linkedAtom = BondEvent.LINKED_ATOM2;
        }
        if (linkedAtom == BondEvent.LINKED_ATOM_NULL) {
            throw new IllegalArgumentException("atom is incorrect.");
        }

        return linkedAtom;
    }

    protected void atomIsRenamed(BondEvent event) {
        int linkedAtom = this.getLinkedAtom(event);
        event.setLinkedAtom(linkedAtom);

        if (this.listeners != null) {
            for (BondEventListener listener : this.listeners) {
                listener.onLinkedAtomRenamed(event);
            }
        }
    }

    protected void atomIsMoved(BondEvent event) {
        int linkedAtom = this.getLinkedAtom(event);
        event.setLinkedAtom(linkedAtom);

        if (this.listeners != null) {
            for (BondEventListener listener : this.listeners) {
                listener.onLinkedAtomMoved(event);
            }
        }
    }

    @Override
    public void display() {
        if (this.atom1.isInDisplayed() && this.atom2.isInDisplayed()) {
            super.display();
        }
    }

    @Override
    public int hashCode() {
        return this.atom1.hashCode() + this.atom2.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        Bond other = (Bond) obj;

        if (this.atom1.equals(other.atom1) && this.atom2.equals(other.atom2)) {
            return true;
        }

        if (this.atom1.equals(other.atom2) && this.atom2.equals(other.atom1)) {
            return true;
        }

        return false;
    }
}
