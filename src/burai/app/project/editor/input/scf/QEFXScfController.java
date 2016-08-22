/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.scf;

import burai.app.QEFXMainController;
import burai.app.project.editor.QEFXEditorController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

public class QEFXScfController extends QEFXEditorController {

    @FXML
    public ScrollPane standardPane;

    @FXML
    public ScrollPane electronPane;

    @FXML
    public ScrollPane magnetizPane;

    @FXML
    public ScrollPane hybridPane;

    @FXML
    public ScrollPane hubbardPane;

    @FXML
    public ScrollPane vdwPane;

    @FXML
    public ScrollPane isolatedPane;

    public QEFXScfController(QEFXMainController mainController) {
        super(mainController);
    }

    public void setStandardPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.standardPane != null) {
            this.standardPane.setContent(node);
        }
    }

    public void setElectronPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.electronPane != null) {
            this.electronPane.setContent(node);
        }
    }

    public void setMagnetizPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.magnetizPane != null) {
            this.magnetizPane.setContent(node);
        }
    }

    public void setHybridPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.hybridPane != null) {
            this.hybridPane.setContent(node);
        }
    }

    public void setHubbardPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.hubbardPane != null) {
            this.hubbardPane.setContent(node);
        }
    }

    public void setVdwPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.vdwPane != null) {
            this.vdwPane.setContent(node);
        }
    }

    public void setIsolatedPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.isolatedPane != null) {
            this.isolatedPane.setContent(node);
        }
    }
}
