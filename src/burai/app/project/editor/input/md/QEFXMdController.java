/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.md;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import burai.app.QEFXMainController;
import burai.app.project.editor.input.QEFXInputController;
import burai.app.project.editor.input.items.QEFXComboString;
import burai.app.project.editor.input.items.QEFXTextFieldDouble;
import burai.app.project.editor.input.items.QEFXTextFieldInteger;
import burai.app.project.editor.input.items.QEFXToggleString;
import burai.app.project.editor.input.items.QEFXUnit;
import burai.app.project.editor.input.items.WarningCondition;
import burai.com.consts.Constants;
import burai.input.QEInput;
import burai.input.namelist.QENamelist;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBuffer;

public class QEFXMdController extends QEFXInputController {

    private static final String[] CELL_METHOD_TEXTS = {
            "Parrinello-Rahman",
            "Wentzcovitch"
    };

    private static final String[] CELL_METHOD_VALUES = {
            "pr",
            "w"
    };

    /*
     * restart
     */
    @FXML
    private Label restartLabel;

    @FXML
    private ToggleButton restartToggle;

    @FXML
    private Button restartButton;

    /*
     * max time
     */
    @FXML
    private Label maxtimeLabel;

    @FXML
    private TextField maxtimeField;

    @FXML
    private Button maxtimeButton;

    @FXML
    private ComboBox<String> maxtimeUnit;

    /*
     * md steps
     */
    @FXML
    private Label mdstepLabel;

    @FXML
    private TextField mdstepField;

    @FXML
    private Button mdstepButton;

    /*
     * time step
     */
    @FXML
    private Label timestepLabel;

    @FXML
    private TextField timestepField;

    @FXML
    private Button timestepButton;

    @FXML
    private ComboBox<String> timestepUnit;

    /*
     * control temperature
     */
    @FXML
    private Label ctrlTempLabel;

    @FXML
    private ToggleButton ctrlTempToggle;

    @FXML
    private Button ctrlTempButton;

    /*
     * temperature
     */
    @FXML
    private Label tempLabel;

    @FXML
    private TextField tempField;

    @FXML
    private Button tempButton;

    @FXML
    private ComboBox<String> tempUnit;

    /*
     * variable cell
     */
    @FXML
    private Label vcLabel;

    @FXML
    private ToggleButton vcToggle;

    @FXML
    private Button vcButton;

    /*
     * cell's method
     */
    @FXML
    private Label cellMethodLabel;

    @FXML
    private ComboBox<String> cellMethodCombo;

    @FXML
    private Button cellMethodButton;

    /*
     * pressure
     */
    @FXML
    private Label pressLabel;

    @FXML
    private TextField pressField;

    @FXML
    private Button pressButton;

    @FXML
    private ComboBox<String> pressUnit;

    /*
     * cell's mass
     */
    @FXML
    private Label cellMassLabel;

    @FXML
    private TextField cellMassField;

    @FXML
    private Button cellMassButton;

    @FXML
    private ComboBox<String> cellMassUnit;

    /*
     * cell's freedom
     */
    @FXML
    private Label cellFreeLabel;

    @FXML
    private ComboBox<String> cellFreeCombo;

    @FXML
    private Button cellFreeButton;

    public QEFXMdController(QEFXMainController mainController, QEInput input) {
        super(mainController, input);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        QENamelist nmlControl = this.input.getNamelist(QEInput.NAMELIST_CONTROL);

        if (nmlControl != null) {
            this.setupRestartItem(nmlControl);
            this.setupMaxTimeItem(nmlControl);
            this.setupMdStepItem(nmlControl);
            this.setupTimeStepItem(nmlControl);
            this.setupVariableCellItem(nmlControl);
        }

        QENamelist nmlIons = this.input.getNamelist(QEInput.NAMELIST_IONS);

        if (nmlIons != null) {
            this.setupControlTemperatureItem(nmlIons);
            this.setupTemperatureItem(nmlIons);
        }

        QENamelist nmlCell = this.input.getNamelist(QEInput.NAMELIST_CELL);

        if (nmlCell != null) {
            QEValueBuffer calcValue = null;
            if (nmlControl != null) {
                calcValue = nmlControl.getValueBuffer("calculation");
            }

            this.setupCellMethodItem(nmlCell, calcValue);
            this.setupPressureItem(nmlCell, calcValue);
            this.setupCellMassItem(nmlCell, calcValue);
            this.setupCellFreedomItem(nmlCell, calcValue);
        }
    }

