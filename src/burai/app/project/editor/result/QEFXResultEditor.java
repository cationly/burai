/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.result;

import java.io.IOException;

import burai.app.QEFXAppComponent;

public abstract class QEFXResultEditor<E extends QEFXResultEditorController<?>> extends QEFXAppComponent<E> {

    public QEFXResultEditor(String fileFXML, E controller) throws IOException {
        super(fileFXML, controller);

        if (this.node != null) {
            this.node.setOnMouseReleased(event -> this.node.requestFocus());
        }
    }

}
