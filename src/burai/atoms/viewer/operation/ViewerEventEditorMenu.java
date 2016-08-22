/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation;

import javafx.event.Event;

public abstract class ViewerEventEditorMenu<T extends Event> implements ViewerEventKernel<T> {

    public ViewerEventEditorMenu() {
        // NOP
    }

    @Override
    public final boolean isToPerform(ViewerEventManager manager) {
        if(manager == null) {
            return false;
        }

        return manager.isEditorMenuBusy();
    }
}
