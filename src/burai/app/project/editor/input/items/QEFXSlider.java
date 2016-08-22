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
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBuffer;

public abstract class QEFXSlider extends QEFXItem<Slider> {

    private QEValue defaultValue;

    protected QEFXSlider(QEValueBuffer valueBuffer, Slider controlItem, QEValue defaultValue) {
        super(valueBuffer, controlItem);

        this.defaultValue = defaultValue;
        this.setupSlider();
    }

    protected abstract void setToValueBuffer(double value);

    protected abstract void setToControlItem(QEValue qeValue);

    private void setupSlider() {
        if (this.valueBuffer.hasValue()) {
            this.onValueChanged(this.valueBuffer.getValue());
        } else {
            this.setToControlItem(this.defaultValue);
            this.setToValueBuffer(this.controlItem.getValue());
        }

        this.controlItem.valueProperty().addListener(o -> {
            double value = this.controlItem.getValue();
            this.setToValueBuffer(value);
        });
    }

    @Override
    protected void onValueChanged(QEValue qeValue) {
        if (qeValue == null) {
            this.setToControlItem(this.defaultValue);
        } else {
            this.setToControlItem(qeValue);
        }
    }
}
