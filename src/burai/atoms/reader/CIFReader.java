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
import java.util.ArrayList;
import java.util.List;

import burai.atoms.model.Cell;
import burai.atoms.model.exception.ZeroVolumCellException;
import burai.atoms.reader.cif.CIFLoopElement;
import burai.atoms.reader.cif.CIFLoopValue;
import burai.atoms.reader.cif.CIFSingleValue;
import burai.atoms.reader.cif.CIFSymmetricOperator;
import burai.atoms.reader.cif.IncorrectCIFSymmetricException;

public class CIFReader extends AtomsReader {

    private static final double RMIN = 1.0e-4;

    private static final String NAME_LENGTH_A = "_cell_length_a";
    private static final String NAME_LENGTH_B = "_cell_length_b";
    private static final String NAME_LENGTH_C = "_cell_length_c";
    private static final String NAME_ANGLE_ALPHA = "_cell_angle_alpha";
    private static final String NAME_ANGLE_BETA = "_cell_angle_beta";
    private static final String NAME_ANGLE_GAMMA = "_cell_angle_gamma";
    private static final String NAME_SYMMETRY_ID1 = "_symmetry_equiv_pos_site_id";
    private static final String NAME_SYMMETRY_XYZ1 = "_symmetry_equiv_pos_as_xyz";
    private static final String NAME_SYMMETRY_ID2 = "_space_group_symop_id";
    private static final String NAME_SYMMETRY_XYZ2 = "_space_group_symop_operation_xyz";
    private static final String NAME_ATOM_LABEL = "_atom_site_label";
    private static final String NAME_ATOM_FRACT_X = "_atom_site_fract_x";
    private static final String NAME_ATOM_FRACT_Y = "_atom_site_fract_y";
    private static final String NAME_ATOM_FRACT_Z = "_atom_site_fract_z";
    private static final String NAME_ATOM_OCCUPANCY = "_atom_site_occupancy";
    private static final String NAME_ATOM_SYMBOL = "_atom_site_type_symbol";

    private CIFSingleValue lengthA;
    private CIFSingleValue lengthB;
    private CIFSingleValue lengthC;
    private CIFSingleValue angleAlpha;
    private CIFSingleValue angleBeta;
    private CIFSingleValue angleGamma;
    private CIFLoopValue symmetry;
    private CIFLoopValue atomSite;

    public CIFReader(String fileName) throws FileNotFoundException {
        super(fileName);
        this.initializeParameters();
    }

    public CIFReader(File file) throws FileNotFoundException {
        super(file);
        this.initializeParameters();
    }

    private void initializeParameters() {
        this.lengthA = null;
        this.lengthB = null;
        this.lengthC = null;
        this.angleAlpha = null;
        this.angleBeta = null;
        this.angleGamma = null;
        this.symmetry = null;
        this.atomSite = null;
    }

    private double toDouble(String value) throws NumberFormatException {
        if (value == null || value.isEmpty()) {
            throw new NumberFormatException("value is empty.");
        }

        String value2 = value.replaceAll("[()]+", "");
        return Double.parseDouble(value2.replace('d', 'e').replace('D', 'E'));
    }

    private boolean hasAllParameters() {
        if (this.lengthA == null) {
            return false;
        }

        if (this.lengthB == null) {
            return false;
        }

        if (this.lengthC == null) {
            return false;
        }

        if (this.angleAlpha == null) {
            return false;
        }

        if (this.angleBeta == null) {
            return false;
        }

        if (this.angleGamma == null) {
            return false;
        }

        if (this.symmetry == null) {
            return false;
        }

        if (this.atomSite == null) {
            return false;
        }

        return true;
    }

