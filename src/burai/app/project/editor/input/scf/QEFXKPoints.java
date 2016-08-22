/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.scf;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import burai.app.project.editor.input.items.QEFXItem;
import burai.input.card.QECardEvent;
import burai.input.card.QEKPoints;

public class QEFXKPoints {

    private static final String SPECIAL_K_NUMBER = "*";

    private boolean busyScreen;

    private QEKPoints card;

    private TextField[] kpointFields;

    private Label kpointLabel;

    private Button kpointButton;

    private String[] originalStyles;

    public QEFXKPoints(QEKPoints card, TextField[] kpointFields, Label kpointLabel, Button kpointButton) {
        if (card == null) {
            throw new IllegalArgumentException("card is null.");
        }

        if (kpointFields == null || kpointFields.length < 3) {
            throw new IllegalArgumentException("kpointFields is incorrect.");
        }

        for (int i = 0; i < 3; i++) {
            if (kpointFields[i] == null) {
                throw new IllegalArgumentException("kpointFields is incorrect.");
            }
        }

        if (kpointLabel == null) {
            throw new IllegalArgumentException("kpointLabel is null.");
        }

        if (kpointButton == null) {
            throw new IllegalArgumentException("kpointButton is null.");
        }

        this.busyScreen = false;
        this.card = card;
        this.kpointFields = kpointFields;
        this.kpointLabel = kpointLabel;
        this.kpointButton = kpointButton;
        this.originalStyles = null;

        this.initialize();
    }

    public void setDisable(boolean disable) {
        for (int i = 0; i < 3; i++) {
            this.kpointFields[i].setDisable(disable);
        }

        this.kpointLabel.setDisable(disable);

        this.kpointButton.setDisable(disable);
    }

    private void initialize() {
        this.createOriginalStyles();
        this.setupKpointFields();
        this.setupKpointButton();
        this.setupCardKPoints();
    }

    private void createOriginalStyles() {
        this.originalStyles = new String[3];
        for (int i = 0; i < 3; i++) {
            this.originalStyles[i] = this.kpointFields[i].getStyle();
        }
    }

    private void setupKpointFields() {
        this.updateKpointFields();

        for (int i = 0; i < 3; i++) {
            this.setupKpointField(i);
        }
    }

    private void updateKpointFieldStyle(int i) {
        TextField kpointField = this.kpointFields[i];
        if (kpointField == null) {
            return;
        }

        String kpointStyle = this.originalStyles[i];

        if (SPECIAL_K_NUMBER.equals(kpointField.getText())) {
            kpointStyle = QEFXItem.WARNING_STYLE;

        } else {
            try {
                Integer.parseInt(kpointField.getText());
            } catch (Exception e) {
                kpointStyle = QEFXItem.ERROR_STYLE;
            }
        }

        kpointField.setStyle(kpointStyle);
    }

    private void setupKpointField(int i) {
        TextField kpointField = this.kpointFields[i];
        if (kpointField == null) {
            return;
        }

        this.updateKpointFieldStyle(i);

        kpointField.setTooltip(new Tooltip("0 < nk" + (i + 1) + " < Inf."));

        kpointField.textProperty().addListener(o -> {
            this.updateKpointFieldStyle(i);

            int[] kGrid = new int[3];
            try {
                kGrid[0] = Math.max(1, Integer.parseInt(this.kpointFields[0].getText()));
            } catch (Exception e) {
                kGrid[0] = 0;
            }
            try {
                kGrid[1] = Math.max(1, Integer.parseInt(this.kpointFields[1].getText()));
            } catch (Exception e) {
                kGrid[1] = 0;
            }
            try {
                kGrid[2] = Math.max(1, Integer.parseInt(this.kpointFields[2].getText()));
            } catch (Exception e) {
                kGrid[2] = 0;
            }

            int kMult = kGrid[0] * kGrid[1] * kGrid[2];

            if (kMult > 0) {
                this.busyScreen = true;

                if (kMult > 1) {
                    if (!this.card.isAutomatic()) {
                        this.card.setAutomatic();
                    }
                } else {
                    if (!this.card.isGamma()) {
                        this.card.setGamma();
                    }
                }

                this.card.setKGrid(kGrid);

                this.busyScreen = false;
            }
        });
    }

    private void updateKpointFields() {
        if (this.card.isGamma()) {
            for (int i = 0; i < 3; i++) {
                this.kpointFields[i].setText("1");
            }

        } else if (this.card.isAutomatic()) {
            int[] kGrid = this.card.getKGrid();
            for (int i = 0; i < 3; i++) {
                this.kpointFields[i].setText(String.valueOf(kGrid[i]));
            }

        } else {
            for (int i = 0; i < 3; i++) {
                this.kpointFields[i].setText(SPECIAL_K_NUMBER);
            }
        }
    }

    private void setupKpointButton() {
        QEFXItem.setupDefaultButton(this.kpointButton);
    }

    private void setupCardKPoints() {
        this.card.addListener(event -> {
            if (event == null) {
                return;
            }

            if (this.busyScreen) {
                return;
            }

            int eventType = event.getEventType();
            if (eventType == QECardEvent.EVENT_TYPE_KGRID_CHANGED
                    || eventType == QECardEvent.EVENT_TYPE_UNIT_CHANGED
                    || eventType == QECardEvent.EVENT_TYPE_NULL) {
                this.updateKpointFields();
            }
        });
    }

    public void setDefault(EventHandler<ActionEvent> handler) {
        this.kpointButton.setOnAction(handler);
    }
}
