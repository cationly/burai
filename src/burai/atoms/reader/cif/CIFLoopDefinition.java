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

public class CIFLoopDefinition {

    private List<String> names;

    public CIFLoopDefinition() {
        this.names = new ArrayList<String>();
    }

    public int numNames() {
        return this.names.size();
    }

    public boolean hasName(String name) {
        return this.names.contains(name);
    }

    public boolean addName(String name) {
        return this.names.add(name);
    }

    public boolean isEmpty() {
        return this.names.isEmpty();
    }

    public void clear() {
        this.names.clear();
    }

    public int indexOf(String name) {
        return this.names.indexOf(name);
    }
}
