/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor;

import java.io.IOException;

import burai.app.QEFXAppComponent;
import burai.app.QEFXAppController;

public abstract class QEFXEditorComponent<T extends QEFXAppController> extends QEFXAppComponent<T> {

    public QEFXEditorComponent(String fileFXML, T controller) throws IOException {
        super(fileFXML, controller);
    }

    public abstract void notifyEditorOpened();

}
