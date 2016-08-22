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
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

public abstract class QEFXMenu<K> extends Group {

    private K selectedKey;

    private MenuShowing onMenuShowing;

    private MenuItemSelected<K> onMenuItemSelected;

    protected QEFXMenu() {
        this.selectedKey = null;
        this.onMenuItemSelected = null;
        this.setupMouseClickedAction();
    }

    public abstract void clearItem();

    public abstract void addItem(K key);

    protected abstract void playShowingAnimation();

    protected void setSelectedKey(K key) {
        this.selectedKey = key;
    }

    public void setOnMenuShowing(MenuShowing onMenuShowing) {
        this.onMenuShowing = onMenuShowing;
    }

    public void setOnMenuItemSelected(MenuItemSelected<K> onMenuItemSelected) {
        this.onMenuItemSelected = onMenuItemSelected;
    }

    public void showMenu() {
        this.playShowingAnimation();

        if (this.onMenuShowing != null) {
            this.onMenuShowing.onMenuShowing();
        }
    }

    private void setupMouseClickedAction() {
        this.setOnMouseClicked(event -> {
            Parent parent = this.getParent();
            if (parent != null && parent instanceof Pane) {
                ((Pane) parent).getChildren().remove(this);
            }

            if (this.onMenuItemSelected != null) {
                this.onMenuItemSelected.onMenuItemSelected(this.selectedKey);
            }
        });
    }
}
