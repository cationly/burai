/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input.namelist;

public class QEInteger extends QEValueBase {

    private int intValue;

    public QEInteger(String name, int i) {
        super(name);
        this.intValue = i;
    }

    @Override
    public int getIntegerValue() {
        return this.intValue;
    }

    @Override
    public double getRealValue() {
        return (double) this.intValue;
    }

    @Override
    public boolean getLogicalValue() {
        return this.intValue != 0;
    }

    @Override
    public String getCharacterValue() {
        return String.valueOf(this.intValue);
    }
}
