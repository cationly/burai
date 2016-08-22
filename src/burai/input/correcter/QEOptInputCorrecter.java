/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input.correcter;

import burai.input.QEInput;
import burai.input.namelist.QEValue;

public class QEOptInputCorrecter extends QEInputCorrecter {

    public QEOptInputCorrecter(QEInput input) {
        super(input);
    }

    @Override
    public void correctInput() {
        this.correctNamelistControl();
        this.correctNamelistIons();
        this.correctNamelistCell();
    }

    private void correctNamelistControl() {
        if (this.nmlControl == null) {
            return;
        }

        QEValue value = null;

        /*
         * calculation
         */
        String calculation = null;
        value = this.nmlControl.getValue("calculation");
        if (value != null) {
            calculation = value.getCharacterValue();
        }
        if ((!"relax".equals(calculation)) && (!"vc-relax".equals(calculation))) {
            this.nmlControl.setValue("calculation = relax");
        }

        /*
         * max_seconds
         */
        value = this.nmlControl.getValue("max_seconds");
        if (value == null) {
            this.nmlControl.setValue("max_seconds = 1.728e+05");
        }

        /*
         * nstep
         */
        int nstep = 0;
        value = this.nmlControl.getValue("nstep");
        if (value != null) {
            nstep = value.getIntegerValue();
        }
        if (nstep <= 1) {
            this.nmlControl.setValue("nstep = 100");
        }

        /*
         * tprnfor
         */
        value = this.nmlControl.getValue("tprnfor");
        if (value != null && (!value.getLogicalValue())) {
            this.nmlControl.removeValue("tprnfor");
        }

        /*
         * tstress
         */
        value = this.nmlControl.getValue("tstress");
        if (value != null && (!value.getLogicalValue())) {
            this.nmlControl.removeValue("tstress");
        }

        /*
         * forc_conv_thr
         */
        value = this.nmlControl.getValue("forc_conv_thr");
        if (value == null) {
            this.nmlControl.setValue("forc_conv_thr = 1.0e-3");
        }
    }

    private void correctNamelistIons() {
        if (this.nmlIons == null) {
            return;
        }

        QEValue value = null;

        /*
         * ion_dynamics
         */
        value = this.nmlIons.getValue("ion_dynamics");
        if (value == null) {
            this.nmlIons.setValue("ion_dynamics = bfgs");
        }
    }

    private void correctNamelistCell() {
        if (this.nmlCell == null) {
            return;
        }

        QEValue calcValue = this.nmlControl.getValue("calculation");
        if (calcValue == null || (!"vc-relax".equals(calcValue.getCharacterValue()))) {
            return;
        }

        QEValue value = null;

        /*
         * cell_dynamics
         */
        value = this.nmlCell.getValue("cell_dynamics");
        if (value == null) {
            this.nmlCell.setValue("cell_dynamics = bfgs");
        }

        /*
         * press_conv_thr
         */
        value = this.nmlCell.getValue("press_conv_thr");
        if (value == null) {
            this.nmlCell.setValue("press_conv_thr = 0.5");
        }
    }
}
