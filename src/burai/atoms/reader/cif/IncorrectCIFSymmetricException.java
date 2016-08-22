/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.reader.cif;

public class IncorrectCIFSymmetricException extends Exception {

    public IncorrectCIFSymmetricException() {
        super("symmetric operator is incorrect.");
    }

    public IncorrectCIFSymmetricException(String operator) {
        super("symmetric operator is incorrect: " + operator + ".");
    }

    public IncorrectCIFSymmetricException(Exception e) {
        super(e);
    }

}
