/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input.namelist;

public interface QEValue {

    public abstract String getName();

    public abstract int getIntegerValue();

    public abstract double getRealValue();

    public abstract boolean getLogicalValue();

    public abstract String getCharacterValue();

    public abstract String toString(int length);

    public abstract String toString();

}
