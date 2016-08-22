/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.preloader;

import java.net.URL;
import java.util.ResourceBundle;

import burai.app.QEFXAppController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class QEFXSplashController extends QEFXAppController {

    @FXML
    private Label commentLabel;

    @FXML
    private ProgressBar progressBar;

    public QEFXSplashController() {
        super();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupCommentLabel();
        this.setupProgressBar();
    }

    private void setupCommentLabel() {
        // TODO
    }

    private void setupProgressBar() {
        // TODO
    }
}
