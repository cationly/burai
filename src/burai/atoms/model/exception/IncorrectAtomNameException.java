/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.model.exception;

public class IncorrectAtomNameException extends Exception {

    public IncorrectAtomNameException() {
        super("atomic name is incorrect.");
    }

    public IncorrectAtomNameException(String name) {
        super("atomic name is incorrect: " + name + ".");
    }

    public IncorrectAtomNameException(Exception e) {
        super(e);
    }

}
