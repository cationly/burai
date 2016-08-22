/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.project.property;

import java.util.ArrayList;
import java.util.List;

public class ProjectGeometry {

    private boolean converged;

    private double time;

    private double energy;

    private double totalForce;

    private double kinetic;

    private double temperature;

    private List<Double> cell;

    private List<Double> stress;

    private List<Atom> atoms;

    public ProjectGeometry() {
        this.converged = false;
        this.time = 0.0;
        this.energy = 0.0;
        this.totalForce = 0.0;
        this.kinetic = 0.0;
        this.temperature = 0.0;
        this.cell = null;
        this.stress = null;
        this.atoms = null;
    }

    public synchronized boolean isConverged() {
        return this.converged;
    }

    public synchronized void setConverged(boolean converged) {
        this.converged = converged;
    }

    public synchronized double getTime() {
        return this.time;
    }

    public synchronized void setTime(double time) {
        this.time = time;
    }

    public synchronized double getEnergy() {
        return this.energy;
    }

    public synchronized void setEnergy(double energy) {
        this.energy = energy;
    }

    public synchronized double getTotalForce() {
        return this.totalForce;
    }

    public synchronized void setTotalForce(double totalForce) {
        this.totalForce = totalForce;
    }

    public synchronized double getTemperature() {
        return this.temperature;
    }

    public synchronized void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public synchronized double getKinetic() {
        return this.kinetic;
    }

    public synchronized void setKinetic(double kinetic) {
        this.kinetic = kinetic;
    }

    private double convertDouble(Double obj) {
        return obj == null ? 0.0 : obj.doubleValue();
    }

    public synchronized double[][] getCell() {
        if (this.cell == null || this.cell.size() < 9) {
            return null;
        }

        double[][] cellArray = new double[3][3];
        cellArray[0][0] = this.convertDouble(this.cell.get(0));
        cellArray[0][1] = this.convertDouble(this.cell.get(1));
        cellArray[0][2] = this.convertDouble(this.cell.get(2));
        cellArray[1][0] = this.convertDouble(this.cell.get(3));
        cellArray[1][1] = this.convertDouble(this.cell.get(4));
        cellArray[1][2] = this.convertDouble(this.cell.get(5));
        cellArray[2][0] = this.convertDouble(this.cell.get(6));
        cellArray[2][1] = this.convertDouble(this.cell.get(7));
        cellArray[2][2] = this.convertDouble(this.cell.get(8));
        return cellArray;
    }

    public synchronized void setCell(double[][] cellArray) {
        if (cellArray == null || cellArray.length < 3) {
            throw new IllegalArgumentException("cellArray is incorrect.");
        }

        for (int i = 0; i < 3; i++) {
            if (cellArray[i] == null || cellArray[i].length < 3) {
                throw new IllegalArgumentException("cellArray[" + i + "] is incorrect.");
            }
        }

        if (this.cell == null) {
            this.cell = new ArrayList<Double>();
        } else {
            this.cell.clear();
        }

        this.cell.add(cellArray[0][0]);
        this.cell.add(cellArray[0][1]);
        this.cell.add(cellArray[0][2]);
        this.cell.add(cellArray[1][0]);
        this.cell.add(cellArray[1][1]);
        this.cell.add(cellArray[1][2]);
        this.cell.add(cellArray[2][0]);
        this.cell.add(cellArray[2][1]);
        this.cell.add(cellArray[2][2]);
    }

    public synchronized double[][] getStress() {
        if (this.stress == null || this.stress.size() < 9) {
            return null;
        }

        double[][] stressArray = new double[3][3];
        stressArray[0][0] = this.convertDouble(this.stress.get(0));
        stressArray[0][1] = this.convertDouble(this.stress.get(1));
        stressArray[0][2] = this.convertDouble(this.stress.get(2));
        stressArray[1][0] = this.convertDouble(this.stress.get(3));
        stressArray[1][1] = this.convertDouble(this.stress.get(4));
        stressArray[1][2] = this.convertDouble(this.stress.get(5));
        stressArray[2][0] = this.convertDouble(this.stress.get(6));
        stressArray[2][1] = this.convertDouble(this.stress.get(7));
        stressArray[2][2] = this.convertDouble(this.stress.get(8));
        return stressArray;
    }

