/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.explorer.body.contextmenu;

import javafx.scene.control.ContextMenu;
import burai.app.explorer.body.QEFXExplorerBody;
import burai.app.icon.QEFXFolderIcon;
import burai.app.icon.QEFXIcon;
import burai.app.icon.QEFXProjectIcon;
import burai.app.icon.QEFXUPFIcon;
import burai.app.icon.QEFXWebIcon;

public abstract class QEFXContextMenu<I extends QEFXIcon> extends ContextMenu {

    public static ContextMenu getContextMenu(QEFXIcon icon, QEFXExplorerBody body) {
        if (body == null) {
            return null;
        }

        if (icon == null) {
            return new QEFXBackgroundContextMenu(body);

        } else if (icon instanceof QEFXProjectIcon) {
            return new QEFXProjectContextMenu((QEFXProjectIcon) icon, body);

        } else if (icon instanceof QEFXWebIcon) {
            return new QEFXWebContextMenu((QEFXWebIcon) icon, body);

        } else if (icon instanceof QEFXUPFIcon) {
            return new QEFXUPFContextMenu((QEFXUPFIcon) icon, body);

        } else if (icon instanceof QEFXFolderIcon) {
            return new QEFXFolderContextMenu((QEFXFolderIcon) icon, body);
        }

        return null;
    }

    protected I icon;

    protected QEFXExplorerBody body;

    protected QEFXContextMenu(I icon, QEFXExplorerBody body) {
        super();

        //if (icon == null) {
        //    throw new IllegalArgumentException("icon is null.");
        //}

        if (body == null) {
            throw new IllegalArgumentException("body is null.");
        }

        this.icon = icon;
        this.body = body;
        this.getStyleClass().add("icon-context-menu");

        this.getItems().clear();
        this.createMenuItems();

        this.setOnShowing(event -> {
            this.getItems().clear();
            this.createMenuItems();
        });
    }

    protected abstract void createMenuItems();
}