    @Override
    public Cell readCell() throws IOException {
        if (this.reader == null) {
            return null;
        }

        String line = null;

        while ((line = this.reader.readLine()) != null) {
            line = line.trim();

            CIFSingleValue singleValue = new CIFSingleValue();
            if (singleValue.read(line)) {
                if (!singleValue.hasValue()) {
                    // NOP

                } else if (singleValue.isName(NAME_LENGTH_A)) {
                    if (this.lengthA == null) {
                        this.lengthA = singleValue;
                    }

                } else if (singleValue.isName(NAME_LENGTH_B)) {
                    if (this.lengthB == null) {
                        this.lengthB = singleValue;
                    }

                } else if (singleValue.isName(NAME_LENGTH_C)) {
                    if (this.lengthC == null) {
                        this.lengthC = singleValue;
                    }

                } else if (singleValue.isName(NAME_ANGLE_ALPHA)) {
                    if (this.angleAlpha == null) {
                        this.angleAlpha = singleValue;
                    }

                } else if (singleValue.isName(NAME_ANGLE_BETA)) {
                    if (this.angleBeta == null) {
                        this.angleBeta = singleValue;
                    }

                } else if (singleValue.isName(NAME_ANGLE_GAMMA)) {
                    if (this.angleGamma == null) {
                        this.angleGamma = singleValue;
                    }
                }

                continue;
            }

            CIFLoopValue loopValue = new CIFLoopValue();
            if (loopValue.readHeader(line)) {
                if (!loopValue.readBody(reader)) {
                    continue;
                }

                if (!loopValue.hasElements()) {
                    // NOP

                } else if (loopValue.containsName(NAME_SYMMETRY_ID1)
                        || loopValue.containsName(NAME_SYMMETRY_ID2)
                        || loopValue.containsName(NAME_SYMMETRY_XYZ1)
                        || loopValue.containsName(NAME_SYMMETRY_XYZ2)) {
                    if (this.symmetry == null) {
                        this.symmetry = loopValue;
                    }

                } else if (loopValue.containsName(NAME_ATOM_LABEL)) {
                    if (this.atomSite == null) {
                        this.atomSite = loopValue;
                    }
                }

                continue;
            }
        }

        if (!this.hasAllParameters()) {
            throw new IOException("cannot obtain enough data from CIF file.");
        }

        double[][] lattice = new double[3][3];
        List<String> atomNames = new ArrayList<String>();
        List<double[]> atomCoords = new ArrayList<double[]>();

        try {
            this.createLatticeVector(lattice);
            this.createAtoms(atomNames, atomCoords);
        } catch (RuntimeException e) {
            throw new IOException(e);
        }

        Cell cell = null;
        try {
            cell = new Cell(lattice);
        } catch (ZeroVolumCellException e) {
            throw new IOException(e);
        }

        int numAtoms = atomNames.size();
        for (int i = 0; i < numAtoms; i++) {
            String name = atomNames.get(i);
            if (name == null || name.trim().isEmpty()) {
                continue;
            }
            double[] coord = atomCoords.get(i);
            if (coord == null || coord.length < 3) {
                continue;
            }
            if (!cell.hasAtomAt(coord[0], coord[1], coord[2])) {
                cell.addAtom(name, coord[0], coord[1], coord[2]);
            }
        }

        return cell;
    }

    private void createLatticeVector(double[][] lattice) throws RuntimeException {
        double a = 0.0;
        double b = 0.0;
        double c = 0.0;
        double alpha = 0.0;
        double beta = 0.0;
        double gamma = 0.0;

        try {
            a = this.toDouble(this.lengthA.getValue());
            b = this.toDouble(this.lengthB.getValue());
            c = this.toDouble(this.lengthC.getValue());
            alpha = this.toDouble(this.angleAlpha.getValue());
            beta = this.toDouble(this.angleBeta.getValue());
            gamma = this.toDouble(this.angleGamma.getValue());
        } catch (NumberFormatException e) {
            throw new RuntimeException("cannot construct lattice vector from CIF file.");
        }

        double cosAlpha = Math.cos((Math.PI / 180.0) * alpha);
        double cosBeta = Math.cos((Math.PI / 180.0) * beta);
        double cosGamma = Math.cos((Math.PI / 180.0) * gamma);
        double sinGamma = Math.sin((Math.PI / 180.0) * gamma);

        if (sinGamma == 0.0) {
            throw new RuntimeException("sin(gamma) is zero, in reading CIF file.");
        }

        lattice[0][0] = a;
        lattice[0][1] = 0.0;
        lattice[0][2] = 0.0;

        lattice[1][0] = b * cosGamma;
        lattice[1][1] = b * sinGamma;
        lattice[1][2] = 0.0;

        lattice[2][0] = c * cosBeta;
        lattice[2][1] = c * (cosAlpha - cosBeta * cosGamma) / sinGamma;
        lattice[2][2] = c * Math.sqrt(1.0 + 2.0 * cosAlpha * cosBeta * cosGamma
                - cosAlpha * cosAlpha - cosBeta * cosBeta - cosGamma * cosGamma) / sinGamma;
    }

    private void createAtoms(List<String> atomNames, List<double[]> atomCoords) throws RuntimeException {
        atomNames.clear();
        atomCoords.clear();

        List<String> primNames = new ArrayList<String>();
        List<double[]> primCoords = new ArrayList<double[]>();
        this.createPrimitiveAtoms(primNames, primCoords);

        List<CIFSymmetricOperator> operators = new ArrayList<CIFSymmetricOperator>();
        this.createSymmetricOperator(operators);

        for (int i = 0; i < primNames.size(); i++) {
            String name = primNames.get(i);
            double[] coord = primCoords.get(i);

            for (CIFSymmetricOperator operator : operators) {
                atomNames.add(name);
                atomCoords.add(operator.operate(coord));
            }
        }
    }

