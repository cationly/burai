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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import burai.app.QEFXMainController;
import burai.app.project.editor.input.QEFXInputController;
import burai.app.project.editor.input.items.QEFXComboInteger;
import burai.app.project.editor.input.items.QEFXTextFieldDouble;
import burai.app.project.editor.input.items.QEFXToggleBoolean;
import burai.app.project.editor.input.items.WarningCondition;
import burai.atoms.element.ElementUtil;
import burai.input.QEInput;
import burai.input.card.QEAtomicSpecies;
import burai.input.card.QECard;
import burai.input.namelist.QENamelist;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBase;
import burai.input.namelist.QEValueBuffer;

public class QEFXMagnetizController extends QEFXInputController {

    private static final String TEXT_NON_POLARIZED = "Non-Polarized";
    private static final String TEXT_COLLINEAR = "Collinear";
    private static final String TEXT_NON_COLLINEAR = "Non-Collinear";

    /*
     * spin polarization
     */
    @FXML
    private Label polarizLabel;

    @FXML
    private ComboBox<String> polarizCombo;

    @FXML
    private Button polarizButton;

    /*
     * spin-orbit
     */
    @FXML
    private Label spinorbitLabel;

    @FXML
    private ToggleButton spinorbitToggle;

    @FXML
    private Button spinorbitButton;

    /*
     * fixing method
     */
    @FXML
    private Label fixmethodLabel;

    @FXML
    private ComboBox<String> fixmethodCombo;

    @FXML
    private Button fixmethodButton;

    /*
     * magnetization of X
     */
    @FXML
    private Label xmagLabel;

    @FXML
    private TextField xmagField;

    @FXML
    private Button xmagButton;

    /*
     * magnetization of Y
     */
    @FXML
    private Label ymagLabel;

    @FXML
    private TextField ymagField;

    @FXML
    private Button ymagButton;

    /*
     * magnetization of Z
     */
    @FXML
    private Label zmagLabel;

    @FXML
    private TextField zmagField;

    @FXML
    private Button zmagButton;

    /*
     * atomic magnetizations
     */
    private ElementAnsatzBinder elementBinder;

    @FXML
    private TableView<ElementAnsatz> elementTable;

    @FXML
    private TableColumn<ElementAnsatz, Integer> indexColumn;

    @FXML
    private TableColumn<ElementAnsatz, String> nameColumn;

    @FXML
    private TableColumn<ElementAnsatz, String> xmagColumn;

    @FXML
    private TableColumn<ElementAnsatz, String> ymagColumn;

    @FXML
    private TableColumn<ElementAnsatz, String> zmagColumn;

    public QEFXMagnetizController(QEFXMainController mainController, QEInput input) {
        super(mainController, input);
        this.elementBinder = null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        QENamelist nmlSystem = this.input.getNamelist(QEInput.NAMELIST_SYSTEM);

        if (nmlSystem != null) {
            this.setupPolarizationItem(nmlSystem);
            this.setupSpinOrbitItem(nmlSystem);
            this.setupFixingMethodItem(nmlSystem);
            this.setupMagnetizXItem(nmlSystem);
            this.setupMagnetizYItem(nmlSystem);
            this.setupMagnetizZItem(nmlSystem);
            this.initializeElementBinder(nmlSystem);
            this.setupElementCondition(nmlSystem);
        }

        this.setupIndexColumn();
        this.setupNameColumn();
        this.setupMagXColumn();
        this.setupMagYColumn();
        this.setupMagZColumn();
        this.setupElementTable();
    }

    private void setupPolarizationItem(QENamelist nmlSystem) {
        if (this.polarizCombo == null) {
            return;
        }

        this.polarizCombo.getItems().clear();
        QEFXComboInteger item = new QEFXComboInteger(nmlSystem.getValueBuffer("!nspin"), this.polarizCombo);

        if (this.polarizLabel != null) {
            item.setLabel(this.polarizLabel);
        }

        if (this.polarizButton != null) {
            item.setDefault(1, this.polarizButton);
        }

        item.addItems(TEXT_NON_POLARIZED, TEXT_COLLINEAR, TEXT_NON_COLLINEAR);

        item.setValueFactory(text -> {
            if (TEXT_NON_POLARIZED.equals(text)) {
                return 1;
            } else if (TEXT_COLLINEAR.equals(text)) {
                return 2;
            } else if (TEXT_NON_COLLINEAR.equals(text)) {
                return 4;
            }

            return 1;
        });
    }

