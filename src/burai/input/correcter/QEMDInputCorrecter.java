/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input.correcter;

import burai.com.consts.Constants;
import burai.input.QEInput;
import burai.input.namelist.QEValue;

public class QEMDInputCorrecter extends QEInputCorrecter {

    public QEMDInputCorrecter(QEInput input) {
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
        if ((!"md".equals(calculation)) && (!"vc-md".equals(calculation))) {
            this.nmlControl.setValue("calculation = md");
        }

        /*
         * max_seconds
         */
        value = this.nmlControl.getValue("max_seconds");
        if (value == null) {
            this.nmlControl.setValue("max_seconds = 6.048E+05");
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
            nstep = 5000;
            this.nmlControl.setValue("nstep = " + nstep);
        }

        /*
         * dt
         */
        double dt = -1.0;
        value = this.nmlControl.getValue("dt");
        if (value != null) {
            dt = value.getRealValue();
        }
        if (dt <= 0.0) {
            dt = 0.5 * 0.5 / (Constants.AU_PS * 1000.0); // 0.5fs
            this.nmlControl.setValue("dt = " + dt);
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
         * etot_conv_thr
         */
        value = this.nmlControl.getValue("etot_conv_thr");
        if (value != null) {
            this.nmlControl.removeValue("etot_conv_thr");
        }

        /*
         * forc_conv_thr
         */
        value = this.nmlControl.getValue("forc_conv_thr");
        if (value != null) {
            this.nmlControl.removeValue("forc_conv_thr");
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
            this.nmlIons.setValue("ion_dynamics = verlet");
        }
    }

    private void correctNamelistCell() {
        if (this.nmlCell == null) {
            return;
        }

        QEValue calcValue = this.nmlControl.getValue("calculation");
        if (calcValue == null || (!"vc-md".equals(calcValue.getCharacterValue()))) {
            return;
        }

        QEValue value = null;

        /*
         * cell_dynamics
         */
        value = this.nmlCell.getValue("cell_dynamics");
        if (value == null) {
            this.nmlCell.setValue("cell_dynamics = pr");
        }

        /*
         * press_conv_thr
         */
        value = this.nmlCell.getValue("press_conv_thr");
        if (value != null) {
            this.nmlCell.removeValue("press_conv_thr");
        }
    }
}