    private void createSymmetricOperator(List<CIFSymmetricOperator> operators) throws RuntimeException {
        operators.clear();

        CIFLoopElement[] symmetryElements = null;
        if (this.symmetry != null) {
            symmetryElements = this.symmetry.listElements();
        }

        if (symmetryElements == null || symmetryElements.length < 1) {
            throw new RuntimeException("elements of symmetry are empty, in reading CIF file.");
        }

        for (CIFLoopElement symmetryElement : symmetryElements) {
            String strOperator = symmetryElement.getValue(NAME_SYMMETRY_XYZ1);
            if (strOperator == null) {
                strOperator = symmetryElement.getValue(NAME_SYMMETRY_XYZ2);
            }
            if (strOperator == null) {
                throw new RuntimeException("incorrect data of symmetry, in reading CIF file.");
            }

            CIFSymmetricOperator operator = null;
            try {
                operator = new CIFSymmetricOperator(strOperator);
            } catch (IncorrectCIFSymmetricException e) {
                throw new RuntimeException(e);
            }
            operators.add(operator);
        }
    }

    private void createPrimitiveAtoms(List<String> atomNames, List<double[]> atomCoords) throws RuntimeException {
        atomNames.clear();
        atomCoords.clear();

        CIFLoopElement[] atomElements = this.atomSite.listElements();
        if (atomElements == null || atomElements.length < 1) {
            throw new RuntimeException("elements of atomic site are empty, in reading CIF file.");
        }

        String[] symbolList = new String[atomElements.length];
        double[] xList = new double[atomElements.length];
        double[] yList = new double[atomElements.length];
        double[] zList = new double[atomElements.length];
        double[] occList = new double[atomElements.length];

        for (int i = 0; i < atomElements.length; i++) {
            CIFLoopElement atomElement = atomElements[i];

            String labelValue = atomElement.getValue(NAME_ATOM_LABEL);
            String symbolValue = atomElement.getValue(NAME_ATOM_SYMBOL);
            String xValue = atomElement.getValue(NAME_ATOM_FRACT_X);
            String yValue = atomElement.getValue(NAME_ATOM_FRACT_Y);
            String zValue = atomElement.getValue(NAME_ATOM_FRACT_Z);
            String occValue = atomElement.getValue(NAME_ATOM_OCCUPANCY);
            if (labelValue == null || xValue == null || yValue == null || zValue == null) {
                throw new RuntimeException("incorrect data of atomic site, in reading CIF file.");
            }

            String symbol = "";
            double x = 0.0;
            double y = 0.0;
            double z = 0.0;
            double occ = 1.0;

            symbol = symbolValue;
            if (symbol == null) {
                String[] subSymbols = labelValue.split("[\\s,:;\\(\\)\\[\\]\\{\\}]+");
                if (subSymbols == null || subSymbols.length < 1) {
                    throw new RuntimeException("incorrect data of atomic site, in reading CIF file.");
                }
                symbol = subSymbols[0];
            }

            try {
                x = this.toDouble(xValue);
                y = this.toDouble(yValue);
                z = this.toDouble(zValue);
                if (occValue != null) {
                    occ = this.toDouble(occValue);
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException("incorrect data of atomic site, in reading CIF file.");
            }

            symbolList[i] = symbol;
            xList[i] = x;
            yList[i] = y;
            zList[i] = z;
            occList[i] = occ;
        }

        for (int i = 0; i < atomElements.length; i++) {
            CIFLoopElement atomElement1 = atomElements[i];
            if (atomElement1 == null) {
                continue;
            }
            double x1 = xList[i];
            double y1 = yList[i];
            double z1 = zList[i];
            double occ1 = occList[i];

            for (int j = (i + 1); j < atomElements.length; j++) {
                CIFLoopElement atomElement2 = atomElements[j];
                if (atomElement2 == null) {
                    continue;
                }
                double x2 = xList[j];
                double y2 = yList[j];
                double z2 = zList[j];
                double occ2 = occList[j];

                double dx = x2 - x1;
                double dy = y2 - y1;
                double dz = z2 - z1;
                double rr = dx * dx + dy * dy + dz * dz;
                if (rr < RMIN * RMIN) {
                    if (occ1 < occ2) {
                        atomElements[i] = null;
                    } else {
                        atomElements[j] = null;
                    }
                }
            }
        }

        for (int i = 0; i < atomElements.length; i++) {
            CIFLoopElement atomElement = atomElements[i];
            if (atomElement == null) {
                continue;
            }
            String name = symbolList[i];
            double[] coord = { xList[i], yList[i], zList[i] };
            atomNames.add(name);
            atomCoords.add(coord);
        }
    }
}
