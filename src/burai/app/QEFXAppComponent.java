/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public abstract class QEFXAppComponent<T extends QEFXAppController> {

    protected Node node;

    protected T controller;

    public QEFXAppComponent(String fileFXML, T controller) throws IOException {
        if (fileFXML == null || fileFXML.trim().isEmpty()) {
            throw new IllegalArgumentException("file name of FXML is empty.");
        }

        if (controller == null) {
            throw new IllegalArgumentException("controller is null.");
        }

        this.controller = controller;

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource(fileFXML));
        fxmlLoader.setController(this.controller);
        this.node = fxmlLoader.load();

        AnchorPane.setBottomAnchor(this.node, 0.0);
        AnchorPane.setTopAnchor(this.node, 0.0);
        AnchorPane.setLeftAnchor(this.node, 0.0);
        AnchorPane.setRightAnchor(this.node, 0.0);
    }

    public final Node getNode() {
        return this.node;
    }

    public final T getController() {
        return this.controller;
    }
}
