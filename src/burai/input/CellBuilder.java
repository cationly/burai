/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input;

import java.util.ArrayList;
import java.util.List;

import burai.atoms.model.Atom;
import burai.atoms.model.AtomProperty;
import burai.atoms.model.Cell;
import burai.atoms.model.exception.ZeroVolumCellException;
import burai.com.consts.Constants;
import burai.com.math.Matrix3D;
import burai.input.card.QEAtomicPositions;
import burai.input.card.QECard;
import burai.input.card.QECellParameters;
import burai.input.namelist.QENamelist;
import burai.input.namelist.QEValue;

public class CellBuilder {

    private static final double ALAT_LIMIT = 1.0e-12;

    private QEInput input;

    public CellBuilder(QEInput input) {
        if (input == null) {
            throw new IllegalArgumentException("input is null.");
        }

        this.input = input;
    }

    public double[][] buildLattice() {
        return this.createLattice();
    }

    public double[][] buildAngstromMatrix() {
        return this.createAngstromMatrix(null);
    }

    public Atom buildAtom(int i) {
        return this.createAtom(i, null);
    }

    public List<Atom> buildAtoms() {
        return this.createAtoms(null);
    }

    public Cell buildCell() {
        double[][] lattice = this.createLattice();
        if (lattice == null) {
            return null;
        }

        List<Atom> atoms = this.createAtoms(lattice);
        if (atoms == null) {
            return null;
        }

        Cell cell = null;
        try {
            cell = new Cell(lattice);
        } catch (ZeroVolumCellException e) {
            e.printStackTrace();
            return null;
        }

        for (Atom atom : atoms) {
            cell.addAtom(atom);
        }

        return cell;
    }

    private double[][] createLattice() {
        QENamelist nmlSystem = this.input.getNamelist(QEInput.NAMELIST_SYSTEM);
        if (nmlSystem == null) {
            return null;
        }

        int ibrav = this.getInteger(nmlSystem, "ibrav", 14);

        double[] celldm = new double[6];
        celldm[0] = this.getDouble(nmlSystem, "celldm(1)", 0.0);
        celldm[1] = this.getDouble(nmlSystem, "celldm(2)", 0.0);
        celldm[2] = this.getDouble(nmlSystem, "celldm(3)", 0.0);
        celldm[3] = this.getDouble(nmlSystem, "celldm(4)", 0.0);
        celldm[4] = this.getDouble(nmlSystem, "celldm(5)", 0.0);
        celldm[5] = this.getDouble(nmlSystem, "celldm(6)", 0.0);

        if (Math.abs(celldm[0]) < ALAT_LIMIT) {
            double a = this.getDouble(nmlSystem, "a", 0.0);
            double b = this.getDouble(nmlSystem, "b", 0.0);
            double c = this.getDouble(nmlSystem, "c", 0.0);
            double cosAlpha = this.getDouble(nmlSystem, "cosbc", 0.0);
            double cosBeta = this.getDouble(nmlSystem, "cosac", 0.0);
            double cosGamma = this.getDouble(nmlSystem, "cosab", 0.0);
            celldm[0] = a / Constants.BOHR_RADIUS_ANGS;
            celldm[1] = b / a;
            celldm[2] = c / a;
            celldm[3] = cosAlpha;
            celldm[4] = cosBeta;
            celldm[5] = cosGamma;
        }

        if (ibrav == 0) {
            return this.createLatticeIBrav0(celldm[0]);
        }

        return this.createLatticeIBrav14(ibrav, celldm);
    }

    private double[][] createLatticeIBrav0(double alat) {
        QECard card = this.input.getCard(QECellParameters.CARD_NAME);
        if (card == null || !(card instanceof QECellParameters)) {
            return null;
        }

        QECellParameters cellParameters = (QECellParameters) card;

        double unit = 1.0;
        if (cellParameters.isAlat()) {
            unit = alat * Constants.BOHR_RADIUS_ANGS;
        } else if (cellParameters.isBohr()) {
            unit = Constants.BOHR_RADIUS_ANGS;
        } else if (cellParameters.isAngstrom()) {
            unit = 1.0;
        } else {
            if (Math.abs(alat) < ALAT_LIMIT) {
                unit = Constants.BOHR_RADIUS_ANGS;
            } else {
                unit = alat * Constants.BOHR_RADIUS_ANGS;
            }
        }

        double[][] lattice = new double[3][];
        lattice[0] = cellParameters.getVector1();
        lattice[1] = cellParameters.getVector2();
        lattice[2] = cellParameters.getVector3();
        lattice = Matrix3D.mult(unit, lattice);

        return lattice;
    }

