/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.items;

import javafx.scene.control.ComboBox;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBuffer;

public class QEFXComboDouble extends QEFXComboBox<Double> {

    private double DELTA = 1.0e-20;

    public QEFXComboDouble(QEValueBuffer valueBuffer, ComboBox<String> controlItem) {
        super(valueBuffer, controlItem);
    }

    @Override
    protected void setToValueBuffer(Double value) {
        if (value != null) {
            this.valueBuffer.setValue(value.doubleValue());
        }
    }

    @Override
    protected boolean setToControlItem(Double value, QEValue qeValue, String item) {
        if (value == null || qeValue == null) {
            return false;
        }

        if (Math.abs(value.doubleValue() - qeValue.getRealValue()) < DELTA) {
            this.controlItem.setValue(item);
            return true;
        }

        return false;
    }
}
