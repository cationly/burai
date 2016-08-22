/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.items;

import javafx.scene.control.Slider;
import burai.input.namelist.QEReal;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBuffer;

public class QEFXSliderDouble extends QEFXSlider {

    public QEFXSliderDouble(QEValueBuffer valueBuffer, Slider controlItem, double defaultValue) {
        super(valueBuffer, controlItem, new QEReal("x", defaultValue));
    }

    @Override
    protected void setToValueBuffer(double value) {
        this.valueBuffer.setValue(value);
    }

    @Override
    protected void setToControlItem(QEValue qeValue) {
        double maxValue = this.controlItem.getMax();
        double minValue = this.controlItem.getMin();
        double value = qeValue == null ? minValue : qeValue.getRealValue();
        value = Math.min(Math.max(minValue, value), maxValue);
        this.controlItem.setValue(value);
    }
}
