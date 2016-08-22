/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.explorer.body.menuitem;

import burai.app.fileview.QEFXFileViewDialog;

public class QEFXOpenFileMenuItem extends QEFXMenuItem {

    public QEFXOpenFileMenuItem(String filePath) {
        super("Open file");

        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("filePath is empty.");
        }

        this.setOnAction(event -> {
            QEFXFileViewDialog fileDialog = new QEFXFileViewDialog(filePath);
            fileDialog.show();
        });
    }
}
