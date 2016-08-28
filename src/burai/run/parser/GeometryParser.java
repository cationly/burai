/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.run.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import burai.com.consts.Constants;
import burai.com.math.Matrix3D;
import burai.project.property.ProjectGeometry;
import burai.project.property.ProjectGeometryList;
import burai.project.property.ProjectProperty;

public class GeometryParser extends LogParser {

    private boolean mdMode;

    private ScfParser scfParser;

    private ProjectGeometryList geometryList;

    public GeometryParser(ProjectProperty property, boolean mdMode) {
        super(property);

        this.mdMode = mdMode;

        this.scfParser = new ScfParser(this.property);

        if (this.mdMode) {
            this.geometryList = this.property.getMdList();
        } else {
            this.geometryList = this.property.getOptList();
        }
    }

    @Override
    public void parse(File file) throws IOException {
        this.scfParser.parse(file);
        this.parseGeometry(file);
    }

    private void parseGeometry(File file) throws IOException {
        if (this.geometryList != null) {
            this.geometryList.clearGeometries();
        }

        try {
            this.parseGeometryKernel(file);

        } catch (IOException e) {
            if (this.geometryList != null) {
                this.geometryList.clearGeometries();
            }
            throw e;

        } finally {
            if (this.mdMode) {
                this.property.saveMdList();
            } else {
                this.property.saveOptList();
            }
        }
    }

