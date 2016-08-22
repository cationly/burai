/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.com.math;

public final class Matrix3D {

    private static final double MIN_DET = 1.0e-20;

    private Matrix3D() {
        // NOP
    }

    private static boolean checkMatrix(double[][] matrix) {
        if (matrix == null || matrix.length < 3) {
            return false;
        }

        for (int i = 0; i < 3; i++) {
            if (matrix[i] == null || matrix[i].length < 3) {
                return false;
            }
        }

        return true;
    }

    private static boolean checkMatrix(double[] matrix) {
        if (matrix == null || matrix.length < 3) {
            return false;
        }

        return true;
    }

    public static double[] zero1() {
        double[] matrix = new double[3];
        for (int i = 0; i < 3; i++) {
            matrix[i] = 0.0;
        }

        return matrix;
    }

    public static double[][] zero() {
        double[][] matrix = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                matrix[i][j] = 0.0;
            }
        }

        return matrix;
    }

    public static double[][] unit() {
        return unit(1.0);
    }

    public static double[][] unit(double alpha) {
        double[][] matrix = zero();
        matrix[0][0] = alpha;
        matrix[1][1] = alpha;
        matrix[2][2] = alpha;

        return matrix;
    }

    public static double[] plus(double[] matrix1, double[] matrix2) {
        if (!(checkMatrix(matrix1) && checkMatrix(matrix2))) {
            return null;
        }

        double[] matrix3 = new double[3];
        for (int i = 0; i < 3; i++) {
            matrix3[i] = matrix1[i] + matrix2[i];
        }

        return matrix3;
    }

    public static double[][] plus(double[][] matrix1, double[][] matrix2) {
        if (!(checkMatrix(matrix1) && checkMatrix(matrix2))) {
            return null;
        }

        double[][] matrix3 = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                matrix3[i][j] = matrix1[i][j] + matrix2[i][j];
            }
        }

        return matrix3;
    }

    public static double[] minus(double[] matrix1, double[] matrix2) {
        if (!(checkMatrix(matrix1) && checkMatrix(matrix2))) {
            return null;
        }

        double[] matrix3 = new double[3];
        for (int i = 0; i < 3; i++) {
            matrix3[i] = matrix1[i] - matrix2[i];
        }

        return matrix3;
    }

    public static double[][] minus(double[][] matrix1, double[][] matrix2) {
        if (!(checkMatrix(matrix1) && checkMatrix(matrix2))) {
            return null;
        }

        double[][] matrix3 = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                matrix3[i][j] = matrix1[i][j] - matrix2[i][j];
            }
        }

        return matrix3;
    }

    public static double[][] trans(double[][] matrix) {
        if (!checkMatrix(matrix)) {
            return null;
        }

        double[][] matrix2 = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                matrix2[i][j] = matrix[j][i];
            }
        }

        return matrix2;
    }

    public static double mult(double[] matrix1, double[] matrix2) {
        if (!(checkMatrix(matrix1) && checkMatrix(matrix2))) {
            return 0.0;
        }

        double dot = 0.0;
        for (int i = 0; i < 3; i++) {
            dot += matrix1[i] * matrix2[i];
        }

        return dot;
    }

    public static double[] mult(double[][] matrix1, double[] matrix2) {
        if (!(checkMatrix(matrix1) && checkMatrix(matrix2))) {
            return null;
        }

        double[] matrix3 = new double[3];
        for (int i = 0; i < 3; i++) {
            matrix3[i] = 0.0;
            for (int j = 0; j < 3; j++) {
                matrix3[i] += matrix1[i][j] * matrix2[j];
            }
        }

        return matrix3;
    }

    public static double[] mult(double[] matrix1, double[][] matrix2) {
        if (!(checkMatrix(matrix1) && checkMatrix(matrix2))) {
            return null;
        }

        double[] matrix3 = new double[3];
        for (int i = 0; i < 3; i++) {
            matrix3[i] = 0.0;
            for (int j = 0; j < 3; j++) {
                matrix3[i] += matrix1[j] * matrix2[j][i];
            }
        }

        return matrix3;
    }

    public static double[][] mult(double[][] matrix1, double[][] matrix2) {
        if (!(checkMatrix(matrix1) && checkMatrix(matrix2))) {
            return null;
        }

        double[][] matrix3 = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                matrix3[i][j] = 0.0;
                for (int k = 0; k < 3; k++) {
                    matrix3[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }

        return matrix3;
    }

    public static double[] mult(double alpha, double[] matrix) {
        if (!checkMatrix(matrix)) {
            return null;
        }

        double[] matrix2 = new double[3];
        for (int i = 0; i < 3; i++) {
            matrix2[i] = alpha * matrix[i];
        }

        return matrix2;
    }

    public static double[] mult(double[] matrix, double alpha) {
        return mult(alpha, matrix);
    }

    public static double[][] mult(double alpha, double[][] matrix) {
        if (!checkMatrix(matrix)) {
            return null;
        }

        double[][] matrix2 = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                matrix2[i][j] = alpha * matrix[i][j];
            }
        }

        return matrix2;
    }

    public static double[][] mult(double[][] matrix, double alpha) {
        return mult(alpha, matrix);
    }

    public static double determinant(double[][] matrix) {
        if (!checkMatrix(matrix)) {
            return 0.0;
        }

        double value = 0.0;
        value += matrix[0][0] * matrix[1][1] * matrix[2][2];
        value += matrix[0][1] * matrix[1][2] * matrix[2][0];
        value += matrix[0][2] * matrix[1][0] * matrix[2][1];
        value -= matrix[0][2] * matrix[1][1] * matrix[2][0];
        value -= matrix[0][0] * matrix[1][2] * matrix[2][1];
        value -= matrix[0][1] * matrix[1][0] * matrix[2][2];

        return value;
    }

    public static double norm2(double[] matrix) {
        if (!checkMatrix(matrix)) {
            return 0.0;
        }

        double value = 0.0;
        value += matrix[0] * matrix[0];
        value += matrix[1] * matrix[1];
        value += matrix[2] * matrix[2];

        return value;
    }

    public static double norm(double[] matrix) {
        return Math.sqrt(norm2(matrix));
    }

    public static double[][] inverse(double[][] matrix) {
        if (!checkMatrix(matrix)) {
            return null;
        }

        double det = determinant(matrix);
        if (Math.abs(det) < MIN_DET) {
            return null;
        }

        double[][] matrix2 = new double[3][3];
        matrix2[0][0] = (matrix[1][1] * matrix[2][2] - matrix[1][2] * matrix[2][1]) / det;
        matrix2[0][1] = (matrix[0][2] * matrix[2][1] - matrix[0][1] * matrix[2][2]) / det;
        matrix2[0][2] = (matrix[0][1] * matrix[1][2] - matrix[0][2] * matrix[1][1]) / det;
        matrix2[1][0] = (matrix[1][2] * matrix[2][0] - matrix[1][0] * matrix[2][2]) / det;
        matrix2[1][1] = (matrix[0][0] * matrix[2][2] - matrix[0][2] * matrix[2][0]) / det;
        matrix2[1][2] = (matrix[0][2] * matrix[1][0] - matrix[0][0] * matrix[1][2]) / det;
        matrix2[2][0] = (matrix[1][0] * matrix[2][1] - matrix[1][1] * matrix[2][0]) / det;
        matrix2[2][1] = (matrix[0][1] * matrix[2][0] - matrix[0][0] * matrix[2][1]) / det;
        matrix2[2][2] = (matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]) / det;

        return matrix2;
    }

    public static double[][] copy(double[][] matrix) {
        if (!checkMatrix(matrix)) {
            return null;
        }

        double[][] matrix2 = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                matrix2[i][j] = matrix[i][j];
            }
        }

        return matrix2;
    }

    public static double[] copy(double[] matrix) {
        if (!checkMatrix(matrix)) {
            return null;
        }

        double[] matrix2 = new double[3];
        for (int i = 0; i < 3; i++) {
            matrix2[i] = matrix[i];
        }

        return matrix2;
    }
}
