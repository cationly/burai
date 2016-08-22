/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation.editor;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import burai.app.QEFXMain;
import burai.atoms.viewer.AtomsViewer;
import burai.atoms.viewer.operation.ViewerEventManager;
import burai.atoms.viewer.operation.mouse.MouseEventHandler;

public class PutMenuItem extends EditorMenuItem {

    private static final String ITEM_LABEL = "Put an atom";

    public PutMenuItem(ViewerEventManager manager) {
        super(ITEM_LABEL, manager);
    }

    public PutMenuItem(EditorMenu editorMenu) {
        super(ITEM_LABEL, editorMenu);
    }

    @Override
    protected void editAtoms() {
        if (this.manager == null) {
            return;
        }

        AtomsViewer atomsViewer = this.manager.getAtomsViewer();
        if (atomsViewer == null) {
            return;
        }

        MouseEventHandler hander = this.manager.getMouseEventHandler();
        if (hander == null) {
            return;
        }

        double sceneX = hander.getMouseX0();
        double sceneY = hander.getMouseY0();
        double sceneZ = 0.0;

        if (!atomsViewer.isInCell(sceneX, sceneY, sceneZ)) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            QEFXMain.initializeDialogOwner(alert);
            String message = "";
            message = message + "Specified coordinate is out of the cell." + System.lineSeparator();
            message = message + "Putted atom will be packed into the cell.";
            alert.setHeaderText(message);
            Optional<ButtonType> optButtonType = alert.showAndWait();
            if (optButtonType == null || !optButtonType.isPresent()) {
                return;
            }
            if (optButtonType.get() != ButtonType.OK) {
                return;
            }
        }

        PeriodicTable periodicTable = new PeriodicTable();
        Optional<ElementButton> optElementButton = periodicTable.showAndWait();
        if (optElementButton == null || !optElementButton.isPresent()) {
            return;
        }

        atomsViewer.storeCell();

        ElementButton elementButton = optElementButton.get();
        String elementName = elementButton.getText();
        atomsViewer.putAtom(elementName, sceneX, sceneY, sceneZ);
    }
}