    private void setupSpinOrbitItem(QENamelist nmlSystem) {
        if (this.spinorbitToggle == null) {
            return;
        }

        QEValueBuffer spinorbValue = nmlSystem.getValueBuffer("lspinorb");
        QEValueBuffer noncolinValue = nmlSystem.getValueBuffer("noncolin");

        QEFXToggleBoolean item = new QEFXToggleBoolean(spinorbValue, this.spinorbitToggle, false);

        if (this.spinorbitLabel != null) {
            item.setLabel(this.spinorbitLabel);
        }

        if (this.spinorbitButton != null) {
            item.setDefault(() -> {
                boolean lspinorbBool = false;
                if (noncolinValue.hasValue() && noncolinValue.getLogicalValue()) {
                    lspinorbBool = true;
                }

                return QEValueBase.getInstance("lspinorb", lspinorbBool);

            }, this.spinorbitButton);
        }

        item.addEnablingTrigger(noncolinValue);
        item.addEnabledCondition((name, value) -> {
            if ("noncolin".equalsIgnoreCase(name)) {
                if (value != null && value.getLogicalValue()) {
                    return true;
                } else {
                    return false;
                }
            }

            return true;
        });

        item.addWarningTrigger(noncolinValue);
        item.addWarningCondition((name, value) -> {
            if ("lspinorb".equalsIgnoreCase(name) || "noncolin".equalsIgnoreCase(name)) {
                if (spinorbValue.hasValue() && spinorbValue.getLogicalValue()) {
                    if ((!noncolinValue.hasValue()) || (!noncolinValue.getLogicalValue())) {
                        return WarningCondition.ERROR;
                    }
                }
                return WarningCondition.OK;
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void setupFixingMethodItem(QENamelist nmlSystem) {
        if (this.fixmethodCombo == null) {
            return;
        }

        QEValueBuffer nspinValue = nmlSystem.getValueBuffer("!nspin");
        QEValueBuffer constMagValue = nmlSystem.getValueBuffer("!constrained_magnetization");

        this.fixmethodCombo.getItems().clear();
        QEFXComboInteger item = new QEFXComboInteger(constMagValue, this.fixmethodCombo);

        if (this.fixmethodLabel != null) {
            item.setLabel(this.fixmethodLabel);
        }

        if (this.fixmethodButton != null) {
            item.setDefault("none", this.fixmethodButton);
        }

        item.addItems("none", "total", "atomic");

        item.addEnablingTrigger(nspinValue);
        item.addEnabledCondition((name, value) -> {
            if ("!nspin".equalsIgnoreCase(name)) {
                if (value != null && value.getIntegerValue() > 1) {
                    return true;
                } else {
                    return false;
                }
            }

            return true;
        });

        item.addWarningTrigger(nspinValue);
        item.addWarningCondition((name, value) -> {
            if ("!nspin".equalsIgnoreCase(name) || "!constrained_magnetization".equalsIgnoreCase(name)) {
                if (nspinValue.hasValue() && nspinValue.getIntegerValue() < 2) {
                    String constMagStr = null;
                    if (constMagValue.hasValue()) {
                        constMagStr = constMagValue.getCharacterValue();
                    }
                    if (constMagStr != null && (!constMagStr.isEmpty()) && (!"none".equals(constMagStr))) {
                        return WarningCondition.ERROR;
                    }
                }
                return WarningCondition.OK;
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void setupMagnetizXItem(QENamelist nmlSystem) {
        if (this.xmagField == null) {
            return;
        }

        QEValueBuffer noncolinValue = nmlSystem.getValueBuffer("noncolin");
        QEValueBuffer constMagValue = nmlSystem.getValueBuffer("constrained_magnetization");

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(
                nmlSystem.getValueBuffer("fixed_magnetization(1)"), this.xmagField);

        if (this.xmagLabel != null) {
            item.setLabel(this.xmagLabel);
        }

        if (this.xmagButton != null) {
            item.setDefault((QEValue) null, this.xmagButton);
        }

        item.addEnablingTrigger(noncolinValue);
        item.addEnablingTrigger(constMagValue);
        item.addEnabledCondition((name, value) -> {
            if ("noncolin".equalsIgnoreCase(name) || "constrained_magnetization".equalsIgnoreCase(name)) {
                if (noncolinValue.hasValue() && noncolinValue.getLogicalValue()) {
                    if (constMagValue.hasValue() && "total".equals(constMagValue.getCharacterValue())) {
                        return true;
                    }
                }
                return false;
            }

            return true;
        });

        item.addWarningCondition((name, value) -> {
            if ("fixed_magnetization(1)".equalsIgnoreCase(name)) {
                if (value != null && Math.abs(value.getRealValue()) >= 10.0) {
                    return WarningCondition.WARNING;
                } else {
                    return WarningCondition.OK;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void setupMagnetizYItem(QENamelist nmlSystem) {
        if (this.ymagField == null) {
            return;
        }

        QEValueBuffer noncolinValue = nmlSystem.getValueBuffer("noncolin");
        QEValueBuffer constMagValue = nmlSystem.getValueBuffer("constrained_magnetization");

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(
                nmlSystem.getValueBuffer("fixed_magnetization(2)"), this.ymagField);

        if (this.ymagLabel != null) {
            item.setLabel(this.ymagLabel);
        }

        if (this.ymagButton != null) {
            item.setDefault((QEValue) null, this.ymagButton);
        }

        item.addEnablingTrigger(noncolinValue);
        item.addEnablingTrigger(constMagValue);
        item.addEnabledCondition((name, value) -> {
            if ("noncolin".equalsIgnoreCase(name) || "constrained_magnetization".equalsIgnoreCase(name)) {
                if (noncolinValue.hasValue() && noncolinValue.getLogicalValue()) {
                    if (constMagValue.hasValue() && "total".equals(constMagValue.getCharacterValue())) {
                        return true;
                    }
                }
                return false;
            }

            return true;
        });

        item.addWarningCondition((name, value) -> {
            if ("fixed_magnetization(2)".equalsIgnoreCase(name)) {
                if (value != null && Math.abs(value.getRealValue()) >= 10.0) {
                    return WarningCondition.WARNING;
                } else {
                    return WarningCondition.OK;
                }
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void setupMagnetizZItem(QENamelist nmlSystem) {
        if (this.zmagField == null) {
            return;
        }

        QEValueBuffer nspinValue = nmlSystem.getValueBuffer("!nspin");
        QEValueBuffer constMagValue = nmlSystem.getValueBuffer("!constrained_magnetization");
        QEValueBuffer totMagValue = nmlSystem.getValueBuffer("!tot_magnetization");

        QEFXTextFieldDouble item = new QEFXTextFieldDouble(totMagValue, this.zmagField);

        if (this.zmagLabel != null) {
            item.setLabel(this.zmagLabel);
        }

        if (this.zmagButton != null) {
            item.setDefault((QEValue) null, this.zmagButton);
        }

        item.addEnablingTrigger(nspinValue);
        item.addEnablingTrigger(constMagValue);
        item.addEnabledCondition((name, value) -> {
            if ("!nspin".equalsIgnoreCase(name) || "!constrained_magnetization".equalsIgnoreCase(name)) {
                if (nspinValue.hasValue() && nspinValue.getIntegerValue() > 1) {
                    if (constMagValue.hasValue() && "total".equals(constMagValue.getCharacterValue())) {
                        return true;
                    }
                }
                return false;
            }

            return true;
        });

        item.addWarningTrigger(nspinValue);
        item.addWarningTrigger(constMagValue);
        item.addWarningCondition((name, value) -> {
            if ("!nspin".equalsIgnoreCase(name)
                    || "!constrained_magnetization".equalsIgnoreCase(name)
                    || "!tot_magnetization".equalsIgnoreCase(name)) {

                int nspin = nspinValue.hasValue() ? nspinValue.getIntegerValue() : 1;
                if (nspin < 2) {
                    // non-polarized
                    if (totMagValue.hasValue()) {
                        return WarningCondition.ERROR;
                    }

                } else if (nspin < 4) {
                    // colinear
                    if ((!constMagValue.hasValue()) || (!"total".equals(constMagValue.getCharacterValue()))) {
                        if (totMagValue.hasValue()) {
                            return WarningCondition.ERROR;
                        }
                    }

                } else {
                    // non-colinear
                    // NOP
                }

                if (totMagValue.hasValue() && Math.abs(totMagValue.getRealValue()) >= 10.0) {
                    return WarningCondition.WARNING;
                }

                return WarningCondition.OK;
            }

            return WarningCondition.OK;
        });

        item.pullAllTriggers();
    }

    private void initializeElementBinder(QENamelist nmlSystem) {
        if (this.elementTable == null) {
            return;
        }

        QECard card = this.input.getCard(QEAtomicSpecies.CARD_NAME);
        if (card == null || !(card instanceof QEAtomicSpecies)) {
            return;
        }

        QEAtomicSpecies atomicSpecies = (QEAtomicSpecies) card;

        this.elementBinder = new ElementAnsatzBinder(this.elementTable, atomicSpecies, nmlSystem);
    }

    private void setupElementTable() {
        if (this.elementTable == null) {
            return;
        }

        if (this.elementBinder != null) {
            this.elementBinder.bindTable();
        }
    }

    private void setupIndexColumn() {
        if (this.indexColumn == null) {
            return;
        }

        this.indexColumn.setCellValueFactory(new PropertyValueFactory<ElementAnsatz, Integer>("index"));
    }

    private void setupNameColumn() {
        if (this.nameColumn == null) {
            return;
        }

        this.nameColumn.setCellValueFactory(new PropertyValueFactory<ElementAnsatz, String>("name"));
        this.nameColumn.setComparator((String name1, String name2) -> {
            int atomNum1 = ElementUtil.getAtomicNumber(name1);
            int atomNum2 = ElementUtil.getAtomicNumber(name2);
            if (atomNum1 < atomNum2) {
                return -1;
            } else if (atomNum1 > atomNum2) {
                return 1;
            }
            return 0;
        });
    }

    private void setupMagXColumn() {
        if (this.xmagColumn == null) {
            return;
        }

        this.xmagColumn.setCellFactory(TextFieldTableCell.<ElementAnsatz> forTableColumn());
        this.xmagColumn.setCellValueFactory(new PropertyValueFactory<ElementAnsatz, String>("magX"));
    }

    private void setupMagYColumn() {
        if (this.ymagColumn == null) {
            return;
        }

        this.ymagColumn.setCellFactory(TextFieldTableCell.<ElementAnsatz> forTableColumn());
        this.ymagColumn.setCellValueFactory(new PropertyValueFactory<ElementAnsatz, String>("magY"));
    }

    private void setupMagZColumn() {
        if (this.zmagColumn == null) {
            return;
        }

        this.zmagColumn.setCellFactory(TextFieldTableCell.<ElementAnsatz> forTableColumn());
        this.zmagColumn.setCellValueFactory(new PropertyValueFactory<ElementAnsatz, String>("magZ"));
    }

    private void setupElementCondition(QENamelist nmlSystem) {
        QEValueBuffer nspinValue = nmlSystem.getValueBuffer("!nspin");
        if (nspinValue != null && nspinValue.hasValue()) {
            this.updateElementCondition(nspinValue.getIntegerValue());
        } else {
            this.updateElementCondition(1);
        }

        nspinValue.addListener(value -> {
            if (value != null) {
                this.updateElementCondition(value.getIntegerValue());
            } else {
                this.updateElementCondition(1);
            }
        });
    }

    private void updateElementCondition(int nspin) {
        if (nspin < 2) {
            // non-polarized
            if (this.elementTable != null) {
                this.elementTable.setDisable(true);
            }
            if (this.xmagColumn != null) {
                this.xmagColumn.setVisible(false);
            }
            if (this.ymagColumn != null) {
                this.ymagColumn.setVisible(false);
            }
            if (this.zmagColumn != null) {
                this.zmagColumn.setVisible(false);
            }

        } else if (nspin < 4) {
            // colinear
            if (this.elementTable != null) {
                this.elementTable.setDisable(false);
            }
            if (this.xmagColumn != null) {
                this.xmagColumn.setVisible(false);
            }
            if (this.ymagColumn != null) {
                this.ymagColumn.setVisible(false);
            }
            if (this.zmagColumn != null) {
                this.zmagColumn.setVisible(true);
            }

        } else {
            // non-colinear
            if (this.elementTable != null) {
                this.elementTable.setDisable(false);
            }
            if (this.xmagColumn != null) {
                this.xmagColumn.setVisible(true);
            }
            if (this.ymagColumn != null) {
                this.ymagColumn.setVisible(true);
            }
            if (this.zmagColumn != null) {
                this.zmagColumn.setVisible(true);
            }
        }
    }
}
