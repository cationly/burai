/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input;

import burai.app.QEFXMainController;
import burai.atoms.model.Cell;
import burai.input.QEInput;

public abstract class QEFXInputModelController extends QEFXInputController {

    protected Cell modelCell;

    public QEFXInputModelController(QEFXMainController mainController, QEInput input, Cell modelCell) {
        super(mainController, input);

        if (modelCell == null) {
            throw new IllegalArgumentException("modelCell is null.");
        }

        this.modelCell = modelCell;
    }

}
