/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.explorer;

import java.io.IOException;

import burai.app.QEFXAppComponent;
import burai.app.QEFXMainController;

public class QEFXExplorer extends QEFXAppComponent<QEFXExplorerController> {

    public QEFXExplorer(QEFXMainController mainController) throws IOException {
        super("QEFXExplorer.fxml", new QEFXExplorerController(mainController));
    }

    public QEFXExplorerFacade getFacade() {
        if(this.controller == null) {
            return null;
        }

        return new QEFXExplorerFacade(this.controller);
    }

}
