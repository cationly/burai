/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.preloader;

import java.io.IOException;

import burai.app.QEFXAppComponent;
import burai.app.QEFXAppController;
import burai.app.QEFXMain;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class QEFXSplash extends QEFXAppComponent<QEFXAppController> {

    private Stage splashStage;

    public QEFXSplash() throws IOException {
        super("QEFXSplash.fxml", new QEFXSplashController());
        this.splashStage = null;
    }

    public void showSplash() {
        if (this.splashStage == null) {
            this.splashStage = new Stage(StageStyle.TRANSPARENT);
            if (this.node != null && this.node instanceof Parent) {
                QEFXMain.initializeStyleSheets(((Parent) this.node).getStylesheets());
                this.splashStage.setScene(new Scene((Parent) this.node));
            }
        }

        this.splashStage.show();
    }

    public void hideSplash() {
        if (this.splashStage != null) {
            this.splashStage.close();
        }
    }
}
