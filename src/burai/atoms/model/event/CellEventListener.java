/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.model.event;

public interface CellEventListener extends ModelEventListener {

    public abstract void onLatticeMoved(CellEvent event);

    public abstract void onAtomAdded(CellEvent event);

    public abstract void onAtomRemoved(CellEvent event);

    public abstract void onBondAdded(CellEvent event);

    public abstract void onBondRemoved(CellEvent event);

}
