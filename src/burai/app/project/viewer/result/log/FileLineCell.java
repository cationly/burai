/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.log;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class FileLineCell extends ListCell<FileLine> {

    private static final double INDEX_WIDTH = 75.0;
    private static final String INDEX_CLASS = "result-log-index";
    private static final String TEXT_CLASS = "result-log-text";
    private static final String SEARCHED_CLASS = "result-log-searched";

    public FileLineCell() {
        // NOP
    }

    @Override
    protected void updateItem(FileLine fileLine, boolean empty) {
        super.updateItem(fileLine, empty);

        if (empty || fileLine == null) {
            this.setGraphic(null);
            while (this.getStyleClass().remove(SEARCHED_CLASS)) {
            }
            return;
        }

        Label indexLabel = new Label(Integer.toString(fileLine.getIndex()));
        indexLabel.getStyleClass().add(INDEX_CLASS);
        Pane indexPane = new Pane(indexLabel);
        indexPane.setPrefWidth(INDEX_WIDTH);

        Label lineLabel = new Label(fileLine.getLine());
        lineLabel.getStyleClass().add(TEXT_CLASS);
        Pane linePane = new Pane(lineLabel);

        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(indexPane);
        borderPane.setCenter(linePane);

        if (fileLine.isEnhanced()) {
            if (!this.getStyleClass().contains(SEARCHED_CLASS)) {
                this.getStyleClass().add(SEARCHED_CLASS);
            }

        } else {
            while (this.getStyleClass().remove(SEARCHED_CLASS)) {
            }
        }

        this.setGraphic(borderPane);
    }
}
