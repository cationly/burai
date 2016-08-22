/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input.namelist;

public class QECharacter extends QEValueBase {

    private String charValue;

    public QECharacter(String name, String c) {
        super(name);
        if (c == null) {
            this.charValue = "???";
        } else {
            this.charValue = c;
        }
    }

    @Override
    public int getIntegerValue() {
        int intValue = 0;

        QEValueChecker valueChecker = new QEValueChecker(this.charValue);
        if (valueChecker.isInteger()) {
            intValue = valueChecker.getInteger();
        }

        return intValue;
    }

    @Override
    public double getRealValue() {
        double realValue = 0.0;

        QEValueChecker valueChecker = new QEValueChecker(this.charValue);
        if (valueChecker.isReal()) {
            realValue = valueChecker.getReal();
        }

        return realValue;
    }

    @Override
    public boolean getLogicalValue() {
        boolean logValue = false;

        QEValueChecker valueChecker = new QEValueChecker(this.charValue);
        if (valueChecker.isLogical()) {
            logValue = valueChecker.getLogical();
        }

        return logValue;
    }

    @Override
    public String getCharacterValue() {
        return this.charValue;
    }

    @Override
    public String toString(int length) {
        String name = this.getName();
        while (name.length() < length) {
            name = name + " ";
        }

        return name + " = \"" + this.getCharacterValue() + "\"";
    }

    @Override
    public String toString() {
        return this.getName() + " = \"" + this.getCharacterValue() + "\"";
    }
}
