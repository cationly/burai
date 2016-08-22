/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.scf;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import burai.app.QEFXMainController;
import burai.app.project.editor.input.QEFXInputController;
import burai.app.project.editor.input.items.QEFXComboInteger;
import burai.app.project.editor.input.items.QEFXComboString;
import burai.app.project.editor.input.items.QEFXSliderDouble;
import burai.app.project.editor.input.items.QEFXSliderInteger;
import burai.app.project.editor.input.items.QEFXTextFieldDouble;
import burai.app.project.editor.input.items.QEFXUnit;
import burai.app.project.editor.input.items.WarningCondition;
import burai.input.QEInput;
import burai.input.namelist.QENamelist;
import burai.input.namelist.QEValueBase;
import burai.input.namelist.QEValueBuffer;

public class QEFXElectronController extends QEFXInputController {

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
     * convergence
     */
    @FXML
    private Label convLabel;

    @FXML
    private TextField convField;

    @FXML
    private Button convButton;

    @FXML
    private ComboBox<String> convUnit;

    /*
     * startingwfc
     */
    @FXML
    private Label startwfcLabel;

    @FXML
    private ComboBox<String> startwfcCombo;

    @FXML
    private Button startwfcButton;

    /*
     * diagonalization
     */
    @FXML
    private Label diagLabel;

    @FXML
    private ComboBox<String> diagCombo;

    @FXML
    private Button diagButton;

    /*
     * number of Davidson
     */
    @FXML
    private Label numdavidLabel;

    @FXML
    private ComboBox<String> numdavidCombo;

    @FXML
    private Button numdavidButton;

    /*
     * startingpot
     */
    @FXML
    private Label startpotLabel;

    @FXML
    private ComboBox<String> startpotCombo;

    @FXML
    private Button startpotButton;

    /*
     * mixing mode
     */
    @FXML
    private Label mixingLabel;

    @FXML
    private ComboBox<String> mixingCombo;

    @FXML
    private Button mixingButton;

    /*
     * mixing beta
     */
    @FXML
    private Label betaLabel;

    @FXML
    private Slider betaSlider;

    @FXML
    private Button betaButton;

    /*
     * number of mixing
     */
    @FXML
    private Label nummixLabel;

    @FXML
    private ComboBox<String> nummixCombo;

    @FXML
    private Button nummixButton;

    public QEFXElectronController(QEFXMainController mainController, QEInput input) {
        super(mainController, input);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        QENamelist nmlElectrons = this.input.getNamelist(QEInput.NAMELIST_ELECTRONS);

        if (nmlElectrons != null) {
            this.setupMaxStepItem(nmlElectrons);
            this.setupConvergenceItem(nmlElectrons);
            this.setupStartingwfcItem(nmlElectrons);
            this.setupDiagonalizeItem(nmlElectrons);
            this.setupNumDavidsonItem(nmlElectrons);
            this.setupStartingpotItem(nmlElectrons);
            this.setupMixingModeItem(nmlElectrons);
            this.setupMixingBetaItem(nmlElectrons);
            this.setupNumMixingItem(nmlElectrons);
        }
    }

    private void setupMaxStepItem(QENamelist nmlElectrons) {
        if (this.maxstepSlider == null) {
            return;
        }

        QEFXSliderInteger item = new QEFXSliderInteger(
                nmlElectrons.getValueBuffer("electron_maxstep"), this.maxstepSlider, 100);

        if (this.maxstepLabel != null) {
            item.setLabel(this.maxstepLabel);
        }

        if (this.maxstepButton != null) {
            item.setDefault(100, this.maxstepButton);
        }
    }

