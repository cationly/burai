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
import javafx.scene.control.MenuItem;

public abstract class EditorMenuItem extends MenuItem {

    protected EditorMenu editorMenu;

    protected ViewerEventManager manager;

    protected EditorMenuItem(String text, ViewerEventManager manager) {
        this(text, null, manager);
    }

    protected EditorMenuItem(String text, EditorMenu editorMenu) {
        this(text, editorMenu, editorMenu == null ? null : editorMenu.getManager());
    }

    protected EditorMenuItem(String text, EditorMenu editorMenu, ViewerEventManager manager) {
        super(text);

        if (manager == null) {
            throw new IllegalArgumentException("manager is null.");
        }

        this.editorMenu = editorMenu;
        this.manager = manager;

        this.setOnAction(event -> {
            if (this.editorMenu != null) {
                this.editorMenu.setItemInAction(true);
            }

            this.editAtoms();

            this.manager.setPrincipleAtom(null);
            this.manager.removeEditorMenu();

            if (this.editorMenu != null) {
                this.editorMenu.setItemInAction(false);
            }
        });
    }

    protected abstract void editAtoms();

    public void performAction() {
        this.editAtoms();
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        EditorMenuItem item = (EditorMenuItem) obj;
        return this.manager == item.manager;
    }
}
