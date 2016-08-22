/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.dos;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import burai.app.QEFXMainController;
import burai.app.project.editor.input.QEFXInputController;
import burai.app.project.editor.input.items.QEFXComboInteger;
import burai.app.project.editor.input.items.QEFXComboString;
import burai.app.project.editor.input.items.QEFXTextFieldDouble;
import burai.app.project.editor.input.items.QEFXTextFieldInteger;
import burai.app.project.editor.input.items.QEFXUnit;
import burai.app.project.editor.input.items.WarningCondition;
import burai.app.project.editor.input.scf.QEFXKPoints;
import burai.input.QEInput;
import burai.input.card.QECard;
import burai.input.card.QEKPoints;
import burai.input.correcter.BandCorrector;
import burai.input.namelist.QENamelist;
import burai.input.namelist.QEValueBase;
import burai.input.namelist.QEValueBuffer;
import burai.input.namelist.tracer.QEDOSTracer;

public class QEFXDosController extends QEFXInputController {

    private static final String[] BROADENING_TEXTS = {
            "Tetrahedron", "Simple Gaussian", "Methfessel-Paxton", "Marzari-Vanderbilt", "Fermi-Dirac"
    };

    private static final int[] BROADENING_INDEXES = {
            QEDOSTracer.NGAUSS_TETRAHEDRON, 0, 1, -1, -99
    };

    private QEFXTextFieldInteger nbandItem;

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
    * nband
    */
    @FXML
    private Label nbandLabel;

    @FXML
    private TextField nbandField;

    @FXML
    private Button nbandButton;

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
     * smearing width
     */
    @FXML
    private Label smearWidthLabel;

    @FXML
    private TextField smearWidthField;

    @FXML
    private Button smearWidthButton;

    @FXML
    private ComboBox<String> smearWidthUnit;

    /*
     * Emax
     */
    @FXML
    private Label emaxLabel;

    @FXML
    private TextField emaxField;

    @FXML
    private Button emaxButton;

    @FXML
    private ComboBox<String> emaxUnit;

    /*
     * Emin
     */
    @FXML
    private Label eminLabel;

    @FXML
    private TextField eminField;

    @FXML
    private Button eminButton;

    @FXML
    private ComboBox<String> eminUnit;

    /*
     * DeltaE
     */
    @FXML
    private Label edeltaLabel;

    @FXML
    private TextField edeltaField;

    @FXML
    private Button edeltaButton;

    @FXML
    private ComboBox<String> edeltaUnit;

    /*
     * broadening
     */
    @FXML
    private Label broadLabel;

    @FXML
    private ComboBox<String> broadCombo;

    @FXML
    private Button broadButton;

    /*
     * broadening width
     */
    @FXML
    private Label broadWidthLabel;

    @FXML
    private TextField broadWidthField;

    @FXML
    private Button broadWidthButton;

    @FXML
    private ComboBox<String> broadWidthUnit;

    public QEFXDosController(QEFXMainController mainController, QEInput input) {
        super(mainController, input);
        this.nbandItem = null;
    }

