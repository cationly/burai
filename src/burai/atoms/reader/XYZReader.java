/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import burai.atoms.model.Atom;
import burai.atoms.model.Cell;
import burai.atoms.model.exception.ZeroVolumCellException;

public class XYZReader extends AtomsReader {

    private static final double BUFFER_OF_CELL = 5.0;

    public XYZReader(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public XYZReader(File file) throws FileNotFoundException {
        super(file);
    }

    private double toDouble(String value) throws NumberFormatException {
        if (value == null || value.isEmpty()) {
            throw new NumberFormatException("value is empty.");
        }

        return Double.parseDouble(value.replace('d', 'e').replace('D', 'E'));
    }

    @Override
    public Cell readCell() throws IOException {
        if (this.reader == null) {
            return null;
        }

        String line = null;

        /*
         * read #atoms
         */
        line = this.readNetLine();
        int numAtoms = 0;
        try {
            numAtoms = Integer.parseInt(line);
        } catch (NumberFormatException e) {
            throw new IOException("cannot read #atoms from a XYZ file.");
        }

        /*
         * read comment
         */
        line = this.readNetLine();
        double[][] lattice = this.parseLatticeVector(line);

        /*
         * read atoms
         */
        String[] name = new String[numAtoms];
        double[][] coord = new double[numAtoms][3];
        for (int i = 0; i < numAtoms; i++) {
            line = this.readNetLine();
            String[] subLines = line.split("[\\s,]+");
            if (subLines == null || subLines.length < 4) {
                throw new IOException("cannot read atomic coordinate from a XYZ file: " + line);
            }

            name[i] = subLines[0];
            try {
                coord[i][0] = this.toDouble(subLines[1]);
                coord[i][1] = this.toDouble(subLines[2]);
                coord[i][2] = this.toDouble(subLines[3]);
            } catch (NumberFormatException e) {
                throw new IOException("cannot read atomic coordinate from a XYZ file: " + line);
            }
        }

        /*
         * create an instance of Cell
         */
        if (lattice == null) {
            this.moveAtomsToCenter(coord);
            lattice = this.createLatticeVector(coord);
        }

        Cell cell = null;
        try {
            cell = new Cell(lattice);
        } catch (ZeroVolumCellException e) {
            throw new IOException(e);
        }

        for (int i = 0; i < numAtoms; i++) {
            cell.addAtom(new Atom(name[i], coord[i][0], coord[i][1], coord[i][2]));
        }

        return cell;
    }

    private String readNetLine() throws IOException {
        String line = this.reader.readLine();
        if (line == null) {
            throw new IOException("not enough lines in reading a XYZ file.");
        }

        return line.trim();
    }

    private double[][] parseLatticeVector(String line) {
        int index = line.indexOf(':');
        if (index < 0 || index >= (line.length() - 1)) {
            return null;
        }

        String line2 = line.substring(index + 1).trim();
        String[] subLines = line2.split("[\\s,]+");
        if (subLines == null || subLines.length < 9) {
            return null;
        }

        double[][] lattice = new double[3][3];
        try {
            lattice[0][0] = this.toDouble(subLines[0]);
            lattice[0][1] = this.toDouble(subLines[1]);
            lattice[0][2] = this.toDouble(subLines[2]);
            lattice[1][0] = this.toDouble(subLines[3]);
            lattice[1][1] = this.toDouble(subLines[4]);
            lattice[1][2] = this.toDouble(subLines[5]);
            lattice[2][0] = this.toDouble(subLines[6]);
            lattice[2][1] = this.toDouble(subLines[7]);
            lattice[2][2] = this.toDouble(subLines[8]);
        } catch (NumberFormatException e) {
            return null;
        }

        return lattice;
    }

    private void moveAtomsToCenter(double[][] coord) {
        if (coord == null || coord.length < 1) {
            return;
        }

        double xMean = 0.0;
        double yMean = 0.0;
        double zMean = 0.0;

        for (int i = 0; i < coord.length; i++) {
            xMean += coord[i][0];
            yMean += coord[i][1];
            zMean += coord[i][2];
        }

        xMean /= (double) coord.length;
        yMean /= (double) coord.length;
        zMean /= (double) coord.length;

        for (int i = 0; i < coord.length; i++) {
            coord[i][0] -= xMean;
            coord[i][1] -= yMean;
            coord[i][2] -= zMean;
        }
    }

    private double[][] createLatticeVector(double[][] coord) {
        double[][] lattice = new double[3][3];
        lattice[0][0] = 2.0 * BUFFER_OF_CELL;
        lattice[0][1] = 0.0;
        lattice[0][2] = 0.0;
        lattice[1][0] = 0.0;
        lattice[1][1] = 2.0 * BUFFER_OF_CELL;
        lattice[1][2] = 0.0;
        lattice[2][0] = 0.0;
        lattice[2][1] = 0.0;
        lattice[2][2] = 2.0 * BUFFER_OF_CELL;

        if (coord == null || coord.length < 1) {
            return lattice;
        }

        double xMax = coord[0][0];
        double xMin = coord[0][0];
        double yMax = coord[0][1];
        double yMin = coord[0][1];
        double zMax = coord[0][2];
        double zMin = coord[0][2];

        for (int i = 1; i < coord.length; i++) {
            xMax = Math.max(xMax, coord[i][0]);
            xMin = Math.min(xMin, coord[i][0]);
            yMax = Math.max(yMax, coord[i][1]);
            yMin = Math.min(yMin, coord[i][1]);
            zMax = Math.max(zMax, coord[i][2]);
            zMin = Math.min(zMin, coord[i][2]);
        }

        double xCenter = BUFFER_OF_CELL + 0.5 * (xMax - xMin);
        double yCenter = BUFFER_OF_CELL + 0.5 * (yMax - yMin);
        double zCenter = BUFFER_OF_CELL + 0.5 * (zMax - zMin);

        for (int i = 0; i < coord.length; i++) {
            coord[i][0] += xCenter;
            coord[i][1] += yCenter;
            coord[i][2] += zCenter;
        }

        lattice[0][0] += xMax - xMin;
        lattice[1][1] += yMax - yMin;
        lattice[2][2] += zMax - zMin;
        return lattice;
    }
}
