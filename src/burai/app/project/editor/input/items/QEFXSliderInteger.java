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
import burai.input.namelist.QEInteger;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBuffer;

public class QEFXSliderInteger extends QEFXSlider {

    public QEFXSliderInteger(QEValueBuffer valueBuffer, Slider controlItem, int defaultValue) {
        super(valueBuffer, controlItem, new QEInteger("i", defaultValue));
    }

    @Override
    protected void setToValueBuffer(double value) {
        int i = (int) (Math.rint(value) + 0.1);
        this.valueBuffer.setValue(i);
    }

    @Override
    protected void setToControlItem(QEValue qeValue) {
        double maxValue = this.controlItem.getMax();
        double minValue = this.controlItem.getMin();
        double value = qeValue == null ? minValue : qeValue.getIntegerValue();
        value = Math.min(Math.max(minValue, value), maxValue);
        this.controlItem.setValue(value);
    }
}
