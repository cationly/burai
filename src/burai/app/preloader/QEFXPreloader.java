/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.preloader;

import javafx.application.Preloader;
import javafx.stage.Stage;

public class QEFXPreloader extends Preloader {

    private QEFXSplash splash;

    @Override
    public void start(Stage primaryStage) {
        try {
            this.splash = new QEFXSplash();
            this.splash.showSplash();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
        if (stateChangeNotification == null) {
            return;
        }

        if (stateChangeNotification.getType() == StateChangeNotification.Type.BEFORE_START) {
            if (this.splash != null) {
                this.splash.hideSplash();
            }
        }
    }
}
