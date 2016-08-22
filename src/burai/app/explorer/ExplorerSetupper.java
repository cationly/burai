/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.explorer;

public abstract class ExplorerSetupper {

    protected QEFXExplorerController controller;

    protected ExplorerSetupper(QEFXExplorerController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("controller is null.");
        }

        this.controller = controller;
    }
}
