/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.geom;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import burai.app.project.editor.input.items.QEFXTextFieldDouble;
import burai.app.project.editor.input.items.QEFXUnit;
import burai.app.project.editor.input.items.WarningCondition;
import burai.com.math.Calculator;
import burai.input.QEInput;
import burai.input.namelist.QENamelist;
import burai.input.namelist.QEReal;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBuffer;
import burai.input.namelist.QEValueWrapper;

public class QEFXLatticeItem extends QEFXTextFieldDouble {

    private QEInput input;

    private Label label;

    private Button button;

    private ComboBox<String> unit;

    public QEFXLatticeItem(String name, QEInput input,
            TextField controlItem, Label label, Button button, ComboBox<String> unit, boolean isAngle) {
        super(getValueBuffer(name, input), controlItem);

        if (input == null) {
            throw new IllegalArgumentException("input is null.");
        }

        if (label == null) {
            throw new IllegalArgumentException("label is null.");
        }

        if (button == null) {
            throw new IllegalArgumentException("button is null.");
        }

        if (unit == null) {
            throw new IllegalArgumentException("unit is null.");
        }

        this.input = input;
        this.label = label;
        this.button = button;
        this.unit = unit;

        this.initialize(isAngle);
    }

    private QEValueBuffer getValueBuffer(String name) {
        return getValueBuffer(name, this.input);
    }

    private static QEValueBuffer getValueBuffer(String name, QEInput input) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        if (input == null) {
            return null;
        }

        QENamelist nmlSystem = input.getNamelist(QEInput.NAMELIST_SYSTEM);
        if (nmlSystem == null) {
            return null;
        }

        return nmlSystem.getValueBuffer(name);
    }

    private void initialize(boolean isAngle) {
        QEValueBuffer ibravValue = this.getValueBuffer("ibrav");

        this.setLabel(this.label);

        this.setDefault((QEValue) null, this.button);

        if (isAngle) {
            this.setUnit(new QEFXUnit(this.unit, QEFXUnit.UNIT_TYPE_ANGLE));
        } else {
            this.setUnit(new QEFXUnit(this.unit, QEFXUnit.UNIT_TYPE_LENGTH_ANGS));
        }

        if (isAngle) {
            this.setLowerBound(0.0, QEFXTextFieldDouble.BOUND_TYPE_LESS_THAN);
            this.setUpperBound(180.0, QEFXTextFieldDouble.BOUND_TYPE_LESS_THAN);
        } else {
            this.setLowerBound(0.0, QEFXTextFieldDouble.BOUND_TYPE_LESS_THAN);
        }

        if (isAngle) {
            this.setValueFactory((text) -> {
                if (text == null) {
                    return null;
                }

                String value = null;
                try {
                    double dbleText = Calculator.expr(text);
                    if (0.0 < dbleText && dbleText < 180.0) {
                        double dbleValue = Math.cos(dbleText * Math.PI / 180.0);
                        value = Double.toString(dbleValue);
                    } else {
                        value = null;
                    }
                } catch (NumberFormatException e) {
                    value = text;
                }
                return value;
            });

            this.setTextFactory((value) -> {
                if (value == null) {
                    return null;
                }

                String text = null;
                try {
                    double dbleValue = Calculator.expr(value);
                    if (Math.abs(dbleValue) <= 1.0) {
                        double dbleText = Math.acos(dbleValue) * 180.0 / Math.PI;
                        text = new QEReal("x", dbleText).getCharacterValue();
                    } else {
                        text = null;
                    }
                } catch (NumberFormatException e) {
                    text = value;
                }
                return text;
            });
        }

        if (ibravValue != null) {
            this.addEnablingTrigger(ibravValue);
        }

        if (ibravValue != null) {
            this.addWarningTrigger(ibravValue);
        }

        this.addWarningCondition((name, value) -> {
            if (!this.isDisable()) {
                String text = this.controlItem.getText();
                if (text == null || text.trim().isEmpty()) {
                    return WarningCondition.ERROR;
                }
            }
            return WarningCondition.OK;
        });
    }

    public void setDefault(QEValueWrapper value) {
        this.setDefault(value, this.button);
    }
}
