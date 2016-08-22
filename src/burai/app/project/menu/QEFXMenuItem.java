/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.menu;

import javafx.scene.Group;

public class QEFXMenuItem<K> extends Group {

    protected K key;

    private QEFXMenu<K> parent;

    protected boolean selected;

    private MenuItemSelected<K> onMenuItemSelected;

    public QEFXMenuItem(K key) {
        this.key = key;
        this.parent = null;
        this.selected = false;
        this.onMenuItemSelected = null;

        this.setupHovering();
    }

    public void setParent(QEFXMenu<K> parent) {
        this.parent = parent;
    }

    protected void setOnMenuItemSelected(MenuItemSelected<K> onMenuItemSelected) {
        this.onMenuItemSelected = onMenuItemSelected;
    }

    private void setupHovering() {
        this.hoverProperty().addListener(o -> {
            this.selected = this.isHover();

            if (this.onMenuItemSelected != null) {
                this.onMenuItemSelected.onMenuItemSelected(this.key);
            }

            if (this.parent != null && this.selected) {
                this.parent.setSelectedKey(this.key);
            }
        });
    }

    @Override
    public String toString() {
        return this.key == null ? super.toString() : this.key.toString();
    }

    @Override
    public int hashCode() {
        return this.key == null ? super.hashCode() : this.key.hashCode();
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

        @SuppressWarnings("unchecked")
        QEFXMenuItem<K> other = (QEFXMenuItem<K>) obj;
        if (this.key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!this.key.equals(other.key)) {
            return false;
        }

        return true;
    }
}