    public synchronized void setStress(double[][] stressArray) {
        if (stressArray == null || stressArray.length < 3) {
            throw new IllegalArgumentException("stressArray is incorrect.");
        }

        for (int i = 0; i < 3; i++) {
            if (stressArray[i] == null || stressArray[i].length < 3) {
                throw new IllegalArgumentException("stressArray[" + i + "] is incorrect.");
            }
        }

        if (this.stress == null) {
            this.stress = new ArrayList<Double>();
        } else {
            this.stress.clear();
        }

        this.stress.add(stressArray[0][0]);
        this.stress.add(stressArray[0][1]);
        this.stress.add(stressArray[0][2]);
        this.stress.add(stressArray[1][0]);
        this.stress.add(stressArray[1][1]);
        this.stress.add(stressArray[1][2]);
        this.stress.add(stressArray[2][0]);
        this.stress.add(stressArray[2][1]);
        this.stress.add(stressArray[2][2]);
    }

    public synchronized int numAtoms() {
        return this.atoms == null ? 0 : this.atoms.size();
    }

    private synchronized Atom getAtom(int i) throws IndexOutOfBoundsException {
        if (this.atoms == null || i < 0 || i >= this.atoms.size()) {
            throw new IndexOutOfBoundsException("incorrect index of atoms: " + i + ".");
        }

        return this.atoms.get(i);
    }

    public synchronized String getName(int i) throws IndexOutOfBoundsException {
        Atom atom = this.getAtom(i);
        return atom == null ? "" : atom.name;
    }

    public synchronized double getX(int i) throws IndexOutOfBoundsException {
        Atom atom = this.getAtom(i);
        return atom == null ? 0.0 : atom.x;
    }

    public synchronized double getY(int i) throws IndexOutOfBoundsException {
        Atom atom = this.getAtom(i);
        return atom == null ? 0.0 : atom.y;
    }

    public synchronized double getZ(int i) throws IndexOutOfBoundsException {
        Atom atom = this.getAtom(i);
        return atom == null ? 0.0 : atom.z;
    }

    public synchronized double getForceX(int i) throws IndexOutOfBoundsException {
        Atom atom = this.getAtom(i);
        return atom == null ? 0.0 : atom.fx;
    }

    public synchronized double getForceY(int i) throws IndexOutOfBoundsException {
        Atom atom = this.getAtom(i);
        return atom == null ? 0.0 : atom.fy;
    }

    public synchronized double getForceZ(int i) throws IndexOutOfBoundsException {
        Atom atom = this.getAtom(i);
        return atom == null ? 0.0 : atom.fz;
    }

    public synchronized void removeAtom(int i) throws IndexOutOfBoundsException {
        if (this.atoms == null || i < 0 || i >= this.atoms.size()) {
            throw new IndexOutOfBoundsException("incorrect index of atoms: " + i + ".");
        }

        this.atoms.remove(i);
    }

    public synchronized void removeAtom(String name, double x, double y, double z) {
        if (name == null) {
            throw new IllegalArgumentException("name is null.");
        }

        name = name.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name is empty.");
        }

        if (this.atoms == null || this.atoms.isEmpty()) {
            return;
        }

        Atom atom = new Atom(name, x, y, z);
        while (this.atoms.remove(atom)) {
            // NOP
        }
    }

    public synchronized void addAtom(String name, double x, double y, double z) {
        if (name == null) {
            throw new IllegalArgumentException("name is null.");
        }

        name = name.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name is empty.");
        }

        if (this.atoms == null) {
            this.atoms = new ArrayList<Atom>();
        }

        this.atoms.add(new Atom(name, x, y, z));
    }

    public synchronized void setForce(int i, double fx, double fy, double fz) throws IndexOutOfBoundsException {
        Atom atom = this.getAtom(i);
        if (atom != null) {
            atom.fx = fx;
            atom.fy = fy;
            atom.fz = fz;
        }
    }

    private static class Atom {

        private static final double MIN_R = 1.0e-8;
        private static final double MIN_RR = MIN_R * MIN_R;

        public String name;
        public double x;
        public double y;
        public double z;
        public double fx;
        public double fy;
        public double fz;

        public Atom(String name, double x, double y, double z) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
            this.fx = 0.0;
            this.fy = 0.0;
            this.fz = 0.0;
        }

        @Override
        public int hashCode() {
            int value = this.name == null ? 0 : this.name.hashCode();
            value += ((int) (100.0 * this.x)) * 1;
            value += ((int) (100.0 * this.y)) * 1000;
            value += ((int) (100.0 * this.z)) * 1000000;
            return value;
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

            Atom other = (Atom) obj;

            if (this.name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!this.name.equals(other.name)) {
                return false;
            }

            double dx = this.x - other.x;
            double dy = this.y - other.y;
            double dz = this.z - other.z;
            double rr = dx * dx + dy * dy + dz * dz;
            if (rr > MIN_RR) {
                return false;
            }

            return true;
        }
    }
}
