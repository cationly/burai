/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input.namelist;

public class QELogical extends QEValueBase {

    private boolean logValue;

    public QELogical(String name, boolean l) {
        super(name);
        this.logValue = l;
    }

    @Override
    public int getIntegerValue() {
        return this.logValue ? 1 : 0;
    }

    @Override
    public double getRealValue() {
        return this.logValue ? 1.0 : 0.0;
    }

    @Override
    public boolean getLogicalValue() {
        return this.logValue;
    }

    @Override
    public String getCharacterValue() {
        return "." + String.valueOf(this.logValue).toUpperCase() + ".";
    }
}
