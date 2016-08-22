/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation.editor;

import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.GridPane;
import burai.app.QEFXMain;
import burai.atoms.element.ElementUtil;

public class PeriodicTable extends Dialog<ElementButton> {

    private GridPane gridPane;

    public PeriodicTable() {
        super();

        DialogPane dialogPane = this.getDialogPane();
        QEFXMain.initializeStyleSheets(dialogPane.getStylesheets());
        QEFXMain.initializeDialogOwner(this);

        this.setResizable(false);
        this.setTitle("Periodic Table");
        dialogPane.setHeaderText("Select an element.");
        dialogPane.getButtonTypes().addAll(ButtonType.CANCEL);

        this.createGridPane();
        dialogPane.setContent(this.gridPane);

        this.setResultConverter(buttonType -> {
            return null;
        });
    }

    private void createGridPane() {
        this.gridPane = new GridPane();
        this.gridPane.setHgap(0.0);
        this.gridPane.setVgap(0.0);
        this.gridPane.setAlignment(Pos.CENTER);

        String[] elementNames = ElementUtil.listAllElements();
        for (String elementName : elementNames) {
            ElementButton elementButton = new ElementButton(elementName);
            elementButton.setDialog(this);
            this.gridPane.add(elementButton, elementButton.getY() - 1, elementButton.getX() - 1);
        }
    }
}
