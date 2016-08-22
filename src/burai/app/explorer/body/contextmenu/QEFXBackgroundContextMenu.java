/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.explorer.body.contextmenu;

import burai.app.explorer.body.QEFXExplorerBody;
import burai.app.explorer.body.menuitem.QEFXMakeDirectoryMenuItem;
import burai.app.explorer.body.menuitem.QEFXPasteFileMenuItem;
import burai.app.icon.QEFXIcon;

public class QEFXBackgroundContextMenu extends QEFXContextMenu<QEFXIcon> {

    public QEFXBackgroundContextMenu(QEFXExplorerBody body) {
        super(null, body);
    }

    @Override
    protected void createMenuItems() {
        this.getItems().clear();

        this.getItems().add(new QEFXPasteFileMenuItem(null, this.body));

        this.getItems().add(new QEFXMakeDirectoryMenuItem(null, this.body));
    }
}
