/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.explorer.body;

import java.io.File;
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
import javafx.scene.control.TextField;
import burai.app.QEFXMain;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;

public class QEFXFileNameDialog extends Dialog<File> implements Initializable {

    private static final double GRAPHIC_SIZE = 20.0;
    private static final String GRAPHIC_CLASS = "picheavy-button";

    private File parentFile;

    private boolean directoryMode;

    private String extension;

    @FXML
    private Label fileNameLabel;

    @FXML
    private TextField fileNameField;

    @FXML
    private Label messageLabel;

    public QEFXFileNameDialog(String title, File parentFile) {
        this(title, parentFile, false);
    }

    public QEFXFileNameDialog(String title, File parentFile, boolean directoryMode) {
        super();

        if (parentFile == null) {
            throw new IllegalArgumentException("parentFile is null.");
        }

        this.parentFile = parentFile;
        this.directoryMode = directoryMode;
        this.extension = null;

        DialogPane dialogPane = this.getDialogPane();
        QEFXMain.initializeStyleSheets(dialogPane.getStylesheets());
        QEFXMain.initializeDialogOwner(this);

        this.setResizable(false);

        // setup title
        if (title != null && (!title.isEmpty())) {
            this.setTitle(title);
        } else {
            if (!this.directoryMode) {
                this.setTitle("Set a file name");
            } else {
                this.setTitle("Set a directory name");
            }
        }

        // setup header
        if (!this.directoryMode) {
            dialogPane.setHeaderText("Set a file name.");
        } else {
            dialogPane.setHeaderText("Set a directory name.");
        }

        // setup buttons
        this.setupButtonTypes(false);

        // setup content
        Node node = null;
        try {
            node = this.createContent();
        } catch (Exception e) {
            node = new Label("ERROR: cannot show QEFXFileNameDialog.");
            e.printStackTrace();
        }

        dialogPane.setContent(node);

        // setup callback
        this.setResultConverter(buttonType -> {
            if (ButtonType.OK.equals(buttonType)) {
                try {
                    File file = this.getInputtedFile();
                    if (file != null && (!file.exists())) {
                        return file;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        });
    }

    public void setExtension(String extension) {
        if (extension != null) {
            int index = extension.lastIndexOf(".");
            if (index > -1) {
                this.extension = extension.substring(index);
                if (this.extension != null) {
                    this.extension = this.extension.trim();
                }

            } else {
                this.extension = null;
            }

        } else {
            this.extension = extension;
        }
    }

    private Node createContent() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("QEFXFileNameDialog.fxml"));
        fxmlLoader.setController(this);
        return fxmlLoader.load();
    }

    private void setupButtonTypes(boolean withOK) {
        DialogPane dialogPane = this.getDialogPane();
        if (dialogPane == null) {
            return;
        }

        dialogPane.getButtonTypes().clear();
        if (withOK) {
            dialogPane.getButtonTypes().add(ButtonType.OK);
        }
        dialogPane.getButtonTypes().add(ButtonType.CANCEL);
    }

    private File getInputtedFile() {
        if (this.fileNameField == null) {
            return null;
        }

        File file = null;

        String fileName = this.fileNameField.getText();
        if (fileName != null) {
            fileName = fileName.trim();
        }

        if (fileName != null && (!fileName.isEmpty())) {
            if ((!this.directoryMode) && (this.extension != null && !this.extension.isEmpty())) {
                if (!this.checkExtension(fileName)) {
                    fileName = fileName + this.extension;
                }
            }

            file = new File(this.parentFile, fileName);
        }

        return file;
    }

    private boolean checkExtension(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        if (this.extension == null || this.extension.isEmpty()) {
            return false;
        }

        String lowExt = this.extension.toLowerCase();
        String lowName = name.toLowerCase();
        return lowName.endsWith(lowExt);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupFileNameLabel();
        this.setupFileNameField();
        this.setupMessageLabel();
    }

    private void setupFileNameLabel() {
        if (this.fileNameLabel == null) {
            return;
        }

        this.fileNameLabel.setText(" : ");
        this.fileNameLabel.getStyleClass().add(GRAPHIC_CLASS);

        if (!this.directoryMode) {
            this.fileNameLabel.setGraphic(SVGLibrary.getGraphic(SVGData.FILE, GRAPHIC_SIZE, null, GRAPHIC_CLASS));
        } else {
            this.fileNameLabel.setGraphic(SVGLibrary.getGraphic(SVGData.FOLDER, GRAPHIC_SIZE, null, GRAPHIC_CLASS));
        }
    }

    private void setupFileNameField() {
        if (this.fileNameField == null) {
            return;
        }

        this.fileNameField.textProperty().addListener(o -> {
            File file = this.getInputtedFile();
            if (file == null) {
                if (this.messageLabel != null) {
                    this.messageLabel.setText("");
                }
                this.setupButtonTypes(false);
                this.fileNameField.requestFocus();
                return;
            }

            try {
                if (!file.exists()) {
                    if (this.messageLabel != null) {
                        this.messageLabel.setText("");
                    }
                    this.setupButtonTypes(true);
                    this.fileNameField.requestFocus();

                } else {
                    if (this.messageLabel != null) {
                        if (!file.isDirectory()) {
                            this.messageLabel.setText("This file already exists: " + file.getName() + ".");
                        } else {
                            this.messageLabel.setText("This directory already exists: " + file.getName() + ".");
                        }
                    }
                    this.setupButtonTypes(false);
                    this.fileNameField.requestFocus();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        this.setOnShown(event -> {
            if (this.fileNameField != null) {
                this.fileNameField.requestFocus();
            }
        });
    }

    private void setupMessageLabel() {
        if (this.messageLabel == null) {
            return;
        }

        this.messageLabel.setText("");
    }
}
