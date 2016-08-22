/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.reader.cif;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CIFSymmetricFormula {

    private static final Pattern FORMULA_PATTERN = Pattern.compile("[+-]([0-9]+[/][0-9]+|[0-9]+)?([xyz]([/][0-9]+)?)?");

    private double xCoeff;
    private double yCoeff;
    private double zCoeff;
    private double constant;

    public CIFSymmetricFormula(String formula) throws IncorrectCIFSymmetricException {
        if (formula == null) {
            throw new IllegalArgumentException("formula is null.");
        }

        String formula2 = formula.trim();
        if (formula2.isEmpty()) {
            throw new IllegalArgumentException("formula is empty.");
        }

        if (formula2.charAt(0) != '-' && formula2.charAt(0) != '+') {
            formula2 = '+' + formula2;
        }

        this.xCoeff = 0.0;
        this.yCoeff = 0.0;
        this.zCoeff = 0.0;
        this.constant = 0.0;
        this.resolveParameters(formula2);
    }

    private void resolveParameters(String formula) throws IncorrectCIFSymmetricException {
        int startPosition = 0;
        int endPosition = 0;
        Matcher matcher = FORMULA_PATTERN.matcher(formula);

        while (matcher.find()) {
            startPosition = matcher.start();
            if (startPosition != endPosition) {
                throw new IncorrectCIFSymmetricException();
            }
            endPosition = matcher.end();

            String term = matcher.group();
            String[] subTerms = term.split("[xyz]");
            if (subTerms == null || subTerms.length < 1 || subTerms.length > 2) {
                throw new IncorrectCIFSymmetricException();
            }

            double coeff = 0.0;
            if ("+".equals(subTerms[0])) {
                coeff = 1.0;
            } else if ("-".equals(subTerms[0])) {
                coeff = -1.0;
            } else {
                coeff = this.calcFraction(subTerms[0]);
            }

            if (subTerms.length > 1) {
                if (subTerms[1].isEmpty() || subTerms[1].charAt(0) != '/') {
                    throw new IncorrectCIFSymmetricException();
                }
                coeff *= this.calcFraction("1" + subTerms[1]);
            }

            if (term.indexOf('x') > -1) {
                this.xCoeff += coeff;
            } else if (term.indexOf('y') > -1) {
                this.yCoeff += coeff;
            } else if (term.indexOf('z') > -1) {
                this.zCoeff += coeff;
            } else {
                this.constant += coeff;
            }
        }
    }

    private double calcFraction(String str) throws NumberFormatException {
        String[] subStr = str.split("/");
        if (subStr == null || subStr.length != 2) {
            throw new NumberFormatException("not fraction.");
        }

        int intNume = Integer.parseInt(subStr[0]);
        int intDeno = Integer.parseInt(subStr[1]);
        if (intDeno == 0) {
            throw new NumberFormatException("denominator is zero.");
        }

        return ((double) intNume) / ((double) intDeno);
    }

    public double operate(double[] coord) {
        if (coord == null || coord.length < 3) {
            throw new IllegalArgumentException("coord is incorrect.");
        }

        return xCoeff * coord[0] + yCoeff * coord[1] + zCoeff * coord[2] + constant;
    }
}
