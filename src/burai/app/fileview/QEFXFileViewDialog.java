/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.fileview;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import burai.app.QEFXMain;

public class QEFXFileViewDialog extends Dialog<File> {

    private static final double TEXTAREA_WIDTH = 800.0;
    private static final double TEXTAREA_HEIGHT = 400.0;
    private static final String TEXTAREA_CLASS = "inputfile-text";

    private File file;

    public QEFXFileViewDialog(String path) {
        this(path == null ? null : new File(path));
    }

    public QEFXFileViewDialog(File file) {
        super();
        if (file == null) {
            throw new IllegalArgumentException("file is null.");
        }

        this.file = file;

        DialogPane dialogPane = this.getDialogPane();
        QEFXMain.initializeStyleSheets(dialogPane.getStylesheets());
        QEFXMain.initializeDialogOwner(this);

        this.setResizable(true);
        this.initModality(Modality.WINDOW_MODAL);
        this.setTitle("File: " + this.file.getName());
        dialogPane.setHeaderText("File: " + this.file.getPath() + ".");
        dialogPane.getButtonTypes().clear();
        dialogPane.getButtonTypes().add(ButtonType.CLOSE);

        Node node = null;
        try {
            node = this.createContent();
        } catch (Exception e) {
            node = new Label("ERROR: cannot show QEFXFileViewDialog.");
            e.printStackTrace();
        }

        dialogPane.setContent(node);

        this.setResultConverter(buttonType -> {
            return this.file;
        });
    }

    private Node createContent() throws IOException {
        String text = this.readFile();
        TextArea textArea = new TextArea();
        textArea.setPrefWidth(TEXTAREA_WIDTH);
        textArea.setPrefHeight(TEXTAREA_HEIGHT);
        textArea.getStyleClass().add(TEXTAREA_CLASS);
        textArea.setEditable(false);
        textArea.setText(text);
        return textArea;
    }

    private String readFile() throws IOException {
        BufferedReader reader = null;
        StringBuilder strBuilder = new StringBuilder();

        try {
            reader = new BufferedReader(new FileReader(this.file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuilder.append(line);
                strBuilder.append(System.lineSeparator());
            }

        } catch (FileNotFoundException e1) {
            throw e1;

        } catch (IOException e2) {
            throw e2;

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e3) {
                    throw e3;
                }
            }
        }

        return strBuilder.toString();
    }
}
