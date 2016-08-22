/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input.namelist;

import java.util.ArrayList;
import java.util.List;

public abstract class QEValueBase implements QEValue {

    public static QEValue getInstance(String name) {
        return QEValueBase.getInstance(name, 0);
    }

    public static QEValue getInstance(String name, int i) {
        return new QEInteger(name, i);
    }

    public static QEValue getInstance(String name, double r) {
        return new QEReal(name, r);
    }

    public static QEValue getInstance(String name, boolean l) {
        return new QELogical(name, l);
    }

    public static QEValue getInstance(String name, String c) {
        QEValueChecker valueChecker = new QEValueChecker(c);

        if (valueChecker.isInteger()) {
            return new QEInteger(name, valueChecker.getInteger());
        }

        if (valueChecker.isReal()) {
            return new QEReal(name, valueChecker.getReal());
        }

        if (valueChecker.isLogical()) {
            return new QELogical(name, valueChecker.getLogical());
        }

        return new QECharacter(name, c);
    }

    private String name;

    private List<Integer> indexList;

    protected QEValueBase(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name is null or empty.");
        }

        this.setupNameAndIndex(name.toLowerCase());
    }

    private void setupNameAndIndex(String name) {
        int startBracket = name.indexOf('(');
        int endBracket = name.indexOf(')');

        if (startBracket > -1 && (startBracket + 1) < endBracket) {
            this.name = name.substring(0, startBracket).trim();
            this.indexList = new ArrayList<Integer>();

            boolean hasError = false;
            String strIndex = name.substring(startBracket + 1, endBracket).trim();
            String[] subIndex = strIndex.split("[\\s,]+");

            for (int i = 0; i < subIndex.length; i++) {
                try {
                    int index = Integer.parseInt(subIndex[i]);
                    this.indexList.add(index);
                } catch (Exception e) {
                    hasError = true;
                    break;
                }
            }

            if (hasError) {
                this.name = this.name + "(" + strIndex + ")";
                this.indexList = null;
            }

        } else {
            this.name = name;
            this.indexList = null;
        }
    }

    @Override
    public String getName() {
        if (this.indexList == null || this.indexList.isEmpty()) {
            return this.name;
        }

        String strIndex = "(";
        for (int i = 0; i < this.indexList.size(); i++) {
            if (i > 0) {
                strIndex = strIndex + ",";
            }
            strIndex = strIndex + this.indexList.get(i);
        }
        strIndex = strIndex + ")";

        return this.name + strIndex;
    }

    @Override
    public String toString(int length) {
        String name = this.getName();
        while (name.length() < length) {
            name = name + " ";
        }

        return name + " = " + this.getCharacterValue();
    }

    @Override
    public String toString() {
        return this.getName() + " = " + this.getCharacterValue();
    }

    @Override
    public int hashCode() {
        int hashIndex = 0;
        if (this.indexList != null) {
            int scale = 1;
            for (int index : this.indexList) {
                hashIndex += scale * index;
                scale *= 10;
            }
        }

        return (this.name.hashCode() + hashIndex);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof QEValue)) {
            return false;
        }

        QEValue other = (QEValue) obj;
        return this.getName().equalsIgnoreCase(other.getName());
    }
}
