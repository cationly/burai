/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.model.event;

public interface ModelEventListener {

    public abstract boolean isToBeFlushed();

    public abstract void onModelDisplayed(ModelEvent event);

    public abstract void onModelNotDisplayed(ModelEvent event);

}
