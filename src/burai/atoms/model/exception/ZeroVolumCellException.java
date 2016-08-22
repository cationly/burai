/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.model.exception;

public class ZeroVolumCellException extends Exception {

    public ZeroVolumCellException() {
        super("volume of cell is zero.");
    }

    public ZeroVolumCellException(String message) {
        super(message);
    }

    public ZeroVolumCellException(Exception e) {
        super(e);
    }

}
