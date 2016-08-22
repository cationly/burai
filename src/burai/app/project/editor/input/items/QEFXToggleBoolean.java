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

public class QEFXToggleBoolean extends QEFXToggleButton<Boolean> {

    public QEFXToggleBoolean(QEValueBuffer valueBuffer, ToggleButton controlItem, boolean defaultSelected) {
        this(valueBuffer, controlItem, defaultSelected, false);
    }

    public QEFXToggleBoolean(
            QEValueBuffer valueBuffer, ToggleButton controlItem, boolean defaultSelected, boolean inverse) {
        super(valueBuffer, controlItem, defaultSelected);

        if (inverse) {
            this.setValueFactory(selected -> {
                return !selected;
            });
        }
    }

    @Override
    protected void setToValueBuffer(Boolean value) {
        if (value != null) {
            this.valueBuffer.setValue(value.booleanValue());
        }
    }

    @Override
    protected boolean setToControlItem(Boolean value, QEValue qeValue, boolean selected) {
        if (value == null || qeValue == null) {
            return false;
        }

        if (value.booleanValue() == qeValue.getLogicalValue()) {
            this.controlItem.setSelected(selected);
            return true;
        }

        return false;
    }
}
