/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.items;

import javafx.scene.control.ToggleButton;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBuffer;

public class QEFXToggleInteger extends QEFXToggleButton<Integer> {

    public QEFXToggleInteger(QEValueBuffer valueBuffer, ToggleButton controlItem, boolean defaultSelected) {
        super(valueBuffer, controlItem, defaultSelected);
    }

    public QEFXToggleInteger(QEValueBuffer valueBuffer,
            ToggleButton controlItem, boolean defaultSelected, int onValue, int offValue) {
        this(valueBuffer, controlItem, defaultSelected);

        this.setValueFactory(selected -> {
            if (selected) {
                return onValue;
            } else {
                return offValue;
            }
        });
    }

    @Override
    protected void setToValueBuffer(Integer value) {
        if (value != null) {
            this.valueBuffer.setValue(value.intValue());
        }
    }

    @Override
    protected boolean setToControlItem(Integer value, QEValue qeValue, boolean selected) {
        if (value == null || qeValue == null) {
            return false;
        }

        if (value.intValue() == qeValue.getIntegerValue()) {
            this.controlItem.setSelected(selected);
            return true;
        }

        return false;
    }
}
