/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input.namelist;

public class QEReal extends QEValueBase {

    private double realValue;

    public QEReal(String name, double r) {
        super(name);
        this.realValue = r;
    }

    @Override
    public int getIntegerValue() {
        return (int) this.realValue;
    }

    @Override
    public double getRealValue() {
        return this.realValue;
    }

    @Override
    public boolean getLogicalValue() {
        int intValue = (int) this.realValue;
        return intValue != 0;
    }

    @Override
    public String getCharacterValue() {
        return String.format("%12.5e", Math.abs(this.realValue) < 1.0e-20 ? 0.0 : this.realValue);
    }
}
