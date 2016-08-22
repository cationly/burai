/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.com.lebedev;

public class Lebedev {

    private static Lebedev instance = null;

    public Lebedev getInstance() {
        if (instance == null) {
            instance = new Lebedev();
        }

        return instance;
    }

    private Lebedev() {
        // NOP
    }

}
