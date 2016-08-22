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

public class QEFXToggleString extends QEFXToggleButton<String> {

    public QEFXToggleString(QEValueBuffer valueBuffer, ToggleButton controlItem, boolean defaultSelected) {
        super(valueBuffer, controlItem, defaultSelected);
    }

    public QEFXToggleString(QEValueBuffer valueBuffer,
            ToggleButton controlItem, boolean defaultSelected, String onValue, String offValue) {
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
    protected void setToValueBuffer(String value) {
        if (value != null) {
            this.valueBuffer.setValue(value);
        }
    }

    @Override
    protected boolean setToControlItem(String value, QEValue qeValue, boolean selected) {
        if (value == null || qeValue == null) {
            return false;
        }

        if (value.equals(qeValue.getCharacterValue())) {
            this.controlItem.setSelected(selected);
            return true;
        }

        return false;
    }
}
