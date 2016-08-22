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

public class ProjectEnergies {

    private boolean converged;

    private List<Double> energies;

    public ProjectEnergies() {
        this.converged = false;
        this.energies = null;
    }

    public synchronized boolean isConverged() {
        return this.converged;
    }

    public synchronized void setConverged(boolean converged) {
        this.converged = converged;
    }

    public synchronized void clearEnergies() {
        this.converged = false;

        if (this.energies != null) {
            this.energies.clear();
        }
    }

    public synchronized int numEnergies() {
        return this.energies == null ? 0 : this.energies.size();
    }

    public synchronized double getEnergy(int i) {
        if (this.energies == null || i < 0 || i >= this.energies.size()) {
            throw new IndexOutOfBoundsException("incorrect index of energies: " + i + ".");
        }

        return this.energies.get(i);
    }

    public synchronized void removeEnergy(int i) {
        if (this.energies == null || i < 0 || i >= this.energies.size()) {
            throw new IndexOutOfBoundsException("incorrect index of energies: " + i + ".");
        }

        this.energies.remove(i);
    }

    public synchronized void addEnergy(double energy) {
        if (this.energies == null) {
            this.energies = new ArrayList<Double>();
        }

        this.energies.add(energy);
    }

    public synchronized ProjectEnergies copyEnergies() {
        ProjectEnergies other = new ProjectEnergies();

        other.converged = this.converged;

        if (this.energies == null) {
            other.energies = null;

        } else {
            other.energies = new ArrayList<Double>(this.energies);
        }

        return other;
    }
}
