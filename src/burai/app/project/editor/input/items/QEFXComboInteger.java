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

public class QEFXComboInteger extends QEFXComboBox<Integer> {

    public QEFXComboInteger(QEValueBuffer valueBuffer, ComboBox<String> controlItem) {
        super(valueBuffer, controlItem);
    }

    @Override
    protected void setToValueBuffer(Integer value) {
        if (value != null) {
            this.valueBuffer.setValue(value.intValue());
        }
    }

    @Override
    protected boolean setToControlItem(Integer value, QEValue qeValue, String item) {
        if (value == null || qeValue == null) {
            return false;
        }

        if (value.intValue() == qeValue.getIntegerValue()) {
            this.controlItem.setValue(item);
            return true;
        }

        return false;
    }
}
