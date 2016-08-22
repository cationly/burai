/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result;

import java.io.IOException;

import burai.app.QEFXAppComponent;

public abstract class QEFXResultViewer<V extends QEFXResultViewerController> extends QEFXAppComponent<V> {

    public QEFXResultViewer(String fileFXML, V controller) throws IOException {
        super(fileFXML, controller);
    }

    public void reload() {
        if (this.controller != null) {
            this.controller.reload();
        }
    }

}
