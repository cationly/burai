/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.reader.cif;

public class CIFSymmetricOperator {

    private CIFSymmetricFormula xFormula;
    private CIFSymmetricFormula yFormula;
    private CIFSymmetricFormula zFormula;

    public CIFSymmetricOperator(String operator) throws IncorrectCIFSymmetricException {
        if (operator == null || operator.isEmpty()) {
            throw new IllegalArgumentException("operator is null or empty.");
        }

        String operator2 = operator;
        operator2 = operator2.replace('(', ' ');
        operator2 = operator2.replace(')', ' ');
        operator2 = operator2.replace('[', ' ');
        operator2 = operator2.replace(']', ' ');
        operator2 = operator2.replace('{', ' ');
        operator2 = operator2.replace('}', ' ');

        String[] subOperator = operator2.trim().split("[,:;]+");
        if (subOperator == null || subOperator.length < 3) {
            throw new IncorrectCIFSymmetricException();
        }

        this.xFormula = new CIFSymmetricFormula(subOperator[0]);
        this.yFormula = new CIFSymmetricFormula(subOperator[1]);
        this.zFormula = new CIFSymmetricFormula(subOperator[2]);
    }

    public double[] operate(double[] coord) {
        if (coord == null || coord.length < 3) {
            return null;
        }

        double x = this.xFormula.operate(coord);
        double y = this.yFormula.operate(coord);
        double z = this.zFormula.operate(coord);

        return new double[] { x, y, z };
    }
}
