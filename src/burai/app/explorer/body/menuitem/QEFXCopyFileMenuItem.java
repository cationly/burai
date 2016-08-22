/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.explorer.body.menuitem;

import burai.app.explorer.body.QEFXExplorerBody;
import burai.app.icon.QEFXIcon;

public class QEFXCopyFileMenuItem extends QEFXMenuItem {

    public QEFXCopyFileMenuItem(QEFXIcon icon, QEFXExplorerBody body) {
        super("Copy file");

        if (icon == null) {
            throw new IllegalArgumentException("icon is null.");
        }

        if (body == null) {
            throw new IllegalArgumentException("body is null.");
        }

        if (body.isExplorerMode()) {
            this.setOnAction(event -> {
                body.copyIcon(icon);
            });

        } else if (body.isRecentlyUsedMode()) {
            this.setOnAction(event -> {
                body.copyIcon(icon);
            });

        } else if (body.isCalculatingMode()) {
            this.setOnAction(event -> {
                body.copyIcon(icon);
            });

        } else if (body.isSearchedMode()) {
            this.setOnAction(event -> {
                body.copyIcon(icon);
            });

        } else if (body.isWebMode()) {
            this.setDisable(true);
        }
    }
}
