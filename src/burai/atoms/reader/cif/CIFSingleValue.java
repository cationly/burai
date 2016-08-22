/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.reader.cif;

import burai.com.str.SmartSplitter;

public class CIFSingleValue {

    private String name;

    private String value;

    public CIFSingleValue() {
        this.name = null;
        this.value = null;
    }

    public boolean isName(String name) {
        if (this.name == null || this.name.isEmpty()) {
            return false;
        }

        return this.name.equalsIgnoreCase(name);
    }

    public boolean hasValue() {
        if (this.name == null || this.name.isEmpty()) {
            return false;
        }

        if (this.value == null || this.value.isEmpty()) {
            return false;
        }

        return true;
    }

    public String getValue() {
        return this.value;
    }

    public boolean read(String line) {
        if (line == null || line.isEmpty()) {
            return false;
        }

        //String[] subLines = line.trim().split("\\s+");
        String[] subLines = SmartSplitter.split(line.trim());
        if (subLines == null || subLines.length < 2) {
            return false;
        }

        if (!subLines[0].startsWith("_")) {
            return false;
        }

        this.name = subLines[0];

        this.value = subLines[1];

        return true;
    }
}
