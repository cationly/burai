/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.model;

import java.util.ArrayList;
import java.util.List;

import burai.atoms.element.ElementUtil;
import burai.atoms.model.event.AtomEvent;
import burai.atoms.model.event.AtomEventListener;
import burai.atoms.model.event.BondEvent;

public class Atom extends Model<AtomEvent, AtomEventListener> {

    private static final double R_MIN = 1.0e-3;

    private Atom masterAtom;
    private List<Atom> slaveAtoms;

    private List<Bond> bonds;

    private String name;
    private int atomNum;
    private double radius;
    private double x;
    private double y;
    private double z;

    public Atom(String name, double x, double y, double z) {
        this(name, ElementUtil.getAtomicNumber(name), ElementUtil.getCovalentRadius(name), x, y, z);
    }

    protected Atom(String name, int atomNum, double radius, double x, double y, double z) {
        super();

        if (radius <= 0.0) {
            throw new IllegalArgumentException("radius is not positive.");
        }

        this.masterAtom = null;
        this.slaveAtoms = null;

        this.bonds = null;

        this.name = ElementUtil.toAvailableName(name);
        this.atomNum = atomNum;
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    protected AtomEvent createEvent() {
        return new AtomEvent(this);
    }

    private AtomEvent createEvent(String name, String oldName) {
        AtomEvent event = new AtomEvent(this);
        event.setName(name);
        event.setOldName(oldName);
        return event;
    }

    private AtomEvent createEvent(double x, double y, double z, double dx, double dy, double dz) {
        AtomEvent event = new AtomEvent(this);
        event.setX(x);
        event.setY(y);
        event.setZ(z);
        event.setDeltaX(dx);
        event.setDeltaY(dy);
        event.setDeltaZ(dz);
        return event;
    }

    protected void setMasterAtom(Atom atom) {
        if (atom == null) {
            this.freeFromMasterAtom();
            return;
        }

        this.masterAtom = atom;

        if (atom.slaveAtoms == null) {
            atom.slaveAtoms = new ArrayList<Atom>();
        }

        if (atom.slaveAtoms.contains(this)) {
            throw new IllegalArgumentException("atom is already slave.");
        }

        atom.slaveAtoms.add(this);
    }

    public Atom getMasterAtom() {
        if (this.masterAtom == null) {
            return this;
        }

        return this.masterAtom.getMasterAtom();
    }

    private void freeFromMasterAtom() {
        if (this.masterAtom == null) {
            return;
        }

        if (this.masterAtom.slaveAtoms == null) {
            return;
        }

        int index = this.masterAtom.slaveAtoms.indexOf(this);
        if (index > -1) {
            this.masterAtom.slaveAtoms.remove(index);
        }
    }

    public boolean isSlaveAtom() {
        return this.masterAtom != null;
    }

    protected Atom[] listSlaveAtoms() {
        if (this.slaveAtoms == null) {
            return null;
        }

        return this.slaveAtoms.toArray(new Atom[this.slaveAtoms.size()]);
    }

    protected boolean addBond(Bond bond) {
        if (bond == null) {
            return false;
        }

        if (this.bonds == null) {
            this.bonds = new ArrayList<Bond>();
        }

        if (this.bonds.contains(bond)) {
            return false;
        }

        return this.bonds.add(bond);
    }

    protected boolean removeBond(Bond bond) {
        if (bond == null) {
            return false;
        }

        if (this.bonds == null) {
            return false;
        }

        int index = this.bonds.indexOf(bond);
        if (index > -1) {
            Bond bond2 = this.bonds.remove(index);
            bond2.notDisplay();
            return true;
        }

        return false;
    }

    public String getName() {
        return this.name;
    }

    public int getAtomNum() {
        return this.atomNum;
    }

    public double getRadius() {
        return this.radius;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    /**
     * this method is only for a master atom.
     */
    public void setName(String name) {
        if (this.isSlaveAtom()) {
            return;
        }

        if (name == null || name.isEmpty()) {
            return;
        }

        int atomNum0 = ElementUtil.getAtomicNumber(name);
        double radius0 = ElementUtil.getCovalentRadius(name);
        this.setNameKernel(ElementUtil.toAvailableName(name), atomNum0, radius0);
    }

    private void setNameKernel(String name, int atomNum, double radius) {
        if (name == null || name.isEmpty()) {
            return;
        }

        String oldName = this.name;
        this.name = name;
        this.atomNum = atomNum;
        this.radius = radius;

        AtomEvent atomEvent = null;
        BondEvent bondEvent = null;

        if (this.listeners != null) {
            if (atomEvent == null) {
                atomEvent = this.createEvent(name, oldName);
            }
            for (AtomEventListener listener : this.listeners) {
                listener.onAtomRenamed(atomEvent);
            }
        }

        if (this.bonds != null) {
            if (atomEvent == null) {
                atomEvent = this.createEvent(name, oldName);
            }
            if (bondEvent == null) {
                bondEvent = new BondEvent(this);
                bondEvent.setAtomEvent(atomEvent);
            }
            for (Bond bond : this.bonds) {
                bond.atomIsRenamed(bondEvent);
            }
        }

        if (this.slaveAtoms != null) {
            for (Atom atom : this.slaveAtoms) {
                atom.setNameKernel(name, atomNum, radius);
            }
        }
    }

    /**
     * this method is only for a master atom.
     * @param x
     * @param y
     * @param z
     */
    public void moveTo(double x, double y, double z) {
        if (this.isSlaveAtom()) {
            return;
        }

        this.moveKernel(x, y, z);
    }

    /**
     * this method is only for a master atom.
     * @param x
     * @param y
     * @param z
     */
    public void moveBy(double x, double y, double z) {
        this.moveTo(this.x + x, this.y + y, this.z + z);
    }

    private void moveKernel(double x, double y, double z) {
        double dx = x - this.x;
        double dy = y - this.y;
        double dz = z - this.z;
        this.x = x;
        this.y = y;
        this.z = z;

        AtomEvent atomEvent = null;
        BondEvent bondEvent = null;

        if (this.listeners != null) {
            if (atomEvent == null) {
                atomEvent = this.createEvent(x, y, z, dx, dy, dz);
            }
            for (AtomEventListener listener : this.listeners) {
                listener.onAtomMoved(atomEvent);
            }
        }

        if (this.bonds != null) {
            if (atomEvent == null) {
                atomEvent = this.createEvent(x, y, z, dx, dy, dz);
            }
            if (bondEvent == null) {
                bondEvent = new BondEvent(this);
                bondEvent.setAtomEvent(atomEvent);
            }
            for (Bond bond : this.bonds) {
                bond.atomIsMoved(bondEvent);
            }
        }

        if (this.slaveAtoms != null) {
            for (Atom atom : this.slaveAtoms) {
                atom.moveKernel(atom.x + dx, atom.y + dy, atom.z + dz);
            }
        }
    }

    /**
     * this method is only for a master atom.
     */
    @Override
    public void setProperty(String key, Object value) {
        if (this.isSlaveAtom()) {
            return;
        }

        this.setPropertyKernel(key, value);
    }

    private void setPropertyKernel(String key, Object value) {
        if (key == null) {
            return;
        }

        super.setProperty(key, value);

        if (this.slaveAtoms != null) {
            for (Atom atom : this.slaveAtoms) {
                atom.setPropertyKernel(key, value);
            }
        }
    }

    @Override
    public void display() {
        super.display();

        if (this.bonds != null) {
            for (Bond bond : this.bonds) {
                bond.display();
            }
        }
    }

    @Override
    public void notDisplay() {
        super.notDisplay();

        if (this.bonds != null) {
            for (Bond bond : this.bonds) {
                bond.notDisplay();
            }
        }
    }

    @Override
    public int hashCode() {
        int ix = (int) (1000.0 * this.x + 0.5);
        int iy = (int) (1000.0 * this.y + 0.5);
        int iz = (int) (1000.0 * this.z + 0.5);
        return ix + iy * 1000 + iz * 1000 * 1000 + this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    public boolean equalsPosition(Atom atom) {
        if (atom == null) {
            return false;
        }

        if (this == atom) {
            return true;
        }

        double dx = this.x - atom.x;
        double dy = this.y - atom.y;
        double dz = this.z - atom.z;
        double rr = dx * dx + dy * dy + dz * dz;
        return rr < R_MIN * R_MIN;
    }
}
