/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import burai.app.QEFXAppController;
import burai.app.QEFXMainController;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

public abstract class QEFXEditorController extends QEFXAppController {

    @FXML
    protected Accordion accordion;

    public QEFXEditorController(QEFXMainController mainController) {
        super(mainController);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupConfigAccordion();
    }

    private void setupConfigAccordion() {
        if (this.accordion == null) {
            return;
        }

        List<TitledPane> panes = this.accordion.getPanes();
        if (panes != null && !panes.isEmpty()) {
            TitledPane pane = panes.get(0);
            if (pane != null) {
                this.accordion.setExpandedPane(pane);
            }
        }
    }
}