    private void parseGeometryKernel(File file) throws IOException {

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));

            int numAtoms = this.getNumAtoms(reader);
            if (numAtoms < 1) {
                return;
            }

            double alat = this.getAlat(reader);
            if (alat <= 0.0) {
                return;
            }

            ProjectGeometry geometry = this.getFirstGeometry(reader, alat, numAtoms);
            if (geometry == null) {
                return;
            }

            if (this.geometryList != null) {
                this.geometryList.addGeometry(geometry);
            }

            for (int iter = 1; true; iter++) {
                boolean[] converged = { false };
                geometry = this.getGeometry(reader, iter, alat, numAtoms, geometry, converged);

                if (converged[0]) {
                    if (this.geometryList != null) {
                        this.geometryList.setConverged(true);
                    }
                }

                if (geometry == null) {
                    break;
                }

                if (this.geometryList != null) {
                    this.geometryList.addGeometry(geometry);
                }
            }

        } catch (FileNotFoundException e1) {
            throw e1;

        } catch (IOException e2) {
            throw e2;

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e3) {
                    throw e3;
                }
            }
        }
    }

    private int getNumAtoms(BufferedReader reader) throws IOException {
        if (reader == null) {
            return -1;
        }

        String line = null;

        int numAtoms = -1;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("number of atoms/cell")) {
                String strNumAtoms = null;
                String[] subLines = line.split("\\s+");
                if (subLines != null && subLines.length > 4) {
                    strNumAtoms = subLines[4];
                }
                if (strNumAtoms != null) {
                    try {
                        numAtoms = Integer.parseInt(strNumAtoms);
                    } catch (NumberFormatException e) {
                        numAtoms = -1;
                    }
                }

                break;
            }
        }

        return numAtoms;
    }

    private double getAlat(BufferedReader reader) throws IOException {
        if (reader == null) {
            return -1.0;
        }

        String line = null;

        double alat = -1.0;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("celldm(1)=")) {
                String strAlat = null;
                String[] subLines = line.split("\\s+");
                if (subLines != null && subLines.length > 1) {
                    strAlat = subLines[1];
                }
                if (strAlat != null) {
                    try {
                        alat = Double.parseDouble(strAlat);
                    } catch (NumberFormatException e) {
                        alat = -1.0;
                    }
                }

                break;
            }
        }

        return alat;
    }

    private ProjectGeometry getFirstGeometry(BufferedReader reader, double alat, int numAtoms) throws IOException {
        if (reader == null) {
            return null;
        }
        if (alat <= 0.0) {
            return null;
        }
        if (numAtoms < 1) {
            return null;
        }

        String line = null;

        /*
         * read cell
         */
        double[][] cell = new double[3][];
        cell[0] = null;
        cell[1] = null;
        cell[2] = null;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("crystal axes: (cart. coord. in units of alat)")) {
                String strA1 = reader.readLine();
                String[] subA1 = strA1 == null ? null : strA1.trim().split("\\s+");
                if (subA1 != null && subA1.length > 5) {
                    try {
                        cell[0] = new double[3];
                        cell[0][0] = alat * Double.parseDouble(subA1[3]);
                        cell[0][1] = alat * Double.parseDouble(subA1[4]);
                        cell[0][2] = alat * Double.parseDouble(subA1[5]);
                    } catch (Exception e) {
                        cell[0] = null;
                    }
                }

                String strA2 = reader.readLine();
                String[] subA2 = strA2 == null ? null : strA2.trim().split("\\s+");
                if (subA2 != null && subA2.length > 5) {
                    try {
                        cell[1] = new double[3];
                        cell[1][0] = alat * Double.parseDouble(subA2[3]);
                        cell[1][1] = alat * Double.parseDouble(subA2[4]);
                        cell[1][2] = alat * Double.parseDouble(subA2[5]);
                    } catch (Exception e) {
                        cell[1] = null;
                    }
                }

                String strA3 = reader.readLine();
                String[] subA3 = strA3 == null ? null : strA3.trim().split("\\s+");
                if (subA3 != null && subA3.length > 5) {
                    try {
                        cell[2] = new double[3];
                        cell[2][0] = alat * Double.parseDouble(subA3[3]);
                        cell[2][1] = alat * Double.parseDouble(subA3[4]);
                        cell[2][2] = alat * Double.parseDouble(subA3[5]);
                    } catch (Exception e) {
                        cell[2] = null;
                    }
                }

                break;
            }
        }

        if (cell[0] == null || cell[1] == null || cell[2] == null) {
            return null;
        }

        /*
         * read atoms
         */
        String[] atomLabels = new String[numAtoms];
        double[][] atomCoords = new double[numAtoms][];
        for (int i = 0; i < numAtoms; i++) {
            atomLabels[i] = null;
            atomCoords[i] = null;
        }

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("site n.     atom                  positions (alat units)")) {
                for (int i = 0; i < numAtoms; i++) {
                    String strAtom = reader.readLine();
                    String[] subAtom = strAtom == null ? null : strAtom.trim().split("\\s+");
                    if (subAtom != null && subAtom.length > 8) {
                        try {
                            atomLabels[i] = subAtom[1];
                            atomCoords[i] = new double[3];
                            atomCoords[i][0] = alat * Double.parseDouble(subAtom[6]);
                            atomCoords[i][1] = alat * Double.parseDouble(subAtom[7]);
                            atomCoords[i][2] = alat * Double.parseDouble(subAtom[8]);
                        } catch (Exception e) {
                            atomLabels[i] = null;
                            atomCoords[i] = null;
                        }
                    }
                }

                break;
            }
        }

        for (int i = 0; i < numAtoms; i++) {
            if (atomLabels[i] == null || atomLabels[i].isEmpty() || atomCoords[i] == null) {
                return null;
            }
        }

        ProjectGeometry geometry = new ProjectGeometry();
        geometry.setConverged(false);
        geometry.setTime(0.0);
        geometry.setEnergy(0.0);
        geometry.setTotalForce(0.0);
        geometry.setKinetic(0.0);
        geometry.setTemperature(0.0);
        geometry.setCell(cell);
        geometry.setStress(Matrix3D.zero());
        for (int i = 0; i < numAtoms; i++) {
            geometry.addAtom(atomLabels[i], atomCoords[i][0], atomCoords[i][1], atomCoords[i][2]);
            geometry.setForce(i, 0.0, 0.0, 0.0);
        }

        return geometry;
    }

    private ProjectGeometry getGeometry(BufferedReader reader, int iter,
            double alat, int numAtoms, ProjectGeometry prevGeometry, boolean[] converged) throws IOException {

        if (reader == null) {
            return null;
        }
        if (alat <= 0.0) {
            return null;
        }
        if (numAtoms < 1) {
            return null;
        }
        if (prevGeometry == null) {
            return null;
        }

        String line = null;

        /*
         * read total energy
         */
        double energy = 0.0;
        boolean hasEnergy = false;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if ((!line.isEmpty()) && line.charAt(0) == '!' && line.indexOf("total energy") > -1) {
                String strErg = null;
                String[] subLines = line.split("\\s+");
                if (subLines != null && subLines.length > 4) {
                    strErg = subLines[4];
                }
                if (strErg != null) {
                    try {
                        energy = Double.parseDouble(strErg);
                        hasEnergy = true;
                    } catch (NumberFormatException e) {
                        energy = 0.0;
                        hasEnergy = false;
                    }
                }

                break;
            }
        }

        if (!hasEnergy) {
            return null;
        }

        prevGeometry.setEnergy(energy);

        /*
         * read forces
         */
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("Forces acting on atoms (Ry/au):")) {
                reader.readLine();
                break;
            }
        }

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("atom")) {
                break;
            }
        }

        for (int i = 0; i < numAtoms; i++) {
            if (i > 0) {
                line = reader.readLine();
            }
            if (line == null) {
                return null;
            }
            String[] subLines = line.trim().split("\\s+");
            if (subLines == null || subLines.length < 9) {
                return null;
            }
            try {
                double fx = Double.parseDouble(subLines[6]);
                double fy = Double.parseDouble(subLines[7]);
                double fz = Double.parseDouble(subLines[8]);
                prevGeometry.setForce(i, fx, fy, fz);
            } catch (Exception e) {
                return null;
            }
        }

        /*
         * read total force
         */
        double force = 0.0;
        boolean hasForce = false;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("Total force =")) {
                String strForce = null;
                String[] subLines = line.split("\\s+");
                if (subLines != null && subLines.length > 3) {
                    strForce = subLines[3];
                }
                if (strForce != null) {
                    try {
                        force = Double.parseDouble(strForce);
                        hasForce = true;
                    } catch (NumberFormatException e) {
                        force = 0.0;
                        hasForce = false;
                    }
                }

                break;
            }
        }

        if (!hasForce) {
            return null;
        }

        prevGeometry.setTotalForce(force);

        /*
         * read stress
         */
        double[][] stress = new double[3][];
        stress[0] = null;
        stress[1] = null;
        stress[2] = null;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("SCF correction compared")) {
                continue;
            }
            if (!line.isEmpty()) {
                break;
            }
        }

        if (line != null && line.startsWith("entering subroutine stress")) {
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("total   stress")) {
                    String strS1 = reader.readLine();
                    String[] subS1 = strS1 == null ? null : strS1.trim().split("\\s+");
                    if (subS1 != null && subS1.length > 2) {
                        try {
                            stress[0] = new double[3];
                            stress[0][0] = Double.parseDouble(subS1[0]);
                            stress[0][1] = Double.parseDouble(subS1[1]);
                            stress[0][2] = Double.parseDouble(subS1[2]);
                        } catch (Exception e) {
                            stress[0] = null;
                        }
                    }

                    String strS2 = reader.readLine();
                    String[] subS2 = strS2 == null ? null : strS2.trim().split("\\s+");
                    if (subS2 != null && subS2.length > 2) {
                        try {
                            stress[1] = new double[3];
                            stress[1][0] = Double.parseDouble(subS2[0]);
                            stress[1][1] = Double.parseDouble(subS2[1]);
                            stress[1][2] = Double.parseDouble(subS2[2]);
                        } catch (Exception e) {
                            stress[1] = null;
                        }
                    }

                    String strS3 = reader.readLine();
                    String[] subS3 = strS3 == null ? null : strS3.trim().split("\\s+");
                    if (subS3 != null && subS3.length > 2) {
                        try {
                            stress[2] = new double[3];
                            stress[2][0] = Double.parseDouble(subS3[0]);
                            stress[2][1] = Double.parseDouble(subS3[1]);
                            stress[2][2] = Double.parseDouble(subS3[2]);
                        } catch (Exception e) {
                            stress[2] = null;
                        }
                    }

                    break;
                }
            }
        }

        if (stress[0] == null) {
            stress[0] = Matrix3D.zero1();
        }
        if (stress[1] == null) {
            stress[1] = Matrix3D.zero1();
        }
        if (stress[2] == null) {
            stress[2] = Matrix3D.zero1();
        }

        prevGeometry.setStress(stress);

        /*
         * read time
         */
        double time = 0.0;
        boolean hasTime = false;

        if (this.mdMode) {
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Entering Dynamics")) {
                    for (int i = 0; i < 2; i++) {
                        String[] subLines = line == null ? null : line.split("\\s+");
                        if (subLines != null && subLines.length > 1 && "pico-seconds".equals(subLines[subLines.length - 1])) {
                            try {
                                time = Double.parseDouble(subLines[subLines.length - 2]);
                                hasTime = true;
                            } catch (Exception e) {
                                time = 0.0;
                                hasTime = false;
                            }
                            break;
                        }

                        line = reader.readLine();
                        line = line == null ? null : line.trim();
                    }

                    break;
                }
            }

        } else {
            time = (double) iter;
            hasTime = true;
        }

        if (!hasTime) {
            return null;
        }

        prevGeometry.setTime(time);

        if (!this.mdMode) {
            prevGeometry.setKinetic(0.0);
            prevGeometry.setTemperature(0.0);
            prevGeometry.setConverged(true);
        }

        /*
         * read cell, atoms, kinetic energy and temperature
         */
        double alat2 = alat;

        String unitCell = null;
        double[][] cell = new double[3][];
        cell[0] = null;
        cell[1] = null;
        cell[2] = null;

        String unitAtom = null;
        String[] atomLabels = new String[numAtoms];
        double[][] atomCoords = new double[numAtoms][];
        for (int i = 0; i < numAtoms; i++) {
            atomLabels[i] = null;
            atomCoords[i] = null;
        }

        double kinetic = 0.0;
        double temperature = 0.0;

        final int lengthQueue = 16;
        LinkedList<String> lineQueue = new LinkedList<String>();

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            String[] subLines = null;

            if (!this.mdMode) {
                if (line.startsWith("Begin final coordinates")) {
                    if (converged != null && converged.length > 0) {
                        converged[0] = true;
                    }
                }
            }

            if (line.startsWith("ATOMIC_POSITIONS")) {
                // read atoms
                line = line.replace('(', ' ');
                line = line.replace(')', ' ');
                line = line.replace('=', ' ');
                subLines = line.split("\\s+");
                if (subLines != null && subLines.length > 1) {
                    unitAtom = subLines[1];
                }

                for (int i = 0; i < numAtoms; i++) {
                    String strAtom = reader.readLine();
                    String[] subAtom = strAtom == null ? null : strAtom.trim().split("\\s+");
                    if (subAtom != null && subAtom.length > 3) {
                        try {
                            atomLabels[i] = subAtom[0];
                            atomCoords[i] = new double[3];
                            atomCoords[i][0] = Double.parseDouble(subAtom[1]);
                            atomCoords[i][1] = Double.parseDouble(subAtom[2]);
                            atomCoords[i][2] = Double.parseDouble(subAtom[3]);
                        } catch (Exception e) {
                            atomLabels[i] = null;
                            atomCoords[i] = null;
                        }
                    }
                }

                // read kinetic energy and temperature
                if (this.mdMode) {
                    for (int i = 0; i < lineQueue.size(); i++) {
                        line = lineQueue.get(i);
                        if (line != null && line.startsWith("Ekin")) {
                            subLines = line.split("\\s+");
                            if (subLines != null && subLines.length > 2) {
                                try {
                                    kinetic = Double.parseDouble(subLines[2]);
                                } catch (Exception e) {
                                    kinetic = 0.0;
                                }
                            }
                            if (subLines != null && subLines.length > 6) {
                                try {
                                    temperature = Double.parseDouble(subLines[6]);
                                } catch (Exception e) {
                                    temperature = 0.0;
                                }
                            }

                            break;
                        }
                    }
                }

                // read cell
                while ((line = lineQueue.pollFirst()) != null) {
                    if (line.startsWith("CELL_PARAMETERS")) {
                        break;
                    }
                }

                if (line != null) {
                    line = line.replace('(', ' ');
                    line = line.replace(')', ' ');
                    line = line.replace('=', ' ');
                    subLines = line.split("\\s+");
                    if (subLines != null && subLines.length > 1) {
                        unitCell = subLines[1];
                    }
                    if (subLines != null && subLines.length > 2) {
                        String strAlat = subLines[2];
                        if (strAlat != null) {
                            try {
                                alat2 = Double.parseDouble(strAlat);
                            } catch (NumberFormatException e) {
                                alat2 = alat;
                            }
                        }
                    }

                    String strA1 = lineQueue.pollFirst();
                    String[] subA1 = strA1 == null ? null : strA1.trim().split("\\s+");
                    if (subA1 != null && subA1.length > 2) {
                        try {
                            cell[0] = new double[3];
                            cell[0][0] = Double.parseDouble(subA1[0]);
                            cell[0][1] = Double.parseDouble(subA1[1]);
                            cell[0][2] = Double.parseDouble(subA1[2]);
                        } catch (Exception e) {
                            cell[0] = null;
                        }
                    }

                    String strA2 = lineQueue.pollFirst();
                    String[] subA2 = strA2 == null ? null : strA2.trim().split("\\s+");
                    if (subA2 != null && subA2.length > 2) {
                        try {
                            cell[1] = new double[3];
                            cell[1][0] = Double.parseDouble(subA2[0]);
                            cell[1][1] = Double.parseDouble(subA2[1]);
                            cell[1][2] = Double.parseDouble(subA2[2]);
                        } catch (Exception e) {
                            cell[1] = null;
                        }
                    }

                    String strA3 = lineQueue.pollFirst();
                    String[] subA3 = strA3 == null ? null : strA3.trim().split("\\s+");
                    if (subA3 != null && subA3.length > 2) {
                        try {
                            cell[2] = new double[3];
                            cell[2][0] = Double.parseDouble(subA3[0]);
                            cell[2][1] = Double.parseDouble(subA3[1]);
                            cell[2][2] = Double.parseDouble(subA3[2]);
                        } catch (Exception e) {
                            cell[2] = null;
                        }
                    }
                }

                break;
            }

            lineQueue.offerLast(line);
            if (lineQueue.size() > lengthQueue) {
                lineQueue.pollFirst();
            }
        }

        // modify cell
        if (cell[0] == null || cell[1] == null || cell[2] == null) {
            cell = Matrix3D.copy(prevGeometry.getCell());

        } else {
            double scale = 1.0;
            if ("bohr".equalsIgnoreCase(unitCell)) {
                scale = 1.0;
            } else if ("angstrom".equalsIgnoreCase(unitCell)) {
                scale = 1.0 / Constants.BOHR_RADIUS_ANGS;
            } else { // alat
                scale = alat2;
            }
            cell = Matrix3D.mult(scale, cell);
        }

        if (cell == null) {
            return null;
        }

        // modify atoms
        for (int i = 0; i < numAtoms; i++) {
            if (atomLabels[i] == null || atomLabels[i].isEmpty() || atomCoords[i] == null) {
                return null;
            }
        }

        if ("bohr".equalsIgnoreCase(unitAtom)) {
            // NOP
        } else if ("angstrom".equalsIgnoreCase(unitAtom)) {
            for (int i = 0; i < numAtoms; i++) {
                atomCoords[i] = Matrix3D.mult(1.0 / Constants.BOHR_RADIUS_ANGS, atomCoords[i]);
            }
        } else if ("crystal".equalsIgnoreCase(unitAtom)) {
            for (int i = 0; i < numAtoms; i++) {
                atomCoords[i] = Matrix3D.mult(atomCoords[i], cell);
            }
        } else { // alat
            for (int i = 0; i < numAtoms; i++) {
                atomCoords[i] = Matrix3D.mult(alat2, atomCoords[i]);
            }
        }

        // read kinetic energy and temperature
        if (this.mdMode) {
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    break;
                }
            }

            if (line != null && line.startsWith("kinetic energy")) {
                String strKin = null;
                String[] subLines = line.split("\\s+");
                if (subLines != null && subLines.length > 4) {
                    strKin = subLines[4];
                }
                if (strKin != null) {
                    try {
                        kinetic = Double.parseDouble(strKin);
                    } catch (NumberFormatException e) {
                        kinetic = 0.0;
                    }
                }
            }

            line = reader.readLine();
            line = line == null ? null : line.trim();
            if (line != null && line.startsWith("temperature")) {
                String strTemp = null;
                String[] subLines = line.split("\\s+");
                if (subLines != null && subLines.length > 2) {
                    strTemp = subLines[2];
                }
                if (strTemp != null) {
                    try {
                        temperature = Double.parseDouble(strTemp);
                    } catch (NumberFormatException e) {
                        temperature = 0.0;
                    }
                }
            }
        }

        if (this.mdMode) {
            prevGeometry.setKinetic(kinetic);
            prevGeometry.setTemperature(temperature);
            prevGeometry.setConverged(true);
        }

        /*
         * create a ProjectGeometry
         */
        ProjectGeometry geometry = new ProjectGeometry();
        geometry.setConverged(false);
        geometry.setTime(0.0);
        geometry.setEnergy(0.0);
        geometry.setTotalForce(0.0);
        geometry.setKinetic(0.0);
        geometry.setTemperature(0.0);
        geometry.setCell(cell);
        geometry.setStress(Matrix3D.zero());
        for (int i = 0; i < numAtoms; i++) {
            geometry.addAtom(atomLabels[i], atomCoords[i][0], atomCoords[i][1], atomCoords[i][2]);
            geometry.setForce(i, 0.0, 0.0, 0.0);
        }

        return geometry;
    }
}
