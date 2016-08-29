/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.QEFXResultViewerController;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;

public class QEFXLogViewerController extends QEFXResultViewerController {

    private static final double GRAPHIC_SIZE = 20.0;
    private static final String GRAPHIC_CLASS = "piclight-button";

    private static final String PLACEHOLDER_STYLE = "-fx-background-color: lightgray";

    private File file;

    private String searchingText;

    private List<FileLine> searchedList;

    @FXML
    private TextField nameField;

    @FXML
    private Button nextButton;

    @FXML
    private Button prevButton;

    @FXML
    private ListView<FileLine> listView;

    public QEFXLogViewerController(QEFXProjectController projectController, File file) {
        super(projectController);

        if (file == null) {
            throw new IllegalArgumentException("file is null.");
        }

        this.file = file;

        this.searchingText = null;
        this.searchedList = new ArrayList<FileLine>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupNameField();
        this.setupNextButton();
        this.setupPrevButton();
        this.setupListView();
    }

    @Override
    public void reload() {
        if (this.listView != null) {
            this.listView.getItems().clear();
        }

        this.searchedList.clear();

        Platform.runLater(() -> {
            this.readFile();
        });

        boolean emptySearched = this.searchedList.isEmpty();
        if (this.nextButton != null) {
            this.nextButton.setDisable(emptySearched);
        }
        if (this.prevButton != null) {
            this.prevButton.setDisable(emptySearched);
        }
    }

    public List<String> searchText(String text) {
        this.searchingText = text == null ? null : text.trim();

        this.reload();

        List<String> lineList = new ArrayList<String>();
        for (FileLine fileLine : this.searchedList) {
            String line = fileLine == null ? null : fileLine.getLine();
            if (line != null) {
                lineList.add(line);
            }
        }

        return lineList;
    }

    private void setupNameField() {
        if (this.nameField == null) {
            return;
        }

        String name = "file:///";
        String path = this.file.getPath();
        if (path != null) {
            name = name + path;
        }

        this.nameField.setText(name);
        this.nameField.setOnAction(event -> this.reload());
    }

    private void setupNextButton() {
        if (this.nextButton == null) {
            return;
        }

        this.nextButton.setText("");
        this.nextButton.setTooltip(new Tooltip("next searched"));
        this.nextButton.setDisable(true);
        this.nextButton.setGraphic(SVGLibrary.getGraphic(SVGData.ARROW_DOWN, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.nextButton.setOnAction(event -> {
            if (this.listView != null) {
                this.listView.requestFocus();
            }
            this.selectSearchedLine(true);
        });
    }

    private void setupPrevButton() {
        if (this.prevButton == null) {
            return;
        }

        this.prevButton.setText("");
        this.prevButton.setTooltip(new Tooltip("previous searched"));
        this.prevButton.setDisable(true);
        this.prevButton.setGraphic(SVGLibrary.getGraphic(SVGData.ARROW_UP, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.prevButton.setOnAction(event -> {
            if (this.listView != null) {
                this.listView.requestFocus();
            }
            this.selectSearchedLine(false);
        });
    }

    private void setupListView() {
        if (this.listView == null) {
            return;
        }

        this.listView.setCellFactory(listView_ -> {
            return new FileLineCell();
        });

        Button reloadButton = new Button(" NO DATA, PUSH TO RELOAD.");
        reloadButton.getStyleClass().add(GRAPHIC_CLASS);
        reloadButton.setGraphic(SVGLibrary.getGraphic(SVGData.ARROW_ROUND, GRAPHIC_SIZE, null, GRAPHIC_CLASS));
        reloadButton.setOnAction(event -> this.reload());

        BorderPane borderPane = new BorderPane(reloadButton);
        borderPane.setStyle(PLACEHOLDER_STYLE);

        this.listView.setPlaceholder(borderPane);
    }

    private void readFile() {
        if (this.listView == null) {
            return;
        }

        String text = null;
        if (this.searchingText != null) {
            text = this.searchingText.trim().toUpperCase();
        }

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(this.file));

            int index = 0;
            String line = null;

            while ((line = reader.readLine()) != null) {
                index++;

                boolean enhanced = false;
                if (text != null && (!text.isEmpty())) {
                    enhanced = (line.toUpperCase().indexOf(text) > -1);
                }

                FileLine fileLien = new FileLine(index, line, enhanced);
                this.listView.getItems().add(fileLien);

                if (enhanced) {
                    this.searchedList.add(fileLien);
                }
            }

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();

        } catch (IOException e2) {
            e2.printStackTrace();

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }

    private void selectSearchedLine(boolean direction) {
        if (this.listView == null) {
            return;
        }

        if (this.searchedList.isEmpty()) {
            return;
        }

        MultipleSelectionModel<FileLine> selectionModel = this.listView.getSelectionModel();
        if (selectionModel == null) {
            return;
        }

        int selectedIndex = -1;

        FileLine fileLine = selectionModel.getSelectedItem();
        if (fileLine == null) {
            fileLine = new FileLine(1, "", false);
        }

        while (true) {
            int index = Collections.binarySearch(this.searchedList, fileLine);

            if (index < 0) {
                int index2 = -(index + 1);
                if (direction) {
                    selectedIndex = index2;
                } else {
                    selectedIndex = index2 - 1;
                }
                break;
            }

            if (direction) {
                fileLine = new FileLine(fileLine.getIndex() + 1, "", false);
            } else {
                fileLine = new FileLine(fileLine.getIndex() - 1, "", false);
            }
        }

        FileLine selectedLine = null;
        if (0 <= selectedIndex && selectedIndex < this.searchedList.size()) {
            selectedLine = this.searchedList.get(selectedIndex);
        }

        if (selectedLine != null) {
            this.listView.scrollTo(selectedLine);
            selectionModel.select(selectedLine);
        }
    }
}
