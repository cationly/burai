/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.geom;

import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import burai.app.QEFXMain;
import burai.app.project.editor.input.items.QEFXItem;
import burai.com.consts.Constants;
import burai.com.math.Calculator;
import burai.com.math.Matrix3D;
import burai.input.QEInput;
import burai.input.card.QECard;
import burai.input.card.QECardEvent;
import burai.input.card.QECellParameters;
import burai.input.namelist.QENamelist;
import burai.input.namelist.QEValue;

public class QEFXLatticeVector {

    private static final String UNIT_ALAT = "Alat";
    private static final String UNIT_BOHR = "Bohr";
    private static final String UNIT_ANGSTROM = "Angstrom";

    private boolean busyScreen;
    private boolean busyQEInput;

    private QEInput input;

    private TextField[][] lattFields;

    private Label[] lattLabels;

    private Button lattButton;

    private ComboBox<String> lattUnit;

    private String[][] originalStyles;

    private EventHandler<ActionEvent> onUnitChanged;

    public QEFXLatticeVector(QEInput input,
            TextField[][] lattFields, Label[] lattLabels, Button lattButton, ComboBox<String> lattUnit) {
        if (input == null) {
            throw new IllegalArgumentException("input is null.");
        }

        if (lattFields == null || lattFields.length < 3) {
            throw new IllegalArgumentException("lattFields is incorrect.");
        }

        for (int i = 0; i < 3; i++) {
            if (lattFields[i] == null || lattFields[i].length < 3) {
                throw new IllegalArgumentException("lattFields is incorrect.");
            }
            for (int j = 0; j < 3; j++) {
                if (lattFields[i][j] == null) {
                    throw new IllegalArgumentException("lattFields is incorrect.");
                }
            }
        }

        if (lattLabels == null || lattLabels.length < 3) {
            throw new IllegalArgumentException("lattLabels is incorrect.");
        }

        for (int i = 0; i < 3; i++) {
            if (lattLabels[i] == null) {
                throw new IllegalArgumentException("lattLabels is incorrect.");
            }
        }

        if (lattButton == null) {
            throw new IllegalArgumentException("lattButton is null.");
        }

        if (lattUnit == null) {
            throw new IllegalArgumentException("lattUnit is null.");
        }

        this.busyScreen = false;
        this.busyQEInput = false;
        this.input = input;
        this.lattFields = lattFields;
        this.lattLabels = lattLabels;
        this.lattButton = lattButton;
        this.lattUnit = lattUnit;
        this.originalStyles = null;
        this.onUnitChanged = null;

        this.initialize();
    }

    public boolean isAlat() {
        String value = this.lattUnit.getValue();
        return UNIT_ALAT.equals(value);
    }

    public boolean isBohr() {
        String value = this.lattUnit.getValue();
        return UNIT_BOHR.equals(value);
    }

    public boolean isAngstrom() {
        String value = this.lattUnit.getValue();
        return UNIT_ANGSTROM.equals(value);
    }

