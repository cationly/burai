/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.opt;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import burai.app.QEFXMainController;
import burai.app.project.editor.input.QEFXInputController;
import burai.app.project.editor.input.items.QEFXComboString;
import burai.app.project.editor.input.items.QEFXSliderInteger;
import burai.app.project.editor.input.items.QEFXTextFieldDouble;
import burai.app.project.editor.input.items.QEFXToggleString;
import burai.app.project.editor.input.items.QEFXUnit;
import burai.app.project.editor.input.items.WarningCondition;
import burai.input.QEInput;
import burai.input.namelist.QENamelist;
import burai.input.namelist.QEValueBase;
import burai.input.namelist.QEValueBuffer;

public class QEFXOptController extends QEFXInputController {

    private static final String[] IONS_METHOD_TEXTS = {
            "BFGS",
            "Damped-MD"
    };

    private static final String[] IONS_METHOD_VALUES = {
            "bfgs",
            "damp"
    };

    private static final String[] CELL_METHOD_TEXTS = {
            "BFGS",
            "Damped-MD (P.R.)",
            "Damped-MD (W.)"
    };

    private static final String[] CELL_METHOD_VALUES = {
            "bfgs",
            "damp-pr",
            "damp-w"
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
     * max steps
     */
    @FXML
    private Label maxstepLabel;

    @FXML
    private Slider maxstepSlider;

    @FXML
    private Button maxstepButton;

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
     * ion's convergence
     */
    @FXML
    private Label ionConvLabel;

    @FXML
    private TextField ionConvField;

    @FXML
    private Button ionConvButton;

    @FXML
    private ComboBox<String> ionConvUnit;

    /*
     * ion's method
     */
    @FXML
    private Label ionMethodLabel;

    @FXML
    private ComboBox<String> ionMethodCombo;

    @FXML
    private Button ionMethodButton;

    /*
     * cell's convergence
     */
    @FXML
    private Label cellConvLabel;

    @FXML
    private TextField cellConvField;

    @FXML
    private Button cellConvButton;

    @FXML
    private ComboBox<String> cellConvUnit;

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
     * cell's freedom
     */
    @FXML
    private Label cellFreeLabel;

    @FXML
    private ComboBox<String> cellFreeCombo;

    @FXML
    private Button cellFreeButton;

    public QEFXOptController(QEFXMainController mainController, QEInput input) {
        super(mainController, input);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        QENamelist nmlControl = this.input.getNamelist(QEInput.NAMELIST_CONTROL);

        if (nmlControl != null) {
            this.setupRestartItem(nmlControl);
            this.setupMaxTimeItem(nmlControl);
            this.setupMaxStepItem(nmlControl);
            this.setupVariableCellItem(nmlControl);
            this.setupIonsConvergenceItem(nmlControl);
        }

        QENamelist nmlIons = this.input.getNamelist(QEInput.NAMELIST_IONS);

        if (nmlIons != null) {
            this.setupIonsMethodItem(nmlIons);
        }

        QENamelist nmlCell = this.input.getNamelist(QEInput.NAMELIST_CELL);

        if (nmlCell != null) {
            QEValueBuffer calcValue = null;
            if (nmlControl != null) {
                calcValue = nmlControl.getValueBuffer("calculation");
            }

            QEValueBuffer iondynValue = null;
            if (nmlIons != null) {
                iondynValue = nmlIons.getValueBuffer("ion_dynamics");
            }

            this.setupCellConvergenceItem(nmlCell, calcValue);
            this.setupCellMethodItem(nmlCell, calcValue, iondynValue);
            this.setupPressureItem(nmlCell, calcValue);
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

    private void setupMaxStepItem(QENamelist nmlControl) {
        if (this.maxstepSlider == null) {
            return;
        }

        QEFXSliderInteger item = new QEFXSliderInteger(
                nmlControl.getValueBuffer("nstep"), this.maxstepSlider, 100);

        if (this.maxstepLabel != null) {
            item.setLabel(this.maxstepLabel);
        }

        if (this.maxstepButton != null) {
            item.setDefault(50, this.maxstepButton);
        }
    }

    private void setupVariableCellItem(QENamelist nmlControl) {
        if (this.vcToggle == null) {
            return;
        }

        QEFXToggleString item = new QEFXToggleString(
                nmlControl.getValueBuffer("calculation"), this.vcToggle, false, "vc-relax", "relax");

        if (this.vcLabel != null) {
            item.setLabel(this.vcLabel);
        }

        if (this.vcButton != null) {
            item.setDefault("relax", this.vcButton);
        }
    }

    private void setupIonsConvergenceItem(QENamelist nmlControl) {
        if (this.ionConvField == null) {
            return;
        }

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(
                nmlControl.getValueBuffer("forc_conv_thr"), this.ionConvField);

        if (this.ionConvLabel != null) {
            item.setLabel(this.ionConvLabel);
        }

        if (this.ionConvButton != null) {
            item.setDefault(1.0e-3, this.ionConvButton);
        }

        if (this.ionConvUnit != null) {
            item.setUnit(new QEFXUnit(this.ionConvUnit, QEFXUnit.UNIT_TYPE_FORCE_RY));
        }

        item.setLowerBound(0.0, QEFXTextFieldDouble.BOUND_TYPE_LESS_THAN);

        item.addWarningCondition((name, value) -> {
            if ("forc_conv_thr".equalsIgnoreCase(name)) {
                if (value != null && value.getRealValue() >= 1.0e-1) {
                    return WarningCondition.WARNING;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void setupIonsMethodItem(QENamelist nmlIons) {
        if (this.ionMethodCombo == null) {
            return;
        }

        this.ionMethodCombo.getItems().clear();
        QEFXComboString item = new QEFXComboString(nmlIons.getValueBuffer("ion_dynamics"), this.ionMethodCombo);

        if (this.ionMethodLabel != null) {
            item.setLabel(this.ionMethodLabel);
        }

        if (this.ionMethodButton != null) {
            item.setDefault("bfgs", this.ionMethodButton);
        }

        item.addItems(IONS_METHOD_TEXTS);

        if (IONS_METHOD_TEXTS.length != IONS_METHOD_VALUES.length) {
            throw new RuntimeException("IONS_METHOD_TEXTS.length != IONS_METHOD_VALUES.length");
        }

        Map<String, String> methodMap = new HashMap<String, String>();
        for (int i = 0; i < IONS_METHOD_TEXTS.length; i++) {
            methodMap.put(IONS_METHOD_TEXTS[i], IONS_METHOD_VALUES[i]);
        }

        item.setValueFactory(text -> {
            return methodMap.get(text);
        });
    }

    private void setupCellConvergenceItem(QENamelist nmlCell, QEValueBuffer calcValue) {
        if (this.cellConvField == null) {
            return;
        }

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(
                nmlCell.getValueBuffer("press_conv_thr"), this.cellConvField);

        if (this.cellConvLabel != null) {
            item.setLabel(this.cellConvLabel);
        }

        if (this.cellConvButton != null) {
            item.setDefault(0.5, this.cellConvButton);
        }

        if (this.cellConvUnit != null) {
            item.setUnit(new QEFXUnit(this.cellConvUnit, QEFXUnit.UNIT_TYPE_PRESSURE));
        }

        item.setLowerBound(0.0, QEFXTextFieldDouble.BOUND_TYPE_LESS_THAN);

        if (calcValue != null) {
            item.addEnablingTrigger(calcValue);
            item.addEnabledCondition((name, value) -> {
                if ("calculation".equalsIgnoreCase(name)) {
                    if (value != null && "vc-relax".equals(value.getCharacterValue())) {
                        return true;
                    } else {
                        return false;
                    }
                }

                return true;
            });

            item.pullAllTriggers();
        }
    }

    private void setupCellMethodItem(QENamelist nmlCell, QEValueBuffer calcValue, QEValueBuffer iondynValue) {
        if (this.cellMethodCombo == null) {
            return;
        }

        QEValueBuffer celldynValue = nmlCell.getValueBuffer("cell_dynamics");

        this.cellMethodCombo.getItems().clear();
        QEFXComboString item = new QEFXComboString(celldynValue, this.cellMethodCombo);

        if (this.cellMethodLabel != null) {
            item.setLabel(this.cellMethodLabel);
        }

        if (this.cellMethodButton != null) {
            if (iondynValue != null) {
                item.setDefault(() -> {
                    if ((!iondynValue.hasValue()) || "bfgs".equals(iondynValue.getCharacterValue())) {
                        return QEValueBase.getInstance("cell_dynamics", "bfgs");
                    } else {
                        return QEValueBase.getInstance("cell_dynamics", "damp-pr");
                    }
                }, this.cellMethodButton);

            } else {
                item.setDefault("bfgs", this.cellMethodButton);
            }
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
                    if (value != null && "vc-relax".equals(value.getCharacterValue())) {
                        return true;
                    } else {
                        return false;
                    }
                }

                return true;
            });

            item.pullAllTriggers();
        }

        if (calcValue != null && iondynValue != null) {
            item.addWarningTrigger(calcValue);
            item.addWarningTrigger(iondynValue);
            item.addWarningCondition((name, value) -> {
                if (calcValue.hasValue() && "vc-relax".equals(calcValue.getCharacterValue())) {
                    if ((!iondynValue.hasValue()) || "bfgs".equals(iondynValue.getCharacterValue())) {
                        if ((!celldynValue.hasValue()) || "bfgs".equals(celldynValue.getCharacterValue())) {
                            return WarningCondition.OK;
                        } else {
                            return WarningCondition.ERROR;
                        }
                    } else {
                        if ((!celldynValue.hasValue()) || "bfgs".equals(celldynValue.getCharacterValue())) {
                            return WarningCondition.ERROR;
                        } else {
                            return WarningCondition.OK;
                        }
                    }
                }

                return WarningCondition.OK;
            });

            item.pullAllTriggers();
        }
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
                    if (value != null && "vc-relax".equals(value.getCharacterValue())) {
                        return true;
                    } else {
                        return false;
                    }
                }

                return true;
            });

            item.pullAllTriggers();
        }
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
                    if (value != null && "vc-relax".equals(value.getCharacterValue())) {
                        return true;
                    } else {
                        return false;
                    }
                }

                return true;
            });

            item.pullAllTriggers();
        }
    }
}
