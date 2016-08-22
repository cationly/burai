/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.result.log;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import burai.app.project.QEFXProjectController;
import burai.app.project.editor.result.QEFXResultEditorController;
import burai.app.project.viewer.result.log.QEFXLogViewerController;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;

public class QEFXLogEditorController extends QEFXResultEditorController<QEFXLogViewerController> {

    private static final double GRAPHIC_SIZE = 14.0;
    private static final String GRAPHIC_CLASS = "picshadow-button";

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private TextArea searchArea;

    public QEFXLogEditorController(QEFXProjectController projectController, QEFXLogViewerController viewerController) {
        super(projectController, viewerController);
    }

    @Override
    protected void setupFXComponents() {
        this.setupSearchButton();
        this.setupSearchField();
        this.setupSearchArea();
    }

    public void focusSearchingField() {
        if (this.searchField != null) {
            this.searchField.requestFocus();
        }
    }

    private void setupSearchField() {
        if (this.searchField == null) {
            return;
        }

        this.searchField.setText("");
        this.searchField.setOnAction(event -> this.searchText());
    }

    private void setupSearchButton() {
        if (this.searchButton == null) {
            return;
        }

        this.searchButton.setText(" ");
        this.searchButton.setGraphic(SVGLibrary.getGraphic(SVGData.SEARCH, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.searchButton.setOnAction(event -> {
            if (this.searchField != null) {
                this.searchField.requestFocus();
            }
            this.searchText();
        });
    }

    private void setupSearchArea() {
        if (this.searchArea == null) {
            return;
        }

        this.searchArea.setText("");
    }

    private void searchText() {
        if (this.searchField == null) {
            return;
        }

        String text = this.searchField.getText();
        if (text != null) {
            text = text.trim();
        }

        List<String> searchedList = null;
        if (this.viewerController != null) {
            searchedList = this.viewerController.searchText(text);
        }

        if (this.searchArea == null) {
            return;
        }

        this.searchArea.setText("");
        if (searchedList != null) {
            for (String searchedStr : searchedList) {
                if (searchedStr != null) {
                    this.searchArea.appendText(searchedStr + System.lineSeparator());
                }
            }
        }
    }
}
