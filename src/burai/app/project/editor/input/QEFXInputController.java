/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input;

import burai.app.QEFXAppController;
import burai.app.QEFXMainController;
import burai.input.QEInput;

public abstract class QEFXInputController extends QEFXAppController {

    protected QEInput input;

    public QEFXInputController(QEFXMainController mainController, QEInput input) {
        super(mainController);

        if (input == null) {
            throw new IllegalArgumentException("input is null.");
        }

        this.input = input;
    }
}
