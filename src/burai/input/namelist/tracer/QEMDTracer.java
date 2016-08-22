/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input.namelist.tracer;

import burai.input.namelist.QENamelist;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBuffer;

public class QEMDTracer {

    private boolean busy;

    private QENamelist nmlControl;

    private QENamelist nmlIons;

    public QEMDTracer(QENamelist nmlControl, QENamelist nmlIons) {
        if (nmlControl == null) {
            throw new IllegalArgumentException("nmlControl is null.");
        }

        if (nmlIons == null) {
            throw new IllegalArgumentException("nmlIons is null.");
        }

        this.busy = false;
        this.nmlControl = nmlControl;
        this.nmlIons = nmlIons;
    }

    public void traceMd() {
        this.setupCalculation();
    }

    private void setupCalculation() {
        QEValueBuffer valueBuffer = this.nmlControl.getValueBuffer("calculation");

        if (valueBuffer.hasValue()) {
            this.updateCalculation(valueBuffer.getValue());
        } else {
            this.updateCalculation(null);
        }

        valueBuffer.addListener(value -> {
            this.updateCalculation(value);
        });
    }

    private void updateCalculation(QEValue value) {
        if (this.busy) {
            return;
        }

        this.busy = true;

        if (value != null) {
            if ("md".equals(value.getCharacterValue())) {
                QEValue ionValue = this.nmlIons.getValue("ion_dynamics");
                String ion = ionValue == null ? null : ionValue.getCharacterValue();
                if (ion == null || (!ion.startsWith("langevin"))) {
                    this.nmlIons.setValue("ion_dynamics = verlet");
                }

            } else if ("vc-md".equals(value.getCharacterValue())) {
                this.nmlIons.setValue("ion_dynamics = beeman");
            }
        }

        this.busy = false;
    }
}
