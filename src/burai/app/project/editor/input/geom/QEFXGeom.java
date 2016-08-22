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

public class QEFXGeom extends QEFXEditorComponent<QEFXGeomController> {

    private QEFXCell cellComponent;
    private QEFXElements elementsComponent;
    private QEFXAtoms atomsComponent;

    public QEFXGeom(QEFXMainController mainController, QEInput input, Cell cell) throws IOException {
        super("QEFXGeom.fxml", new QEFXGeomController(mainController));

        if (input == null) {
            throw new IllegalArgumentException("input is null.");
        }

        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        this.createComponents(input, cell);
    }

    private void createComponents(QEInput input, Cell cell) throws IOException {
        QEFXMainController mainController = null;
        if (this.controller != null) {
            mainController = this.controller.getMainController();
        }

        this.cellComponent = new QEFXCell(mainController, input, cell);
        this.elementsComponent = new QEFXElements(mainController, input, cell);
        this.atomsComponent = new QEFXAtoms(mainController, input, cell);

        if (this.controller != null) {
            this.controller.setCellPane(this.cellComponent.getNode());
            this.controller.setElementsPane(this.elementsComponent.getNode());
            this.controller.setAtomsPane(this.atomsComponent.getNode());
        }
    }

    @Override
    public void notifyEditorOpened() {
        this.cellComponent.notifyEditorOpened();
        this.elementsComponent.notifyEditorOpened();
        this.atomsComponent.notifyEditorOpened();
    }

}
