/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.logger;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import burai.atoms.model.Atom;
import burai.atoms.model.AtomProperty;
import burai.atoms.model.Cell;
import burai.atoms.model.exception.ZeroVolumCellException;

public class AtomsLogger {

    private static final int DEFAULT_MAX_STORED = 20;

    private int maxStored;

    private Cell cell;

    private Deque<Configuration> configs;

    private Deque<Configuration> subConfigs;

    public AtomsLogger(Cell cell) {
        this(DEFAULT_MAX_STORED, cell);
    }

    public AtomsLogger(int maxStored, Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        this.maxStored = Math.max(maxStored, 0);
        this.cell = cell;
        this.configs = new LinkedList<AtomsLogger.Configuration>();
        this.subConfigs = new LinkedList<AtomsLogger.Configuration>();
    }

    public void storeConfiguration() {
        this.subConfigs.clear();

        this.configs.push(new Configuration(this.cell));

        if (this.configs.size() > this.maxStored) {
            this.configs.removeLast();
        }
    }

    public boolean canRestoreConfiguration() {
        if (this.configs == null || this.configs.isEmpty()) {
            return false;
        }

        return true;
    }

    public boolean canSubRestoreConfiguration() {
        if (this.subConfigs == null || this.subConfigs.isEmpty()) {
            return false;
        }

        return true;
    }

    public void restoreConfiguration() {
        if (this.configs == null || this.configs.isEmpty()) {
            return;
        }

        this.subConfigs.push(new Configuration(this.cell));

        boolean status = this.restoreConfiguration(this.configs);

        if (!status) {
            this.subConfigs.poll();
        }
    }

    public void subRestoreConfiguration() {
        if (this.subConfigs == null || this.subConfigs.isEmpty()) {
            return;
        }

        this.configs.push(new Configuration(this.cell));

        boolean status = this.restoreConfiguration(this.subConfigs);

        if (!status) {
            this.configs.poll();
        }
    }

    private boolean restoreConfiguration(Deque<Configuration> configs) {
        if (configs == null || configs.isEmpty()) {
            return false;
        }

        Configuration config = configs.poll();

        if (config.lattice == null) {
            return false;
        }
        if (config.atomName == null) {
            return false;
        }
        if (config.atomCoord == null) {
            return false;
        }
        if (config.atomFixed == null) {
            return false;
        }
        if (config.atomName.length != config.atomCoord.length) {
            return false;
        }
        if (config.atomName.length != config.atomFixed.length) {
            return false;
        }

        this.cell.removeAllAtoms();
        this.cell.stopBondResolving();
        this.restoreCell(config);
        this.restoreAtoms(config);
        this.cell.restartBondResolving();
        return true;
    }

    private void restoreCell(Configuration config) {
        if (config == null) {
            return;
        }

        double[][] preLattice = this.cell.copyLattice();

        try {
            this.cell.moveLattice(config.lattice);

        } catch (ZeroVolumCellException e1) {
            try {
                this.cell.moveLattice(preLattice);

            } catch (ZeroVolumCellException e2) {
                e2.printStackTrace();
            }
        }
    }

    private void restoreAtoms(Configuration config) {
        if (config == null) {
            return;
        }

        for (int i = 0; i < config.atomName.length; i++) {
            if (config.atomCoord[i].length < 3) {
                continue;
            }

            String name = config.atomName[i];
            double x = config.atomCoord[i][0];
            double y = config.atomCoord[i][1];
            double z = config.atomCoord[i][2];
            boolean xFixed = config.atomFixed[i][0];
            boolean yFixed = config.atomFixed[i][1];
            boolean zFixed = config.atomFixed[i][2];

            if (name != null && !name.isEmpty()) {
                Atom atom = new Atom(name, x, y, z);
                atom.setProperty(AtomProperty.FIXED_X, xFixed);
                atom.setProperty(AtomProperty.FIXED_Y, yFixed);
                atom.setProperty(AtomProperty.FIXED_Z, zFixed);
                this.cell.addAtom(atom);
            }
        }
    }

    private static class Configuration {

        public double[][] lattice;

        public String[] atomName;

        public double[][] atomCoord;

        public boolean[][] atomFixed;

        public Configuration(Cell cell) {
            this.lattice = null;
            this.atomName = null;
            this.atomCoord = null;
            this.atomFixed = null;

            if (cell == null) {
                return;
            }

            this.lattice = cell.copyLattice();

            Atom[] atoms = cell.listAtoms();
            if (atoms == null || atoms.length < 1) {
                return;
            }

            List<String> listName = new ArrayList<String>();
            List<double[]> listCoord = new ArrayList<double[]>();
            List<boolean[]> listFixed = new ArrayList<boolean[]>();

            for (Atom atom : atoms) {
                if (atom == null || !atom.isSlaveAtom()) {
                    listName.add(atom.getName());

                    double x = atom.getX();
                    double y = atom.getY();
                    double z = atom.getZ();
                    listCoord.add(new double[] { x, y, z });

                    boolean xFixed = atom.booleanProperty(AtomProperty.FIXED_X);
                    boolean yFixed = atom.booleanProperty(AtomProperty.FIXED_Y);
                    boolean zFixed = atom.booleanProperty(AtomProperty.FIXED_Z);
                    listFixed.add(new boolean[] { xFixed, yFixed, zFixed });
                }
            }

            if (listName.isEmpty() || listCoord.isEmpty()) {
                return;
            }

            this.atomName = listName.toArray(new String[listName.size()]);
            this.atomCoord = listCoord.toArray(new double[listCoord.size()][]);
            this.atomFixed = listFixed.toArray(new boolean[listFixed.size()][]);
        }
    }
}
