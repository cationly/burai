/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation.editor;

import burai.atoms.viewer.operation.ViewerEventManager;


public class DetailMenuItem extends EditorMenuItem {

    private static final String ITEM_LABEL = "Show detail";

    public DetailMenuItem(ViewerEventManager manager) {
        super(ITEM_LABEL, manager);
    }

    public DetailMenuItem(EditorMenu editorMenu) {
        super(ITEM_LABEL, editorMenu);
    }

    @Override
    protected void editAtoms() {
        // TODO
        System.out.println("detail-item is not implemented.");
    }
}