    public void updateNBandStatus() {
        if (this.nbandItem != null) {
            this.nbandItem.pullAllTriggers();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        QECard card = this.input.getCard(QEKPoints.CARD_NAME);
        QENamelist nmlSystem = this.input.getNamelist(QEInput.NAMELIST_SYSTEM);
        QENamelist nmlDos = this.input.getNamelist(QEInput.NAMELIST_DOS);

        if (card != null && card instanceof QEKPoints) {
            this.setupKPointItem((QEKPoints) card);
        }

        if (nmlSystem != null) {
            this.setupNBandItem(nmlSystem);
            if (card != null && card instanceof QEKPoints) {
                this.setupOccupationItem(nmlSystem, (QEKPoints) card);
            }
            this.setupSmearingItem(nmlSystem);
            this.setupSmearWidthItem(nmlSystem);
        }

        if (nmlDos != null) {
            this.setupEmaxItem(nmlDos);
            this.setupEminItem(nmlDos);
            this.setupDeltaEItem(nmlDos);
            if (nmlSystem != null) {
                this.setupBroadeningItem(nmlDos, nmlSystem);
                this.setupBroadeningWidthItem(nmlDos, nmlSystem);
            }
        }
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
            cardKPoints.setAccurateCondition(this.input);
            this.kpointField1.requestFocus();
        });
    }

    private void setupNBandItem(QENamelist nmlSystem) {
        if (this.nbandField == null) {
            return;
        }

        BandCorrector corrector = new BandCorrector(this.input);

        QEFXTextFieldInteger item = new QEFXTextFieldInteger(nmlSystem.getValueBuffer("nbnd"), this.nbandField);

        if (this.nbandLabel != null) {
            item.setLabel(this.nbandLabel);
        }

        if (this.nbandButton != null) {
            item.setDefault(() -> {
                int nband = corrector.isAvailable() ? corrector.getNumBands() : 0;
                if (nband > 0) {
                    return QEValueBase.getInstance("nbnd", nband);
                } else {
                    return null;
                }

            }, this.nbandButton);
        }

        item.setLowerBound(0, QEFXTextFieldInteger.BOUND_TYPE_LESS_THAN);

        item.addWarningCondition((name, value) -> {
            if ("nbnd".equalsIgnoreCase(name)) {
                if (value == null) {
                    return WarningCondition.WARNING;
                }

                int nband = corrector.isAvailable() ? corrector.getNumBands() : 0;
                if (nband > 0) {
                    if (nband != value.getIntegerValue()) {
                        return WarningCondition.WARNING;
                    } else {
                        return WarningCondition.OK;
                    }
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();

        this.nbandItem = item;
    }

    private void setupOccupationItem(QENamelist nmlSystem, QEKPoints cardKPoints) {
        if (this.occupCombo == null) {
            return;
        }

        this.occupCombo.getItems().clear();
        QEFXComboString item = new QEFXComboString(nmlSystem.getValueBuffer("occupations"), this.occupCombo);

        if (this.occupLabel != null) {
            item.setLabel(this.occupLabel);
        }

        if (this.occupButton != null) {
            item.setDefault(() -> {
                if (cardKPoints.isAutomatic()) {
                    return QEValueBase.getInstance("occupations", "tetrahedra");
                } else {
                    return QEValueBase.getInstance("occupations", "smearing");
                }
            }, this.occupButton);
        }

        item.addItems("smearing", "tetrahedra", "fixed");

        item.addWarningCondition((name, value) -> {
            if ("occupations".equalsIgnoreCase(name)) {
                if (value == null) {
                    return WarningCondition.ERROR;
                } else if ("tetrahedra".equals(value.getCharacterValue())) {
                    if (!cardKPoints.isAutomatic()) {
                        return WarningCondition.ERROR;
                    } else {
                        return WarningCondition.OK;
                    }
                } else {
                    return WarningCondition.OK;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();

        cardKPoints.addListener(event -> item.pullAllTriggers());
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

    private void setupSmearWidthItem(QENamelist nmlSystem) {
        if (this.smearWidthField == null) {
            return;
        }

        QEValueBuffer occupValue = nmlSystem.getValueBuffer("occupations");
        QEValueBuffer gaussValue = nmlSystem.getValueBuffer("degauss");

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(gaussValue, this.smearWidthField);

        if (this.smearWidthLabel != null) {
            item.setLabel(this.smearWidthLabel);
        }

        if (this.smearWidthButton != null) {
            item.setDefault(0.01, this.smearWidthButton); // 0.01Ry
        }

        if (this.smearWidthUnit != null) {
            item.setUnit(new QEFXUnit(this.smearWidthUnit, QEFXUnit.UNIT_TYPE_ENERGY_RY));
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

    private void setupEmaxItem(QENamelist nmlDos) {
        if (this.emaxField == null) {
            return;
        }

        QEValueBuffer emaxValue = nmlDos.getValueBuffer("emax");
        QEValueBuffer eminValue = nmlDos.getValueBuffer("emin");

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(emaxValue, this.emaxField);

        if (this.emaxLabel != null) {
            item.setLabel(this.emaxLabel);
        }

        if (this.emaxButton != null) {
            item.setDefault(50.0, this.emaxButton); // 50eV
        }

        if (this.emaxUnit != null) {
            item.setUnit(new QEFXUnit(this.emaxUnit, QEFXUnit.UNIT_TYPE_ENERGY_EV));
        }

        item.addWarningTrigger(eminValue);
        item.addWarningCondition((name, value) -> {
            if ("emax".equalsIgnoreCase(name) || "emin".equalsIgnoreCase(name)) {
                double ediff = -1.0;
                if (emaxValue.hasValue() && eminValue.hasValue()) {
                    ediff = emaxValue.getRealValue() - eminValue.getRealValue();
                }

                if (ediff <= 0.0) {
                    return WarningCondition.ERROR;
                } else if (ediff <= 2.0) { // 2.0eV
                    return WarningCondition.WARNING;
                } else {
                    return WarningCondition.OK;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void setupEminItem(QENamelist nmlDos) {
        if (this.eminField == null) {
            return;
        }

        QEValueBuffer emaxValue = nmlDos.getValueBuffer("emax");
        QEValueBuffer eminValue = nmlDos.getValueBuffer("emin");

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(eminValue, this.eminField);

        if (this.eminLabel != null) {
            item.setLabel(this.eminLabel);
        }

        if (this.eminButton != null) {
            item.setDefault(-50.0, this.eminButton); // -50eV
        }

        if (this.eminUnit != null) {
            item.setUnit(new QEFXUnit(this.eminUnit, QEFXUnit.UNIT_TYPE_ENERGY_EV));
        }

        item.addWarningTrigger(emaxValue);
        item.addWarningCondition((name, value) -> {
            if ("emax".equalsIgnoreCase(name) || "emin".equalsIgnoreCase(name)) {
                double ediff = -1.0;
                if (emaxValue.hasValue() && eminValue.hasValue()) {
                    ediff = emaxValue.getRealValue() - eminValue.getRealValue();
                }

                if (ediff <= 0.0) {
                    return WarningCondition.ERROR;
                } else if (ediff <= 2.0) { // 2.0eV
                    return WarningCondition.WARNING;
                } else {
                    return WarningCondition.OK;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void setupDeltaEItem(QENamelist nmlDos) {
        if (this.edeltaField == null) {
            return;
        }

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(nmlDos.getValueBuffer("deltae"), this.edeltaField);

        if (this.edeltaLabel != null) {
            item.setLabel(this.edeltaLabel);
        }

        if (this.edeltaButton != null) {
            item.setDefault(0.01, this.edeltaButton); // 0.01eV
        }

        if (this.edeltaUnit != null) {
            item.setUnit(new QEFXUnit(this.edeltaUnit, QEFXUnit.UNIT_TYPE_ENERGY_EV));
        }

        item.setLowerBound(0.0, QEFXTextFieldDouble.BOUND_TYPE_LESS_EQUAL);

        item.addWarningCondition((name, value) -> {
            if ("deltae".equalsIgnoreCase(name)) {
                double deltaE = -1.0;
                if (value != null) {
                    deltaE = value.getRealValue();
                }

                if (deltaE <= 0.0) {
                    return WarningCondition.ERROR;
                } else if (deltaE >= 0.5) { // 0.5eV
                    return WarningCondition.WARNING;
                } else {
                    return WarningCondition.OK;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void setupBroadeningItem(QENamelist nmlDos, QENamelist nmlSystem) {
        if (this.broadCombo == null) {
            return;
        }

        QEValueBuffer ngausValue = nmlDos.getValueBuffer("!ngauss");
        QEValueBuffer occupValue = nmlSystem.getValueBuffer("occupations");

        this.broadCombo.getItems().clear();
        QEFXComboInteger item = new QEFXComboInteger(ngausValue, this.broadCombo);

        if (this.broadLabel != null) {
            item.setLabel(this.broadLabel);
        }

        if (this.broadButton != null) {
            item.setDefault(() -> {
                if (occupValue.hasValue() && "tetrahedra".equals(occupValue.getCharacterValue())) {
                    return QEValueBase.getInstance("!ngauss", QEDOSTracer.NGAUSS_TETRAHEDRON);
                } else {
                    return QEValueBase.getInstance("!ngauss", 0);
                }

            }, this.broadButton);
        }

        item.addItems(BROADENING_TEXTS);

        if (BROADENING_TEXTS.length != BROADENING_INDEXES.length) {
            throw new RuntimeException("BROADENING_TEXTS.length != BROADENING_INDEXES.length");
        }

        Map<String, Integer> broadMap = new HashMap<String, Integer>();
        for (int i = 0; i < BROADENING_TEXTS.length; i++) {
            broadMap.put(BROADENING_TEXTS[i], BROADENING_INDEXES[i]);
        }

        item.setValueFactory(text -> {
            return broadMap.get(text);
        });

        item.addWarningTrigger(occupValue);
        item.addWarningCondition((name, value) -> {
            if ("!ngauss".equalsIgnoreCase(name) || "occupations".equalsIgnoreCase(name)) {
                if (occupValue.hasValue() && "tetrahedra".equals(occupValue.getCharacterValue())) {
                    return WarningCondition.OK;
                }

                if (ngausValue.hasValue() && ngausValue.getIntegerValue() == QEDOSTracer.NGAUSS_TETRAHEDRON) {
                    return WarningCondition.ERROR;
                } else {
                    return WarningCondition.OK;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void setupBroadeningWidthItem(QENamelist nmlDos, QENamelist nmlSystem) {
        if (this.broadWidthField == null) {
            return;
        }

        QEValueBuffer ngausValue = nmlDos.getValueBuffer("!ngauss");
        QEValueBuffer gaussValue = nmlDos.getValueBuffer("degauss");
        QEValueBuffer gaSysValue = nmlSystem.getValueBuffer("degauss");

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(gaussValue, this.broadWidthField);

        if (this.broadWidthLabel != null) {
            item.setLabel(this.broadWidthLabel);
        }

        if (this.broadWidthButton != null) {
            item.setDefault(() -> {
                if (ngausValue.hasValue() && ngausValue.getIntegerValue() == QEDOSTracer.NGAUSS_TETRAHEDRON) {
                    return null;
                } else if (gaSysValue.hasValue()) {
                    return gaSysValue.getValue();
                } else {
                    return QEValueBase.getInstance("degauss", 0.01); // 0.01Ry
                }

            }, this.broadWidthButton);
        }

        if (this.broadWidthUnit != null) {
            item.setUnit(new QEFXUnit(this.broadWidthUnit, QEFXUnit.UNIT_TYPE_ENERGY_RY));
        }

        item.setLowerBound(0.0, QEFXTextFieldDouble.BOUND_TYPE_LESS_THAN);

        item.addEnablingTrigger(ngausValue);
        item.addEnabledCondition((name, value) -> {
            if ("!ngauss".equalsIgnoreCase(name)) {
                if (value != null && value.getIntegerValue() == QEDOSTracer.NGAUSS_TETRAHEDRON) {
                    return false;
                } else {
                    return true;
                }
            }

            return true;
        });

        item.addWarningTrigger(ngausValue);
        item.addWarningCondition((name, value) -> {
            if ("degauss".equalsIgnoreCase(name) || "!ngauss".equalsIgnoreCase(name)) {
                if (ngausValue.hasValue() && ngausValue.getIntegerValue() == QEDOSTracer.NGAUSS_TETRAHEDRON) {
                    if (gaussValue.hasValue()) {
                        return WarningCondition.ERROR;
                    } else {
                        return WarningCondition.OK;
                    }

                } else {
                    if (!gaussValue.hasValue()) {
                        return WarningCondition.ERROR;
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
}