    private void setupRestartItem(QENamelist nmlControl) {
        if (this.restartToggle == null) {
            return;
        }

        QEFXToggleString item = new QEFXToggleString(
                nmlControl.getValueBuffer("restart_mode"), this.restartToggle, false, "restart", "from_scratch");

        if (this.restartLabel != null) {
            item.setLabel(this.restartLabel);
        }

        if (this.restartButton != null) {
            item.setDefault("from_scratch", this.restartButton);
        }
    }

    private void setupMaxTimeItem(QENamelist nmlControl) {
        if (this.maxtimeField == null) {
            return;
        }

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(nmlControl.getValueBuffer("max_seconds"), this.maxtimeField);

        if (this.maxtimeLabel != null) {
            item.setLabel(this.maxtimeLabel);
        }

        if (this.maxtimeButton != null) {
            item.setDefault(24.0 * 60.0 * 60.0, this.maxtimeButton); // 1day
        }

        if (this.maxtimeUnit != null) {
            item.setUnit(new QEFXUnit(this.maxtimeUnit, QEFXUnit.UNIT_TYPE_REAL_TIME));
        }

        item.setLowerBound(0.0, QEFXTextFieldDouble.BOUND_TYPE_LESS_THAN);

        item.addWarningCondition((name, value) -> {
            if ("max_seconds".equalsIgnoreCase(name)) {
                if (value != null && value.getRealValue() < 60.0) { // 1min
                    return WarningCondition.WARNING;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void setupMdStepItem(QENamelist nmlControl) {
        if (this.mdstepField == null) {
            return;
        }

        QEFXTextFieldInteger item = new QEFXTextFieldInteger(nmlControl.getValueBuffer("nstep"), this.mdstepField);

        if (this.mdstepLabel != null) {
            item.setLabel(this.mdstepLabel);
        }

        if (this.mdstepButton != null) {
            item.setDefault(5000, this.mdstepButton); // 5000 * 0.5fs = 2.5ps
        }

        item.setLowerBound(0, QEFXTextFieldInteger.BOUND_TYPE_LESS_THAN);

        item.addWarningCondition((name, value) -> {
            if ("nstep".equalsIgnoreCase(name)) {
                if (value != null && value.getIntegerValue() < 50) {
                    return WarningCondition.WARNING;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void setupTimeStepItem(QENamelist nmlControl) {
        if (this.timestepField == null) {
            return;
        }

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(nmlControl.getValueBuffer("dt"), this.timestepField);

        if (this.timestepLabel != null) {
            item.setLabel(this.timestepLabel);
        }

        if (this.timestepButton != null) {
            item.setDefault(0.5 * 0.5 / (Constants.AU_PS * 1000.0), this.timestepButton); // 0.5fs
        }

        if (this.timestepUnit != null) {
            item.setUnit(new QEFXUnit(this.timestepUnit, QEFXUnit.UNIT_TYPE_TIME_RY));
        }

        item.setLowerBound(0.0, QEFXTextFieldDouble.BOUND_TYPE_LESS_THAN);

        item.addWarningCondition((name, value) -> {
            if ("dt".equalsIgnoreCase(name)) {
                if (value != null && value.getRealValue() > (1.5 * 0.5 / (Constants.AU_PS * 1000.0))) { // 1.5fs
                    return WarningCondition.WARNING;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void setupControlTemperatureItem(QENamelist nmlIon) {
        if (this.ctrlTempToggle == null) {
            return;
        }

        QEFXToggleString item = new QEFXToggleString(
                nmlIon.getValueBuffer("ion_temperature"), this.ctrlTempToggle, false, "rescaling", "not_controlled");

        if (this.ctrlTempLabel != null) {
            item.setLabel(this.ctrlTempLabel);
        }

        if (this.ctrlTempButton != null) {
            item.setDefault("not_controlled", this.ctrlTempButton);
        }
    }

    private void setupTemperatureItem(QENamelist nmlIon) {
        if (this.tempField == null) {
            return;
        }

        QEValueBuffer tempwValue = nmlIon.getValueBuffer("tempw");
        QEValueBuffer iontempValue = nmlIon.getValueBuffer("ion_temperature");

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(tempwValue, this.tempField);

        if (this.tempLabel != null) {
            item.setLabel(this.tempLabel);
        }

        if (this.tempButton != null) {
            item.setDefault(Constants.CENTIGRADE_ZERO + 25.0, this.tempButton); // 25.0C
        }

        if (this.tempButton != null) {
            item.setUnit(new QEFXUnit(this.tempUnit, QEFXUnit.UNIT_TYPE_TEMPERATURE));
        }

        item.setLowerBound(0.0, QEFXTextFieldDouble.BOUND_TYPE_LESS_THAN);

        item.addEnablingTrigger(iontempValue);
        item.addEnabledCondition((name, value) -> {
            if ("ion_temperature".equalsIgnoreCase(name)) {
                if (value != null && "rescaling".equals(value.getCharacterValue())) {
                    return true;
                } else {
                    return false;
                }
            }

            return true;
        });

        item.addWarningTrigger(iontempValue);
        item.addWarningCondition((name, value) -> {
            if (iontempValue.hasValue() && "rescaling".equals(iontempValue.getCharacterValue())) {
                if (tempwValue.hasValue()) {
                    return WarningCondition.OK;
                } else {
                    return WarningCondition.ERROR;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void setupVariableCellItem(QENamelist nmlControl) {
        if (this.vcToggle == null) {
            return;
        }

        QEFXToggleString item = new QEFXToggleString(
                nmlControl.getValueBuffer("calculation"), this.vcToggle, false, "vc-md", "md");

        if (this.vcLabel != null) {
            item.setLabel(this.vcLabel);
        }

        if (this.vcButton != null) {
            item.setDefault("md", this.vcButton);
        }
    }

    private void setupCellMethodItem(QENamelist nmlCell, QEValueBuffer calcValue) {
        if (this.cellMethodCombo == null) {
            return;
        }

        QEValueBuffer cellDynValue = nmlCell.getValueBuffer("cell_dynamics");

        this.cellMethodCombo.getItems().clear();
        QEFXComboString item = new QEFXComboString(cellDynValue, this.cellMethodCombo);

        if (this.cellMethodLabel != null) {
            item.setLabel(this.cellMethodLabel);
        }

        if (this.cellMethodButton != null) {
            item.setDefault("pr", this.cellMethodButton);
        }

        item.addItems(CELL_METHOD_TEXTS);

        if (CELL_METHOD_TEXTS.length != CELL_METHOD_VALUES.length) {
            throw new RuntimeException("CELL_METHOD_TEXTS.length != CELL_METHOD_VALUES.length");
        }

        Map<String, String> methodMap = new HashMap<String, String>();
        for (int i = 0; i < CELL_METHOD_TEXTS.length; i++) {
            methodMap.put(CELL_METHOD_TEXTS[i], CELL_METHOD_VALUES[i]);
        }

        item.setValueFactory(text -> {
            return methodMap.get(text);
        });

        if (calcValue != null) {
            item.addEnablingTrigger(calcValue);
            item.addEnabledCondition((name, value) -> {
                if ("calculation".equalsIgnoreCase(name)) {
                    if (value != null && "vc-md".equals(value.getCharacterValue())) {
                        return true;
                    } else {
                        return false;
                    }
                }

                return true;
            });
        }

        if (calcValue != null) {
            item.addWarningTrigger(calcValue);
            item.addWarningCondition((name, value) -> {
                if ("calculation".equalsIgnoreCase(name) || "cell_dynamics".equalsIgnoreCase(name)) {
                    if (calcValue.hasValue() && "vc-md".equals(calcValue.getCharacterValue())) {
                        String cellDynStr = null;
                        if (cellDynValue.hasValue()) {
                            cellDynStr = cellDynValue.getCharacterValue();
                        }
                        for (int i = 0; i < CELL_METHOD_VALUES.length; i++) {
                            if (CELL_METHOD_VALUES[i].equals(cellDynStr)) {
                                return WarningCondition.OK;
                            }
                        }
                        return WarningCondition.ERROR;
                    }
                    return WarningCondition.OK;
                }

                return WarningCondition.OK;
            });
        }

        item.pullAllTriggers();
    }

    private void setupPressureItem(QENamelist nmlCell, QEValueBuffer calcValue) {
        if (this.pressField == null) {
            return;
        }

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(nmlCell.getValueBuffer("press"), this.pressField);

        if (this.pressLabel != null) {
            item.setLabel(this.pressLabel);
        }

        if (this.pressButton != null) {
            item.setDefault(0.0, this.pressButton);
        }

        if (this.pressUnit != null) {
            item.setUnit(new QEFXUnit(this.pressUnit, QEFXUnit.UNIT_TYPE_PRESSURE));
        }

        if (calcValue != null) {
            item.addEnablingTrigger(calcValue);
            item.addEnabledCondition((name, value) -> {
                if ("calculation".equalsIgnoreCase(name)) {
                    if (value != null && "vc-md".equals(value.getCharacterValue())) {
                        return true;
                    } else {
                        return false;
                    }
                }

                return true;
            });
        }

        item.pullAllTriggers();
    }

    private void setupCellMassItem(QENamelist nmlCell, QEValueBuffer calcValue) {
        if (this.cellMassField == null) {
            return;
        }

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(nmlCell.getValueBuffer("wmass"), this.cellMassField);

        if (this.cellMassLabel != null) {
            item.setLabel(this.cellMassLabel);
        }

        if (this.cellMassButton != null) {
            item.setDefault((QEValue) null, this.cellMassButton);
        }

        if (this.cellMassUnit != null) {
            item.setUnit(new QEFXUnit(this.cellMassUnit, QEFXUnit.UNIT_TYPE_MASS));
        }

        item.setLowerBound(0.0, QEFXTextFieldDouble.BOUND_TYPE_LESS_THAN);

        if (calcValue != null) {
            item.addEnablingTrigger(calcValue);
            item.addEnabledCondition((name, value) -> {
                if ("calculation".equalsIgnoreCase(name)) {
                    if (value != null && "vc-md".equals(value.getCharacterValue())) {
                        return true;
                    } else {
                        return false;
                    }
                }

                return true;
            });
        }

        item.pullAllTriggers();
    }

    private void setupCellFreedomItem(QENamelist nmlCell, QEValueBuffer calcValue) {
        if (this.cellFreeCombo == null) {
            return;
        }

        this.cellFreeCombo.getItems().clear();
        QEFXComboString item = new QEFXComboString(nmlCell.getValueBuffer("cell_dofree"), this.cellFreeCombo);

        if (this.cellFreeLabel != null) {
            item.setLabel(this.cellFreeLabel);
        }

        if (this.cellFreeButton != null) {
            item.setDefault("all", this.cellFreeButton);
        }

        item.addItems("all", "x", "y", "z", "xy", "xz", "yz", "xyz", "shape", "volume", "2Dxy", "2Dshape");

        if (calcValue != null) {
            item.addEnablingTrigger(calcValue);
            item.addEnabledCondition((name, value) -> {
                if ("calculation".equalsIgnoreCase(name)) {
                    if (value != null && "vc-md".equals(value.getCharacterValue())) {
                        return true;
                    } else {
                        return false;
                    }
                }

                return true;
            });
        }

        item.pullAllTriggers();
    }
}
