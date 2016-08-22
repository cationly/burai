/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.about;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import burai.app.QEFXMain;
import burai.com.env.Environments;
import burai.ver.Version;

public class QEFXAboutDialog extends Dialog<ButtonType> implements Initializable {

    @FXML
    private ImageView imageView;

    @FXML
    private TextArea textArea;

    public QEFXAboutDialog() {
        super();

        DialogPane dialogPane = this.getDialogPane();
        QEFXMain.initializeStyleSheets(dialogPane.getStylesheets());
        QEFXMain.initializeDialogOwner(this);

        this.setResizable(false);
        this.setTitle("About BURAI");
        dialogPane.getButtonTypes().clear();
        dialogPane.getButtonTypes().add(ButtonType.CLOSE);

        Node node = null;
        try {
            node = this.createContent();
        } catch (Exception e) {
            node = new Label("ERROR: cannot show QEFXAboutDialog.");
            e.printStackTrace();
        }

        dialogPane.setContent(node);

        this.setResultConverter(buttonType -> {
            return null;
        });
    }

    private Node createContent() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("QEFXAboutDialog.fxml"));
        fxmlLoader.setController(this);
        return fxmlLoader.load();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupImageView();
        this.setupTextArea();
    }

    private void setupImageView() {
        if (this.imageView == null) {
            return;
        }

        URL url = QEFXMain.class.getResource("resource/image/icon_128.png");
        String imageName = url == null ? null : url.toExternalForm();

        if (imageName != null && (!imageName.isEmpty())) {
            Image image = null;
            try {
                image = new Image(imageName);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (image != null) {
                this.imageView.setFitWidth(image.getWidth());
                this.imageView.setFitHeight(image.getHeight());
                this.imageView.setImage(image);
            }
        }
    }

    private void setupTextArea() {
        if (this.textArea == null) {
            return;
        }

        String ls = System.lineSeparator();
        String burai = "BURAI" + Version.VERSION;
        String qeWeb = Environments.getEspressoWebsite();

        String message = "";
        message = message + burai + " is a GUI system of Quantum ESPRESSO <" + qeWeb + ">." + ls;
        message = message + ls;
        message = message + "This system is developed as a JavaFX application, and requires JRE1.8" + ls;
        message = message + "or later version of runtime environment." + ls;
        message = message + ls;
        message = message + "The following external libraries are used:" + ls;
        message = message + "  - exp4j (Apache License 2.0, http://www.objecthunter.net/exp4j/)" + ls;
        message = message + "  - Gson  (Apache License 2.0, https://github.com/google/gson)" + ls;
        message = message + ls;
        message = message + "All of SVG icons are from FLATICON <http://www.flaticon.com>." + ls;
        message = message + ls;
        message = message + "----------------------------------------------------------------------------" + ls;
        message = message + "LICENSE:" + ls;
        message = message + "----------------------------------------------------------------------------" + ls;
        message = message + "  BURAI is free software; you can redistribute it and/or modify" + ls;
        message = message + "  it under the terms of the GNU General Public License as published by" + ls;
        message = message + "  the Free Software Foundation, version 3 of the License." + ls;
        message = message + ls;
        message = message + "  This program is distributed in the hope that it will be useful," + ls;
        message = message + "  but WITHOUT ANY WARRANTY; without even the implied warranty of" + ls;
        message = message + "  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the" + ls;
        message = message + "  GNU General Public License for more details." + ls;
        message = message + ls;
        message = message + "  You should have received a copy of the GNU General Public License" + ls;
        message = message + "  along with this program.  If not, see <http://www.gnu.org/licenses/>." + ls;
        message = message + "----------------------------------------------------------------------------" + ls;

        this.textArea.setText(message);
    }
}
