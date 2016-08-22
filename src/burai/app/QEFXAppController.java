/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app;

import javafx.fxml.Initializable;
import javafx.stage.Stage;

public abstract class QEFXAppController implements Initializable {

    protected QEFXMainController mainController;

    public QEFXAppController() {
        this.mainController = null;
    }

    public QEFXAppController(QEFXMainController mainController) {
        if (mainController == null) {
            throw new IllegalArgumentException("mainController is null.");
        }

        this.mainController = mainController;
    }

    public QEFXMainController getMainController() {
        return this.mainController;
    }

    public Stage getStage() {
        if (this.mainController != null) {
            return this.mainController.getStage();
        }

        return null;
    }

    public void quitSystem() {
        if (this.mainController != null) {
            this.mainController.quitSystem();
        }
    }

    public void setMaximized(boolean maximized) {
        if (this.mainController != null) {
            this.mainController.setMaximized(maximized);
        }
    }

    public void setFullScreen(boolean fullScreen) {
        if (this.mainController != null) {
            this.mainController.setFullScreen(fullScreen);
        }
    }

    public void setResizable(boolean resizable) {
        if (this.mainController != null) {
            this.mainController.setResizable(resizable);
        }
    }
}
