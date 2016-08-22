/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.geom;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import burai.app.QEFXMainController;
import burai.app.project.editor.QEFXEditorController;

public class QEFXGeomController extends QEFXEditorController {

    @FXML
    private ScrollPane cellPane;

    @FXML
    private ScrollPane elementsPane;

    @FXML
    private ScrollPane atomsPane;

    public QEFXGeomController(QEFXMainController mainController) {
        super(mainController);
    }

    public void setCellPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.cellPane != null) {
            this.cellPane.setContent(node);
        }
    }

    public void setElementsPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.elementsPane != null) {
            this.elementsPane.setContent(node);
        }
    }

    public void setAtomsPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.atomsPane != null) {
            this.atomsPane.setContent(node);
        }
    }
}
