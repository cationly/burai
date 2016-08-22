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

public class BondEvent extends ModelEvent {

    public static final int LINKED_ATOM_NULL = 0;
    public static final int LINKED_ATOM1 = 1;
    public static final int LINKED_ATOM2 = 2;

    private AtomEvent atomEvent;

    private int linkedAtom;

    public BondEvent(Object source) {
        super(source);
        this.atomEvent = null;
        this.linkedAtom = LINKED_ATOM_NULL;
    }

    public void setAtomEvent(AtomEvent atomEvent) {
        this.atomEvent = atomEvent;
    }

    public AtomEvent getAtomEvent() {
        return this.atomEvent;
    }

    public void setLinkedAtom(int linkedAtom) {
        this.linkedAtom = linkedAtom;
    }

    public int getLinkedAtom() {
        return this.linkedAtom;
    }

    public Atom getAtom() {
        if (this.source instanceof Atom) {
            return (Atom) this.source;
        }

        return null;
    }
}