    private double[][] createLatticeIBrav14(int ibrav, double[] celldm) {
        if (celldm[0] == 0.0) {
            return null;
        }

        double[][] lattice = Matrix3D.zero();

        double term1;
        double term2;
        double root2;
        double root3;

        switch (ibrav) {
        case 1:
            lattice[0][0] = celldm[0];
            lattice[1][1] = celldm[0];
            lattice[2][2] = celldm[0];
            break;

        case 2:
            term1 = 0.5 * celldm[0];
            lattice[0][0] = -term1;
            lattice[0][2] = term1;
            lattice[1][1] = term1;
            lattice[1][2] = term1;
            lattice[2][0] = -term1;
            lattice[2][1] = term1;
            break;

        case 3:
            term1 = 0.5 * celldm[0];
            for (int i = 0; i < 3; i++) {
                lattice[0][i] = term1;
                lattice[1][i] = term1;
                lattice[2][i] = term1;
            }
            lattice[1][0] = -term1;
            lattice[2][0] = -term1;
            lattice[2][1] = -term1;
            break;

        case 4:
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = celldm[0];
            lattice[1][0] = -celldm[0] / 2.0;
            lattice[1][1] = celldm[0] * Math.sqrt(3.0) / 2.0;
            lattice[2][2] = celldm[0] * celldm[2];
            break;

        case 5:
            if (celldm[3] <= -0.5 || celldm[3] >= 1.0) {
                lattice = null;
                break;
            }
            term1 = Math.sqrt(1.0 + 2.0 * celldm[3]);
            term2 = Math.sqrt(1.0 - celldm[3]);
            root2 = Math.sqrt(2.0);
            root3 = Math.sqrt(3.0);
            lattice[1][1] = root2 * celldm[0] * term2 / root3;
            lattice[1][2] = celldm[0] * term1 / root3;
            lattice[0][0] = celldm[0] * term2 / root2;
            lattice[0][1] = -lattice[0][0] / root3;
            lattice[0][2] = lattice[1][2];
            lattice[2][0] = -lattice[0][0];
            lattice[2][1] = lattice[0][1];
            lattice[2][2] = lattice[1][2];
            break;

        case -5:
            if (celldm[3] <= -0.5 || celldm[3] >= 1.0) {
                lattice = null;
                break;
            }
            term1 = Math.sqrt(1.0 + 2.0 * celldm[3]);
            term2 = Math.sqrt(1.0 - celldm[3]);
            lattice[0][0] = celldm[0] * (term1 - 2.0 * term2) / 3.0;
            lattice[0][1] = celldm[0] * (term1 + term2) / 3.0;
            lattice[0][2] = lattice[0][1];
            lattice[1][0] = lattice[0][2];
            lattice[1][1] = lattice[0][0];
            lattice[1][2] = lattice[0][1];
            lattice[2][0] = lattice[0][1];
            lattice[2][1] = lattice[0][2];
            lattice[2][2] = lattice[0][0];
            break;

        case 6:
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = celldm[0];
            lattice[1][1] = celldm[0];
            lattice[2][2] = celldm[0] * celldm[2];
            break;

        case 7:
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            lattice[1][0] = celldm[0] / 2.0;
            lattice[1][1] = lattice[1][0];
            lattice[1][2] = celldm[2] * celldm[0] / 2.0;
            lattice[0][0] = lattice[1][0];
            lattice[0][1] = -lattice[1][0];
            lattice[0][2] = lattice[1][2];
            lattice[2][0] = -lattice[1][0];
            lattice[2][1] = -lattice[1][0];
            lattice[2][2] = lattice[1][2];
            break;

        case 8:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = celldm[0];
            lattice[1][1] = celldm[0] * celldm[1];
            lattice[2][2] = celldm[0] * celldm[2];
            break;

        case 9:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = 0.5 * celldm[0];
            lattice[0][1] = lattice[0][0] * celldm[1];
            lattice[1][0] = -lattice[0][0];
            lattice[1][1] = lattice[0][1];
            lattice[2][2] = celldm[0] * celldm[2];
            break;

        case -9:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = 0.5 * celldm[0];
            lattice[0][1] = -lattice[0][0] * celldm[1];
            lattice[1][0] = lattice[0][0];
            lattice[1][1] = -lattice[0][1];
            lattice[2][2] = celldm[0] * celldm[2];
            break;

        case 91:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = celldm[0];
            lattice[1][1] = celldm[0] * celldm[1] * 0.5;
            lattice[1][2] = -celldm[0] * celldm[2] * 0.5;
            lattice[2][1] = lattice[1][1];
            lattice[2][2] = -lattice[1][2];
            break;

        case 10:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            lattice[1][0] = 0.5 * celldm[0];
            lattice[1][1] = lattice[1][0] * celldm[1];
            lattice[0][0] = lattice[1][0];
            lattice[0][2] = lattice[1][0] * celldm[2];
            lattice[2][1] = lattice[1][0] * celldm[1];
            lattice[2][2] = lattice[0][2];
            break;

        case 11:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = 0.5 * celldm[0];
            lattice[0][1] = lattice[0][0] * celldm[1];
            lattice[0][2] = lattice[0][0] * celldm[2];
            lattice[1][0] = -lattice[0][0];
            lattice[1][1] = lattice[0][1];
            lattice[1][2] = lattice[0][2];
            lattice[2][0] = -lattice[0][0];
            lattice[2][1] = -lattice[0][1];
            lattice[2][2] = lattice[0][2];
            break;

        case 12:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            if (Math.abs(celldm[3]) >= 1.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = celldm[0];
            lattice[1][0] = celldm[0] * celldm[1] * celldm[3];
            lattice[1][1] = celldm[0] * celldm[1] * Math.sqrt(1.0 - celldm[3] * celldm[3]);
            lattice[2][2] = celldm[0] * celldm[2];
            break;

        case -12:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            if (Math.abs(celldm[4]) >= 1.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = celldm[0];
            lattice[1][1] = celldm[0] * celldm[1];
            lattice[2][0] = celldm[0] * celldm[2] * celldm[4];
            lattice[2][2] = celldm[0] * celldm[2] * Math.sqrt(1.0 - celldm[4] * celldm[4]);
            break;

        case 13:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            if (Math.abs(celldm[3]) >= 1.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = 0.5 * celldm[0];
            lattice[0][2] = -lattice[0][0] * celldm[2];
            lattice[1][0] = celldm[0] * celldm[1] * celldm[3];
            lattice[1][1] = celldm[0] * celldm[1] * Math.sqrt(1.0 - celldm[3] * celldm[3]);
            lattice[2][0] = lattice[0][0];
            lattice[2][2] = -lattice[0][2];
            break;

        case -13:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            if (Math.abs(celldm[4]) >= 1.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = 0.5 * celldm[0];
            lattice[0][1] = -lattice[0][0] * celldm[1];
            lattice[1][0] = lattice[0][0];
            lattice[1][1] = -lattice[0][1];
            lattice[2][0] = celldm[0] * celldm[2] * celldm[4];
            lattice[2][2] = celldm[0] * celldm[2] * Math.sqrt(1.0 - celldm[4] * celldm[4]);
            break;

        case 14:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            if (Math.abs(celldm[3]) >= 1.0) {
                lattice = null;
                break;
            }
            if (Math.abs(celldm[4]) >= 1.0) {
                lattice = null;
                break;
            }
            if (Math.abs(celldm[5]) >= 1.0) {
                lattice = null;
                break;
            }
            term1 = Math.sqrt(1.0 - celldm[5] * celldm[5]);
            term2 = 1.0 + 2.0 * celldm[3] * celldm[4] * celldm[5];
            term2 += -celldm[3] * celldm[3] - celldm[4] * celldm[4] - celldm[5] * celldm[5];
            if (term2 < 0.0) {
                lattice = null;
                break;
            }
            term2 = Math.sqrt(term2 / (1.0 - celldm[5] * celldm[5]));
            lattice[0][0] = celldm[0];
            lattice[1][0] = celldm[0] * celldm[1] * celldm[5];
            lattice[1][1] = celldm[0] * celldm[1] * term1;
            lattice[2][0] = celldm[0] * celldm[2] * celldm[4];
            lattice[2][1] = celldm[0] * celldm[2] * (celldm[3] - celldm[4] * celldm[5]) / term1;
            lattice[2][2] = celldm[0] * celldm[2] * term2;
            break;

        default:
            lattice = null;
            break;
        }

        if (lattice != null) {
            lattice = Matrix3D.mult(Constants.BOHR_RADIUS_ANGS, lattice);
        }

        return lattice;
    }

    private int getInteger(QENamelist namelist, String key, int defaultValue) {
        QEValue qeValue = namelist.getValue(key);
        if (qeValue == null) {
            return defaultValue;
        }

        return qeValue.getIntegerValue();
    }

    private double getDouble(QENamelist namelist, String key, double defaultValue) {
        QEValue qeValue = namelist.getValue(key);
        if (qeValue == null) {
            return defaultValue;
        }

        return qeValue.getRealValue();
    }

    private double createAtomsUnit(double[][] lattice) {
        QECard card = this.input.getCard(QEAtomicPositions.CARD_NAME);
        if (card == null || !(card instanceof QEAtomicPositions)) {
            return -1.0;
        }

        double unit = 1.0;
        QEAtomicPositions atomicPositions = (QEAtomicPositions) card;

        if (atomicPositions.isAlat()) {
            unit = this.getAlat(lattice);
        } else if (atomicPositions.isBohr()) {
            unit = Constants.BOHR_RADIUS_ANGS;
        } else if (atomicPositions.isAngstrom()) {
            unit = 1.0;
        } else if (atomicPositions.isCrystal()) {
            unit = 1.0;
        } else {
            unit = this.getAlat(lattice);
        }

        return unit;
    }

    private double[][] createAngstromMatrix(double[][] lattice) {
        QECard card = this.input.getCard(QEAtomicPositions.CARD_NAME);
        if (card == null || !(card instanceof QEAtomicPositions)) {
            return null;
        }

        double[][] matrix = null;
        QEAtomicPositions atomicPositions = (QEAtomicPositions) card;

        if (atomicPositions.isCrystal()) {
            double[][] lattice2 = lattice;
            if (lattice2 == null) {
                lattice2 = this.createLattice();
            }
            if (lattice2 != null) {
                matrix = Matrix3D.trans(lattice2);
            }

        } else {
            double[][] lattice2 = lattice;
            if (lattice2 == null) {
                lattice2 = this.createLattice();
            }
            double unit = -1.0;
            if (lattice2 != null) {
                unit = this.createAtomsUnit(lattice2);
            }
            if (unit > 0.0) {
                matrix = Matrix3D.unit(unit);
            }
        }

        return matrix;
    }

    private Atom createAtom(int i, double[][] lattice) {
        QECard card = this.input.getCard(QEAtomicPositions.CARD_NAME);
        if (card == null || !(card instanceof QEAtomicPositions)) {
            return null;
        }

        QEAtomicPositions atomicPositions = (QEAtomicPositions) card;

        if (i < 0 || atomicPositions.numPositions() <= i) {
            return null;
        }

        double[][] matrix = this.createAngstromMatrix(lattice);

        return this.getAtom(atomicPositions, i, matrix);
    }

    private List<Atom> createAtoms(double[][] lattice) {
        QECard card = this.input.getCard(QEAtomicPositions.CARD_NAME);
        if (card == null || !(card instanceof QEAtomicPositions)) {
            return null;
        }

        QEAtomicPositions atomicPositions = (QEAtomicPositions) card;

        double[][] matrix = this.createAngstromMatrix(lattice);

        List<Atom> atoms = new ArrayList<Atom>();

        int numAtoms = atomicPositions.numPositions();
        for (int i = 0; i < numAtoms; i++) {
            Atom atom = this.getAtom(atomicPositions, i, matrix);
            if (atom != null) {
                atoms.add(atom);
            }
        }

        return atoms;
    }

    private double getAlat(double[][] lattice) {
        QENamelist nmlSystem = this.input.getNamelist(QEInput.NAMELIST_SYSTEM);
        if (nmlSystem != null) {
            double celldm1 = this.getDouble(nmlSystem, "celldm(1)", 0.0);
            if (Math.abs(celldm1) >= ALAT_LIMIT) {
                return celldm1 * Constants.BOHR_RADIUS_ANGS;
            }
            double a = this.getDouble(nmlSystem, "a", 0.0);
            if (Math.abs(a) >= ALAT_LIMIT) {
                return a;
            }
        }

        double[][] lattice2 = lattice;
        if (lattice2 == null) {
            lattice2 = this.createLattice();
        }

        return Matrix3D.norm(lattice2[0]);
    }

    private Atom getAtom(QEAtomicPositions atomicPositions, int i, double[][] matrix) {
        if (atomicPositions == null) {
            return null;
        }

        if (i < 0 || atomicPositions.numPositions() <= i) {
            return null;
        }

        if (matrix == null) {
            return null;
        }

        String name = atomicPositions.getLabel(i);
        if (name == null || name.isEmpty()) {
            return null;
        }

        double[] coord = atomicPositions.getPosition(i);
        if (coord == null || coord.length < 3) {
            return null;
        }

        boolean[] mobile = atomicPositions.getMobile(i);
        if (mobile == null || mobile.length < 3) {
            return null;
        }

        coord = Matrix3D.mult(matrix, coord);

        Atom atom = new Atom(name, coord[0], coord[1], coord[2]);
        atom.setProperty(AtomProperty.FIXED_X, !mobile[0]);
        atom.setProperty(AtomProperty.FIXED_Y, !mobile[1]);
        atom.setProperty(AtomProperty.FIXED_Z, !mobile[2]);
        atom.setProperty(AtomProperty.INPUT_INDEX, i);

        return atom;
    }
}
