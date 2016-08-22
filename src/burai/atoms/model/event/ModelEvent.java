/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.model.event;

public abstract class ModelEvent {

    protected Object source;

    protected ModelEvent(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return this.source;
    }
}
