/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.proxy;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import burai.app.QEFXMain;
import burai.com.env.Environments;

public class QEFXProxyDialog extends Dialog<ButtonType> implements Initializable {

    @FXML
    private TextField hostField;

    @FXML
    private TextField portField;

    @FXML
    private TextField userField;

    @FXML
    private PasswordField passField;

    @FXML
    private CheckBox passCheck;

    public QEFXProxyDialog() {
        super();

        DialogPane dialogPane = this.getDialogPane();
        QEFXMain.initializeStyleSheets(dialogPane.getStylesheets());
        QEFXMain.initializeDialogOwner(this);

        this.setResizable(false);
        this.setTitle("Proxy server");
        dialogPane.setHeaderText("Set proxy server.");
        dialogPane.getButtonTypes().clear();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Node node = null;
        try {
            node = this.createContent();
        } catch (Exception e) {
            node = new Label("ERROR: cannot show QEFXProxyDialog.");
            e.printStackTrace();
        }

        dialogPane.setContent(node);

        this.setResultConverter(buttonType -> {
            return buttonType;
        });
    }

    private Node createContent() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("QEFXProxyDialog.fxml"));
        fxmlLoader.setController(this);
        return fxmlLoader.load();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupHostField();
        this.setupPortField();
        this.setupUserField();
        this.setupPassField();
        this.setupPassCheck();
    }

    private void setupHostField() {
        if (this.hostField == null) {
            return;
        }

        String hostStr = Environments.getProperty(ProxyServer.PROP_KEY_HOST);
        if (hostStr == null) {
            this.hostField.setText("");
        } else {
            this.hostField.setText(hostStr);
        }
    }

    private void setupPortField() {
        if (this.portField == null) {
            return;
        }

        String portStr = Environments.getProperty(ProxyServer.PROP_KEY_PORT);
        if (portStr == null) {
            this.portField.setText("");
        } else {
            this.portField.setText(portStr);
        }
    }

    private void setupUserField() {
        if (this.userField == null) {
            return;
        }

        String userStr = Environments.getProperty(ProxyServer.PROP_KEY_USER);
        if (userStr == null) {
            this.userField.setText("");
        } else {
            this.userField.setText(userStr);
        }
    }

    private void setupPassField() {
        if (this.passField == null) {
            return;
        }

        String passStr = Environments.getProperty(ProxyServer.PROP_KEY_PASSWORD);
        if (passStr == null) {
            this.passField.setText("");
        } else {
            this.passField.setText(passStr);
        }
    }

    private void setupPassCheck() {
        if (this.passCheck == null) {
            return;
        }

        this.passCheck.setSelected(Environments.getBoolProperty(ProxyServer.PROP_KEY_SAVEPASSWORD));
    }

    public void showAndSetProperties() {
        Optional<ButtonType> optButtonType = this.showAndWait();
        if (optButtonType == null || !optButtonType.isPresent()) {
            return;
        }
        if (optButtonType.get() != ButtonType.OK) {
            return;
        }

        String hostStr = this.getHost();
        String portStr = this.getPort();
        String userStr = this.getUser();
        String passStr = this.getPassword();
        boolean passSaved = this.isPasswordSaved();

        if (hostStr != null && (!hostStr.isEmpty())) {
            Environments.setProperty(ProxyServer.PROP_KEY_HOST, hostStr);
        } else {
            Environments.setProperty(ProxyServer.PROP_KEY_HOST, null);
        }

        if (portStr != null && (!portStr.isEmpty())) {
            Environments.setProperty(ProxyServer.PROP_KEY_PORT, portStr);
        } else {
            Environments.setProperty(ProxyServer.PROP_KEY_PORT, null);
        }

        if (userStr != null && (!userStr.isEmpty())) {
            Environments.setProperty(ProxyServer.PROP_KEY_USER, userStr);
        } else {
            Environments.setProperty(ProxyServer.PROP_KEY_USER, null);
        }

        if (passSaved && passStr != null && (!passStr.isEmpty())) {
            Environments.setProperty(ProxyServer.PROP_KEY_PASSWORD, passStr);
        } else {
            Environments.setProperty(ProxyServer.PROP_KEY_PASSWORD, null);
        }

        Environments.setProperty(ProxyServer.PROP_KEY_SAVEPASSWORD, passSaved);

        ProxyServer.initProxyServer(passStr);
    }

    private String getHost() {
        if (this.hostField == null) {
            return null;
        }

        String value = this.hostField.getText();
        return value == null ? null : value.trim();
    }

    private String getPort() {
        if (this.portField == null) {
            return null;
        }

        String value = this.portField.getText();
        return value == null ? null : value.trim();
    }

    private String getUser() {
        if (this.userField == null) {
            return null;
        }

        String value = this.userField.getText();
        return value == null ? null : value.trim();
    }

    private String getPassword() {
        if (this.passField == null) {
            return null;
        }

        String value = this.passField.getText();
        return value == null ? null : value.trim();
    }

    private boolean isPasswordSaved() {
        if (this.passCheck == null) {
            return false;
        }

        return this.passCheck.isSelected();
    }
}