    public void setDisable(boolean disable) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.lattFields[i][j].setDisable(disable);
            }
        }

        for (int i = 0; i < 3; i++) {
            this.lattLabels[i].setDisable(disable);
        }

        this.lattButton.setDisable(disable);

        this.lattUnit.setDisable(disable);
    }

    public void setOnUnitChanged(EventHandler<ActionEvent> handler) {
        this.onUnitChanged = handler;
    }

    private void initialize() {
        this.createOriginalStyles();
        this.setupLattFields();
        this.setupLattButton();
        this.setupLattUnit();
        this.setupCellParameters();
        this.setupIBrav();
    }

    private void createOriginalStyles() {
        this.originalStyles = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.originalStyles[i][j] = this.lattFields[i][j].getStyle();
            }
        }
    }

    private void setupLattFields() {
        this.updateLatticeVector();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.setupLattField(i, j);
            }
        }
    }

    private void setupLattField(int i, int j) {
        TextField lattField = this.lattFields[i][j];
        if (lattField == null) {
            return;
        }

        lattField.textProperty().addListener(o -> {
            try {
                Calculator.expr(lattField.getText());
                lattField.setStyle(this.originalStyles[i][j]);
            } catch (Exception e) {
                lattField.setStyle(QEFXItem.ERROR_STYLE);
            }

            if (this.busyQEInput) {
                return;
            }

            double[] vector = new double[3];
            try {
                vector[0] = Calculator.expr(this.lattFields[i][0].getText());
            } catch (Exception e) {
                vector[0] = 0.0;
            }
            try {
                vector[1] = Calculator.expr(this.lattFields[i][1].getText());
            } catch (Exception e) {
                vector[1] = 0.0;
            }
            try {
                vector[2] = Calculator.expr(this.lattFields[i][2].getText());
            } catch (Exception e) {
                vector[2] = 0.0;
            }

            QECard card = this.input.getCard(QECellParameters.CARD_NAME);
            if (card == null || !(card instanceof QECellParameters)) {
                return;
            }

            QECellParameters cellParameters = (QECellParameters) card;

            this.busyScreen = true;
            cellParameters.setVector(i + 1, vector);
            this.busyScreen = false;
        });
    }

    private String dataToString(double value) {
        String strValue = String.format("%12.8f", value);
        return strValue;
    }

    private void updateLatticeVector() {
        QECard card = this.input.getCard(QECellParameters.CARD_NAME);
        if (card == null || !(card instanceof QECellParameters)) {
            return;
        }

        QECellParameters cellParameters = (QECellParameters) card;

        double[][] lattice = new double[3][];
        lattice[0] = cellParameters.getVector1();
        lattice[1] = cellParameters.getVector2();
        lattice[2] = cellParameters.getVector3();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.lattFields[i][j].setText(this.dataToString(lattice[i][j]));
            }
        }
    }

    private void setupLattButton() {
        QEFXItem.setupDefaultButton(this.lattButton);
    }

    private void setupLattUnit() {
        this.lattUnit.getItems().clear();
        this.lattUnit.getItems().add(UNIT_ALAT);
        this.lattUnit.getItems().add(UNIT_BOHR);
        this.lattUnit.getItems().add(UNIT_ANGSTROM);

        this.updateLatticeUnit();

        this.lattUnit.setOnAction(event -> {
            if (this.busyQEInput) {
                return;
            }

            QECard card = this.input.getCard(QECellParameters.CARD_NAME);
            if (card == null || !(card instanceof QECellParameters)) {
                return;
            }

            double unitOld = 1.0;
            double unitNew = 1.0;

            QECellParameters cellParameters = (QECellParameters) card;

            this.busyScreen = true;

            unitOld = this.getCurrentUnit(cellParameters);

            String value = this.lattUnit.getValue();
            if (UNIT_ALAT.equals(value)) {
                cellParameters.setAlat();
            } else if (UNIT_BOHR.equals(value)) {
                cellParameters.setBohr();
            } else if (UNIT_ANGSTROM.equals(value)) {
                cellParameters.setAngstrom();
            }

            unitNew = this.getCurrentUnit(cellParameters);

            if (this.onUnitChanged != null) {
                this.onUnitChanged.handle(event);
            }

            this.busyScreen = false;

            Alert alert = new Alert(AlertType.CONFIRMATION);
            QEFXMain.initializeDialogOwner(alert);
            alert.setHeaderText("Convert lattice vectors ?");
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> optButtonType = alert.showAndWait();
            if (optButtonType != null && optButtonType.isPresent() && optButtonType.get() == ButtonType.YES) {
                double[] vector1 = cellParameters.getVector1();
                double[] vector2 = cellParameters.getVector2();
                double[] vector3 = cellParameters.getVector3();
                cellParameters.stopListeners();
                cellParameters.setVector(1, Matrix3D.mult(unitOld / unitNew, vector1));
                cellParameters.setVector(2, Matrix3D.mult(unitOld / unitNew, vector2));
                cellParameters.setVector(3, Matrix3D.mult(unitOld / unitNew, vector3));
                cellParameters.restartListeners();
            }
        });
    }

    private double getCurrentUnit(QECellParameters cellParameters) {
        double unit = 1.0;

        if (cellParameters.isAlat()) {
            QEValue value = null;
            QENamelist nmlSystem = this.input.getNamelist(QEInput.NAMELIST_SYSTEM);
            if (nmlSystem != null) {
                value = nmlSystem.getValue("a");
            }
            unit = value == null ? 1.0 : value.getRealValue();

        } else if (cellParameters.isBohr()) {
            unit = Constants.BOHR_RADIUS_ANGS;

        } else if (cellParameters.isAngstrom()) {
            unit = 1.0;
        }

        if (unit <= 0.0) {
            unit = 1.0;
        }

        return unit;
    }

    private void updateLatticeUnit() {
        QECard card = this.input.getCard(QECellParameters.CARD_NAME);
        if (card == null || !(card instanceof QECellParameters)) {
            return;
        }

        QECellParameters cellParameters = (QECellParameters) card;
        if (cellParameters.isAlat()) {
            this.lattUnit.setValue(UNIT_ALAT);
        } else if (cellParameters.isBohr()) {
            this.lattUnit.setValue(UNIT_BOHR);
        } else if (cellParameters.isAngstrom()) {
            this.lattUnit.setValue(UNIT_ANGSTROM);
        } else {
            QEValue value = null;
            QENamelist nmlSystem = this.input.getNamelist(QEInput.NAMELIST_SYSTEM);
            if (nmlSystem != null) {
                value = nmlSystem.getValue("a");
            }
            if (value != null) {
                this.lattUnit.setValue(UNIT_ALAT);
            } else {
                this.lattUnit.setValue(UNIT_BOHR);
            }
        }
    }

    private void setupCellParameters() {
        QECard card = this.input.getCard(QECellParameters.CARD_NAME);
        if (card == null || !(card instanceof QECellParameters)) {
            return;
        }

        QECellParameters cellParameters = (QECellParameters) card;

        cellParameters.addListener(event -> {
            if (event == null) {
                return;
            }

            if (this.busyScreen) {
                return;
            }

            this.busyQEInput = true;

            int eventType = event.getEventType();
            if (eventType == QECardEvent.EVENT_TYPE_UNIT_CHANGED) {
                this.updateLatticeUnit();
            } else {
                this.updateLatticeUnit();
                this.updateLatticeVector();
            }

            this.busyQEInput = false;
        });
    }

    private void setupIBrav() {
        QENamelist nmlSystem = this.input.getNamelist(QEInput.NAMELIST_SYSTEM);
        if (nmlSystem == null) {
            return;
        }

        this.updateStatus(nmlSystem.getValue("ibrav"));
        nmlSystem.getValueBuffer("ibrav").addListener(value -> this.updateStatus(value));
    }

    private void updateStatus(QEValue ibravValue) {
        if (ibravValue != null && ibravValue.getIntegerValue() == 0) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    String text = this.lattFields[i][j].getText();
                    this.lattFields[i][j].setText(text);
                }
            }
            this.setDisable(false);

        } else {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    this.lattFields[i][j].setStyle(this.originalStyles[i][j]);
                }
            }
            this.setDisable(true);
        }
    }

    public void setDefault(EventHandler<ActionEvent> handler) {
        this.lattButton.setOnAction(handler);
    }
}
