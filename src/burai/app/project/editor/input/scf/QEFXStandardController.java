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
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import burai.app.QEFXMainController;
import burai.app.project.editor.input.QEFXInputController;
import burai.app.project.editor.input.items.QEFXComboString;
import burai.app.project.editor.input.items.QEFXTextFieldDouble;
import burai.app.project.editor.input.items.QEFXToggleBoolean;
import burai.app.project.editor.input.items.QEFXToggleString;
import burai.app.project.editor.input.items.QEFXUnit;
import burai.app.project.editor.input.items.WarningCondition;
import burai.input.QEInput;
import burai.input.card.QECard;
import burai.input.card.QEKPoints;
import burai.input.correcter.CutoffCorrector;
import burai.input.namelist.QENamelist;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBase;
import burai.input.namelist.QEValueBuffer;

public class QEFXStandardController extends QEFXInputController {

    private static final double DELTA_ECUT = 1.0e-2;
    private static final double DEFAULT_ECUTWFC = 25.0; // 25Ry
    private static final double DEFAULT_ECUTRHO = 225.0; // 225Ry

    private QEFXTextFieldDouble ecutwfcItem;
    private QEFXTextFieldDouble ecutrhoItem;

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
     * calc force
     */
    @FXML
    private Label forceLabel;

    @FXML
    private ToggleButton forceToggle;

    @FXML
    private Button forceButton;

    /*
     * calc stress
     */
    @FXML
    private Label stressLabel;

    @FXML
    private ToggleButton stressToggle;

    @FXML
    private Button stressButton;

    /*
     * ecutwfc
     */
    @FXML
    private Label ecutwfcLabel;

    @FXML
    private TextField ecutwfcField;

    @FXML
    private Button ecutwfcButton;

    @FXML
    private ComboBox<String> ecutwfcUnit;

    /*
     * ecutrho
     */
    @FXML
    private Label ecutrhoLabel;

    @FXML
    private TextField ecutrhoField;

    @FXML
    private Button ecutrhoButton;

    @FXML
    private ComboBox<String> ecutrhoUnit;

    /*
     * total charge
     */
    @FXML
    private Label totchargeLabel;

    @FXML
    private TextField totchargeField;

    @FXML
    private Button totchargeButton;

    /*
     * symmetry
     */
    @FXML
    private Label symmLabel;

    @FXML
    private ToggleButton symmToggle;

    @FXML
    private Button symmButton;

    /*
     * k-point
     */
    @FXML
    private Label kpointLabel;

    @FXML
    private TextField kpointField1;

    @FXML
    private TextField kpointField2;

    @FXML
    private TextField kpointField3;

    @FXML
    private Button kpointButton;

    /*
     * occupation
     */
    @FXML
    private Label occupLabel;

    @FXML
    private ComboBox<String> occupCombo;

    @FXML
    private Button occupButton;

    /*
     * smearing
     */
    @FXML
    private Label smearLabel;

    @FXML
    private ComboBox<String> smearCombo;

    @FXML
    private Button smearButton;

    /*
     * gaussian (for smearing)
     */
    @FXML
    private Label gaussLabel;

    @FXML
    private TextField gaussField;

    @FXML
    private Button gaussButton;

    @FXML
    private ComboBox<String> gaussUnit;

    public QEFXStandardController(QEFXMainController mainController, QEInput input) {
        super(mainController, input);
        this.ecutwfcItem = null;
        this.ecutrhoItem = null;
    }

