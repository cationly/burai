/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.geom;

import java.io.IOException;

import burai.app.QEFXMainController;
import burai.app.project.editor.QEFXEditorComponent;
import burai.atoms.model.Cell;
import burai.input.QEInput;

public class QEFXAtoms extends QEFXEditorComponent<QEFXAtomsController> {

    public QEFXAtoms(QEFXMainController mainController, QEInput input, Cell cell) throws IOException {
        super("QEFXAtoms.fxml", new QEFXAtomsController(mainController, input, cell));
    }

    @Override
    public void notifyEditorOpened() {
        // TODO 自動生成されたメソッド・スタブ
    }

}