    private void setupConvergenceItem(QENamelist nmlElectrons) {
        if (this.convField == null) {
            return;
        }

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(nmlElectrons.getValueBuffer("conv_thr"), this.convField);

        if (this.convLabel != null) {
            item.setLabel(this.convLabel);
        }

        if (this.convButton != null) {
            item.setDefault(1.0e-6, this.convButton);
        }

        if (this.convUnit != null) {
            item.setUnit(new QEFXUnit(this.convUnit, QEFXUnit.UNIT_TYPE_ENERGY_RY));
        }

        item.setLowerBound(0.0, QEFXTextFieldDouble.BOUND_TYPE_LESS_THAN);

        item.addWarningCondition((name, value) -> {
            if ("conv_thr".equalsIgnoreCase(name)) {
                if (value != null && value.getRealValue() >= 1.0e-3) {
                    return WarningCondition.WARNING;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void setupStartingwfcItem(QENamelist nmlElectrons) {
        if (this.startwfcCombo == null) {
            return;
        }

        this.startwfcCombo.getItems().clear();
        QEFXComboString item = new QEFXComboString(nmlElectrons.getValueBuffer("startingwfc"), this.startwfcCombo);

        if (this.startwfcLabel != null) {
            item.setLabel(this.startwfcLabel);
        }

        if (this.startwfcButton != null) {
            item.setDefault("atomic+random", this.startwfcButton);
        }

        item.addItems("atomic", "atomic+random", "random", "file");
    }

    private void setupDiagonalizeItem(QENamelist nmlElectrons) {
        if (this.diagCombo == null) {
            return;
        }

        this.diagCombo.getItems().clear();
        QEFXComboString item = new QEFXComboString(nmlElectrons.getValueBuffer("diagonalization"), this.diagCombo);

        if (this.diagLabel != null) {
            item.setLabel(this.diagLabel);
        }

        if (this.diagButton != null) {
            item.setDefault("david", this.diagButton);
        }

        item.addItems("david", "cg");
    }

    private void setupNumDavidsonItem(QENamelist nmlElectrons) {
        if (this.numdavidCombo == null) {
            return;
        }

        this.numdavidCombo.getItems().clear();
        QEFXComboInteger item = new QEFXComboInteger(
                nmlElectrons.getValueBuffer("diago_david_ndim"), this.numdavidCombo);

        if (this.numdavidLabel != null) {
            item.setLabel(this.numdavidLabel);
        }

        if (this.numdavidButton != null) {
            item.setDefault(4, this.numdavidButton);
        }

        item.addItems("2", "3", "4", "5", "6", "7", "8");

        item.setValueFactory(text -> {
            return Integer.parseInt(text);
        });

        item.addEnablingTrigger(nmlElectrons.getValueBuffer("diagonalization"));
        item.addEnabledCondition((name, value) -> {
            if ("diagonalization".equalsIgnoreCase(name)) {
                if (value != null && "david".equals(value.getCharacterValue())) {
                    return true;
                } else {
                    return false;
                }
            }

            return true;
        });

        item.pullAllTriggers();
    }

    private void setupStartingpotItem(QENamelist nmlElectrons) {
        if (this.startpotCombo == null) {
            return;
        }

        this.startpotCombo.getItems().clear();
        QEFXComboString item = new QEFXComboString(nmlElectrons.getValueBuffer("startingpot"), this.startpotCombo);

        if (this.startpotLabel != null) {
            item.setLabel(this.startpotLabel);
        }

        if (this.startpotButton != null) {
            item.setDefault("atomic", this.startpotButton);
        }

        item.addItems("atomic", "file");
    }

    private void setupMixingModeItem(QENamelist nmlElectrons) {
        if (this.mixingCombo == null) {
            return;
        }

        this.mixingCombo.getItems().clear();
        QEFXComboString item = new QEFXComboString(nmlElectrons.getValueBuffer("mixing_mode"), this.mixingCombo);

        if (this.mixingLabel != null) {
            item.setLabel(this.mixingLabel);
        }

        if (this.mixingButton != null) {
            item.setDefault("plain", this.mixingButton);
        }

        item.addItems("plain", "TF", "local-TF");
    }

    private void setupMixingBetaItem(QENamelist nmlElectrons) {
        if (this.betaSlider == null) {
            return;
        }

        double defaultBeta = 0.7;
        if (this.isSpinPolarization()) {
            defaultBeta = 0.4;
        }

        QEFXSliderDouble item = new QEFXSliderDouble(
                nmlElectrons.getValueBuffer("mixing_beta"), this.betaSlider, defaultBeta);

        if (this.betaLabel != null) {
            item.setLabel(this.betaLabel);
        }

        if (this.betaButton != null) {
            item.setDefault(() -> {
                if (this.isSpinPolarization()) {
                    return QEValueBase.getInstance("mixing_beta", 0.4);
                } else {
                    return QEValueBase.getInstance("mixing_beta", 0.7);
                }
            }, this.betaButton);
        }
    }

    private boolean isSpinPolarization() {
        QENamelist nmlSystem = this.input.getNamelist(QEInput.NAMELIST_SYSTEM);

        QEValueBuffer nspinValue = null;
        if (nmlSystem != null) {
            nspinValue = nmlSystem.getValueBuffer("nspin");
        }
        if (nspinValue != null && nspinValue.hasValue() && nspinValue.getIntegerValue() >= 2) {
            return true;
        }

        QEValueBuffer noncolinValue = null;
        if (nmlSystem != null) {
            noncolinValue = nmlSystem.getValueBuffer("noncolin");
        }
        if (noncolinValue != null && noncolinValue.hasValue() && noncolinValue.getLogicalValue()) {
            return true;
        }

        return false;
    }

    private void setupNumMixingItem(QENamelist nmlElectrons) {
        if (this.nummixCombo == null) {
            return;
        }

        this.nummixCombo.getItems().clear();
        QEFXComboInteger item = new QEFXComboInteger(
                nmlElectrons.getValueBuffer("mixing_ndim"), this.nummixCombo);

        if (this.nummixLabel != null) {
            item.setLabel(this.nummixLabel);
        }

        if (this.nummixButton != null) {
            item.setDefault(8, this.nummixButton);
        }

        item.addItems("2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16");

        item.setValueFactory(text -> {
            return Integer.parseInt(text);
        });
    }
}
