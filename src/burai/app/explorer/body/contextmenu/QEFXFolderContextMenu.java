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
import burai.app.explorer.body.menuitem.QEFXCopyFileMenuItem;
import burai.app.explorer.body.menuitem.QEFXDeleteFileMenuItem;
import burai.app.explorer.body.menuitem.QEFXMakeDirectoryMenuItem;
import burai.app.explorer.body.menuitem.QEFXPasteFileMenuItem;
import burai.app.explorer.body.menuitem.QEFXRenameFileMenuItem;
import burai.app.icon.QEFXFolderIcon;

public class QEFXFolderContextMenu extends QEFXContextMenu<QEFXFolderIcon> {

    public QEFXFolderContextMenu(QEFXFolderIcon icon, QEFXExplorerBody body) {
        super(icon, body);
    }

    @Override
    protected void createMenuItems() {

        this.getItems().add(new QEFXRenameFileMenuItem(this.icon, this.body));

        this.getItems().add(new QEFXCopyFileMenuItem(this.icon, this.body));

        this.getItems().add(new QEFXPasteFileMenuItem(this.icon, this.body));

        this.getItems().add(new QEFXDeleteFileMenuItem(this.icon, this.body));

        this.getItems().add(new QEFXMakeDirectoryMenuItem(this.icon, this.body));
    }
}