    public void updateEcutStatus() {
        if (this.ecutwfcItem != null) {
            this.ecutwfcItem.pullAllTriggers();
        }

        if (this.ecutrhoItem != null) {
            this.ecutrhoItem.pullAllTriggers();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        QENamelist nmlControl = this.input.getNamelist(QEInput.NAMELIST_CONTROL);

        if (nmlControl != null) {
            this.setupRestartItem(nmlControl);
            this.setupMaxTimeItem(nmlControl);
            this.setupCalcForceItem(nmlControl);
            this.setupCalcStressItem(nmlControl);
        }

        QENamelist nmlSystem = this.input.getNamelist(QEInput.NAMELIST_SYSTEM);

        if (nmlSystem != null) {
            this.setupEcutwfcItem(nmlSystem);
            this.setupEcutrhoItem(nmlSystem);
            this.setupTotChargeItem(nmlSystem);
            this.setupSymmetryItem(nmlSystem);
            this.setupOccupationItem(nmlSystem);
            this.setupSmearingItem(nmlSystem);
            this.setupGaussianItem(nmlSystem);
        }

        QECard card = this.input.getCard(QEKPoints.CARD_NAME);

        if (card != null && card instanceof QEKPoints) {
            this.setupKPointItem((QEKPoints) card);
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

    private void setupCalcForceItem(QENamelist nmlControl) {
        if (this.forceToggle == null) {
            return;
        }

        QEFXToggleBoolean item = new QEFXToggleBoolean(nmlControl.getValueBuffer("tprnfor"), this.forceToggle, false);

        if (this.forceLabel != null) {
            item.setLabel(this.forceLabel);
        }

        if (this.forceButton != null) {
            item.setDefault(false, this.forceButton);
        }
    }

    private void setupCalcStressItem(QENamelist nmlControl) {
        if (this.stressToggle == null) {
            return;
        }

        QEFXToggleBoolean item = new QEFXToggleBoolean(nmlControl.getValueBuffer("tstress"), this.stressToggle, false);

        if (this.stressLabel != null) {
            item.setLabel(this.stressLabel);
        }

        if (this.stressButton != null) {
            item.setDefault(false, this.stressButton);
        }
    }

    private void setupEcutwfcItem(QENamelist nmlSystem) {
        if (this.ecutwfcField == null) {
            return;
        }

        CutoffCorrector corrector = new CutoffCorrector(this.input);

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(nmlSystem.getValueBuffer("ecutwfc"), this.ecutwfcField);

        if (this.ecutwfcLabel != null) {
            item.setLabel(this.ecutwfcLabel);
        }

        if (this.ecutwfcButton != null) {
            item.setDefault(() -> {
                double ecut = corrector.isAvailable() ? corrector.getCutoffOfWF() : DEFAULT_ECUTWFC;
                return QEValueBase.getInstance("ecutwfc", ecut);
            }, this.ecutwfcButton);
        }

        if (this.ecutwfcUnit != null) {
            item.setUnit(new QEFXUnit(this.ecutwfcUnit, QEFXUnit.UNIT_TYPE_ENERGY_RY));
        }

        item.setLowerBound(0.0, QEFXTextFieldDouble.BOUND_TYPE_LESS_THAN);

        item.addWarningCondition((name, value) -> {
            if ("ecutwfc".equalsIgnoreCase(name)) {
                if (value == null) {
                    return WarningCondition.ERROR;
                }

                double ecut = corrector.isAvailable() ? corrector.getCutoffOfWF() : DEFAULT_ECUTWFC;
                if (value.getRealValue() < (ecut - DELTA_ECUT)) {
                    return WarningCondition.WARNING;
                } else {
                    return WarningCondition.OK;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();

        this.ecutwfcItem = item;
    }

    private void setupEcutrhoItem(QENamelist nmlSystem) {
        if (this.ecutrhoField == null) {
            return;
        }

        CutoffCorrector corrector = new CutoffCorrector(this.input);

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(nmlSystem.getValueBuffer("ecutrho"), this.ecutrhoField);

        if (this.ecutrhoLabel != null) {
            item.setLabel(this.ecutrhoLabel);
        }

        if (this.ecutrhoButton != null) {
            item.setDefault(() -> {
                double ecut = corrector.isAvailable() ? corrector.getCutoffOfCharge() : DEFAULT_ECUTWFC;
                return QEValueBase.getInstance("ecutrho", ecut);
            }, this.ecutrhoButton);
        }

        if (this.ecutrhoUnit != null) {
            item.setUnit(new QEFXUnit(this.ecutrhoUnit, QEFXUnit.UNIT_TYPE_ENERGY_RY));
        }

        QEValueBuffer ecutwfcValue = nmlSystem.getValueBuffer("ecutwfc");

        ecutwfcValue.addListener(value -> {
            if ((!ecutwfcValue.hasValue()) || ecutwfcValue.getRealValue() <= 0.0) {
                item.setLowerBound(0.0, QEFXTextFieldDouble.BOUND_TYPE_LESS_THAN);
            } else {
                item.setLowerBound(4.0 * ecutwfcValue.getRealValue(), QEFXTextFieldDouble.BOUND_TYPE_LESS_EQUAL);
            }
        });

        ecutwfcValue.runAllListeners();

        item.addWarningCondition((name, value) -> {
            if ("ecutrho".equalsIgnoreCase(name)) {
                if (value == null) {
                    return WarningCondition.ERROR;
                }

                double ecut = corrector.isAvailable() ? corrector.getCutoffOfCharge() : DEFAULT_ECUTRHO;
                if (value.getRealValue() < (ecut - DELTA_ECUT)) {
                    return WarningCondition.WARNING;
                } else {
                    return WarningCondition.OK;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();

        this.ecutrhoItem = item;
    }

    private void setupTotChargeItem(QENamelist nmlSystem) {
        if (this.totchargeField == null) {
            return;
        }

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(nmlSystem.getValueBuffer("tot_charge"), this.totchargeField);

        if (this.totchargeLabel != null) {
            item.setLabel(this.totchargeLabel);
        }

        if (this.totchargeButton != null) {
            item.setDefault((QEValue) null, this.totchargeButton);
        }

        item.addWarningCondition((name, value) -> {
            if ("tot_charge".equalsIgnoreCase(name)) {
                if (value != null && Math.abs(value.getRealValue()) >= 10.0) {
                    return WarningCondition.WARNING;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void setupSymmetryItem(QENamelist nmlSystem) {
        if (this.symmToggle == null) {
            return;
        }

        QEFXToggleBoolean item = new QEFXToggleBoolean(nmlSystem.getValueBuffer("nosym"), this.symmToggle, true, true);

        if (this.symmLabel != null) {
            item.setLabel(this.symmLabel);
        }

        if (this.symmButton != null) {
            item.setDefault(false, this.symmButton);
        }
    }

    private void setupOccupationItem(QENamelist nmlSystem) {
        if (this.occupCombo == null) {
            return;
        }

        this.occupCombo.getItems().clear();
        QEFXComboString item = new QEFXComboString(nmlSystem.getValueBuffer("occupations"), this.occupCombo);

        if (this.occupLabel != null) {
            item.setLabel(this.occupLabel);
        }

        if (this.occupButton != null) {
            item.setDefault("smearing", this.occupButton);
        }

        item.addItems("smearing", "fixed");

        item.addWarningCondition((name, value) -> {
            if ("occupations".equalsIgnoreCase(name)) {
                if (value == null) {
                    return WarningCondition.ERROR;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void setupSmearingItem(QENamelist nmlSystem) {
        if (this.smearCombo == null) {
            return;
        }

        this.smearCombo.getItems().clear();
        QEFXComboString item = new QEFXComboString(nmlSystem.getValueBuffer("smearing"), this.smearCombo);

        if (this.smearLabel != null) {
            item.setLabel(this.smearLabel);
        }

        if (this.smearButton != null) {
            item.setDefault("gaussian", this.smearButton);
        }

        item.addItems("gaussian", "methfessel-paxton", "marzari-vanderbilt", "fermi-dirac");

        item.addEnablingTrigger(nmlSystem.getValueBuffer("occupations"));
        item.addEnabledCondition((name, value) -> {
            if ("occupations".equalsIgnoreCase(name)) {
                if (value != null && "smearing".equals(value.getCharacterValue())) {
                    return true;
                } else {
                    return false;
                }
            }

            return true;
        });

        item.pullAllTriggers();
    }

    private void setupGaussianItem(QENamelist nmlSystem) {
        if (this.gaussField == null) {
            return;
        }

        QEValueBuffer occupValue = nmlSystem.getValueBuffer("occupations");
        QEValueBuffer gaussValue = nmlSystem.getValueBuffer("degauss");

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(gaussValue, this.gaussField);

        if (this.gaussLabel != null) {
            item.setLabel(this.gaussLabel);
        }

        if (this.gaussButton != null) {
            item.setDefault(0.01, this.gaussButton); // 0.01Ry
        }

        if (this.gaussUnit != null) {
            item.setUnit(new QEFXUnit(this.gaussUnit, QEFXUnit.UNIT_TYPE_ENERGY_RY));
        }

        item.setLowerBound(0.0, QEFXTextFieldDouble.BOUND_TYPE_LESS_EQUAL);

        item.addEnablingTrigger(occupValue);
        item.addEnabledCondition((name, value) -> {
            if ("occupations".equalsIgnoreCase(name)) {
                if (value != null && "smearing".equals(value.getCharacterValue())) {
                    return true;
                } else {
                    return false;
                }
            }

            return true;
        });

        item.addWarningTrigger(occupValue);
        item.addWarningCondition((name, value) -> {
            if ("occupations".equalsIgnoreCase(name) || "degauss".equalsIgnoreCase(name)) {
                if (!item.isDisable()) {
                    if ((!gaussValue.hasValue()) || gaussValue.getRealValue() == 0.0) {
                        return WarningCondition.WARNING;
                    } else if (gaussValue.getRealValue() > 0.05) { // 0.05Ry
                        return WarningCondition.WARNING;
                    } else {
                        return WarningCondition.OK;
                    }
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void setupKPointItem(QEKPoints cardKPoints) {
        if (this.kpointField1 == null || this.kpointField2 == null || this.kpointField3 == null) {
            return;
        }

        if (this.kpointLabel == null || this.kpointButton == null) {
            return;
        }

        TextField[] kpointFields = { this.kpointField1, this.kpointField2, this.kpointField3 };

        QEFXKPoints kPoints = new QEFXKPoints(cardKPoints, kpointFields, this.kpointLabel, this.kpointButton);

        kPoints.setDefault(event -> {
            cardKPoints.setRecommendedCondition(this.input);
            this.kpointField1.requestFocus();
        });
    }
}
