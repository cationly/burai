/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.com.math;

public final class Lattice {

    private Lattice() {
        // NOP
    }

    public static double getA(double[][] cell) {
        if (cell == null || cell.length < 3) {
            return -1.0;
        }

        if (cell[0] == null || cell[0].length < 3) {
            return -1.0;
        }

        return Matrix3D.norm(cell[0]);
    }

    public static double getB(double[][] cell) {
        if (cell == null || cell.length < 3) {
            return -1.0;
        }

        if (cell[1] == null || cell[1].length < 3) {
            return -1.0;
        }

        return Matrix3D.norm(cell[1]);
    }

    public static double getC(double[][] cell) {
        if (cell == null || cell.length < 3) {
            return -1.0;
        }

        if (cell[2] == null || cell[2].length < 3) {
            return -1.0;
        }

        return Matrix3D.norm(cell[2]);
    }

    public static double getAlpha(double[][] cell) {
        if (cell == null || cell.length < 3) {
            return -1.0;
        }

        if (cell[1] == null || cell[1].length < 3) {
            return -1.0;
        }

        if (cell[2] == null || cell[2].length < 3) {
            return -1.0;
        }

        double b = Matrix3D.norm(cell[1]);
        if (b <= 0.0) {
            return -1.0;
        }

        double c = Matrix3D.norm(cell[2]);
        if (c <= 0.0) {
            return -1.0;
        }

        double cosbc = Matrix3D.mult(cell[1], cell[2]) / b / c;
        return Math.acos(Math.max(-1.0, Math.min(cosbc, 1.0))) * 180.0 / Math.PI;
    }

    public static double getBeta(double[][] cell) {
        if (cell == null || cell.length < 3) {
            return -1.0;
        }

        if (cell[0] == null || cell[0].length < 3) {
            return -1.0;
        }

        if (cell[2] == null || cell[2].length < 3) {
            return -1.0;
        }

        double a = Matrix3D.norm(cell[0]);
        if (a <= 0.0) {
            return -1.0;
        }

        double c = Matrix3D.norm(cell[2]);
        if (c <= 0.0) {
            return -1.0;
        }

        double cosac = Matrix3D.mult(cell[0], cell[2]) / a / c;
        return Math.acos(Math.max(-1.0, Math.min(cosac, 1.0))) * 180.0 / Math.PI;
    }

    public static double getGamma(double[][] cell) {
        if (cell == null || cell.length < 3) {
            return -1.0;
        }

        if (cell[0] == null || cell[0].length < 3) {
            return -1.0;
        }

        if (cell[1] == null || cell[1].length < 3) {
            return -1.0;
        }

        double a = Matrix3D.norm(cell[0]);
        if (a <= 0.0) {
            return -1.0;
        }

        double b = Matrix3D.norm(cell[1]);
        if (b <= 0.0) {
            return -1.0;
        }

        double cosab = Matrix3D.mult(cell[0], cell[1]) / a / b;
        return Math.acos(Math.max(-1.0, Math.min(cosab, 1.0))) * 180.0 / Math.PI;
    }

}
