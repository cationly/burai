/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.web;

import java.io.IOException;

import javafx.scene.web.WebEngine;
import burai.app.QEFXAppComponent;
import burai.app.QEFXMainController;

public class QEFXWeb extends QEFXAppComponent<QEFXWebController> {

    public QEFXWeb(QEFXMainController mainController, String url) throws IOException {
        super("QEFXWeb.fxml", new QEFXWebController(mainController, url));

        this.setupOnTabClosed();
    }

    private void setupOnTabClosed() {
        WebEngine engine = this.getEngine();
        if (engine == null) {
            return;
        }

        engine.setOnVisibilityChanged(event -> {
            if (event != null && (!event.getData())) {
                QEFXMainController mainController = null;
                if (this.controller != null) {
                    mainController = this.controller.getMainController();
                }

                if (mainController != null) {
                    mainController.hideWebPage(engine);
                }
            }
        });
    }

    public WebEngine getEngine() {
        if (this.controller == null) {
            return null;
        }

        return this.controller.getEngine();
    }
}
