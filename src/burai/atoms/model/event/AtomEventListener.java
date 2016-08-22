/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.model.event;

public interface AtomEventListener extends ModelEventListener {

    public abstract void onAtomRenamed(AtomEvent event);

    public abstract void onAtomMoved(AtomEvent event);

}
