/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.reader.cif;

import java.util.ArrayList;
import java.util.List;

public class CIFLoopElement {

    private CIFLoopDefinition definition;

    private List<String> values;

    public CIFLoopElement(CIFLoopDefinition definition) {
        if (definition == null || definition.isEmpty()) {
            throw new IllegalArgumentException("loop definition of CIF is empty.");
        }

        this.definition = definition;

        this.values = new ArrayList<String>();
    }

    public boolean addValue(String value) {
        if (this.values.size() >= this.definition.numNames()) {
            return false;
        }

        return this.values.add(value);
    }

    public boolean addValue(String[] valueList) {
        if (valueList == null || valueList.length < 1) {
            return false;
        }

        boolean stat = true;
        for (String value : valueList) {
            stat = stat && this.addValue(value);
        }

        return stat;
    }

    public String getValue(String name) {
        int index = this.definition.indexOf(name);
        if (index < 0) {
            return null;
        }

        return values.get(index);
    }

    public boolean isEmpty() {
        return this.values.isEmpty();
    }
}
