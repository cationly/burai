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

public class QESpinTracer {

    private boolean busyNSpin;
    private boolean busyConstMag;
    private boolean busyTotMag;

    private QENamelist nmlSystem;

    public QESpinTracer(QENamelist nmlSystem) {
        if (nmlSystem == null) {
            throw new IllegalArgumentException("nmlSystem is null.");
        }

        this.busyNSpin = false;
        this.busyConstMag = false;
        this.busyTotMag = false;
        this.nmlSystem = nmlSystem;
    }

    public void traceSpin() {
        this.setupNSpin();
        this.setupNoncolin();
        this.setupConstrainedMagnetization();
        this.setupTotMagnetization();
        this.setupFixedMagnetization3();

        this.setupNSpinExt();
        this.setupConstrainedMagnetizationExt();
        this.setupTotMagnetizationExt();
    }

    private void setupNSpinExt() {
        QEValueBuffer valueBuffer = this.nmlSystem.getValueBuffer("!nspin");

        if (valueBuffer.hasValue()) {
            this.updateNSpinExt(valueBuffer.getValue());
        } else {
            this.updateNSpinExt(null);
        }

        valueBuffer.addListener(value -> {
            this.updateNSpinExt(value);
        });
    }

    private void updateNSpinExt(QEValue value) {
        if (this.busyNSpin) {
            return;
        }

        this.busyNSpin = true;

        if (value == null) {
            this.nmlSystem.removeValue("nspin");
            this.nmlSystem.removeValue("noncolin");

        } else {
            int nspin = value.getIntegerValue();
            if (nspin < 4) {
                this.nmlSystem.setValue("nspin = " + nspin);
                this.nmlSystem.removeValue("noncolin");
            } else {
                this.nmlSystem.removeValue("nspin");
                this.nmlSystem.setValue("noncolin = .true.");
            }
        }

        this.letStartingMagnetization1Be();

        this.busyNSpin = false;
    }

    private void setupNSpin() {
        QEValueBuffer valueBuffer = this.nmlSystem.getValueBuffer("nspin");

        if (valueBuffer.hasValue()) {
            this.updateNSpin(valueBuffer.getValue());
        } else {
            this.updateNSpin(null);
        }

        valueBuffer.addListener(value -> {
            this.updateNSpin(value);
        });
    }

    private void updateNSpin(QEValue value) {
        if (this.busyNSpin) {
            return;
        }

        this.busyNSpin = true;

        if (value == null) {
            QEValue noncolinValue = this.nmlSystem.getValue("noncolin");
            if (noncolinValue != null && noncolinValue.getLogicalValue()) {
                this.nmlSystem.setValue("!nspin = 4");
            } else {
                this.nmlSystem.setValue("!nspin = 1");
            }

        } else {
            int i = value.getIntegerValue();
            this.nmlSystem.setValue("!nspin = " + i);
            if (i < 4) {
                this.nmlSystem.removeValue("noncolin");
            } else {
                this.nmlSystem.removeValue("nspin");
                this.nmlSystem.setValue("noncolin = .true.");
            }
        }

        this.letStartingMagnetization1Be();

        this.busyNSpin = false;
    }

    private void setupNoncolin() {
        QEValueBuffer valueBuffer = this.nmlSystem.getValueBuffer("noncolin");

        if (valueBuffer.hasValue()) {
            this.updateNoncolin(valueBuffer.getValue());
        } else {
            this.updateNoncolin(null);
        }

        valueBuffer.addListener(value -> {
            this.updateNoncolin(value);
        });

        valueBuffer.addListener(value -> {
            QEValueBuffer constMagExtValue = this.nmlSystem.getValueBuffer("!constrained_magnetization");
            QEValueBuffer constMagValue = this.nmlSystem.getValueBuffer("constrained_magnetization");

            if (constMagValue.hasValue()) {
                this.updateConstrainedMagnetization(constMagValue.getValue());
            }

            if (constMagExtValue.hasValue()) {
                this.updateConstrainedMagnetizationExt(constMagExtValue.getValue());
            } else {
                this.updateConstrainedMagnetizationExt(null);
            }
        });

        valueBuffer.addListener(value -> {
            QEValueBuffer totMagExtValue = this.nmlSystem.getValueBuffer("!tot_magnetization");
            QEValueBuffer totMagValue = this.nmlSystem.getValueBuffer("tot_magnetization");
            QEValueBuffer fixMagValue = this.nmlSystem.getValueBuffer("fixed_magnetization(3)");

            if (totMagValue.hasValue()) {
                this.updateTotMagnetization(totMagValue.getValue());
            }

            if (fixMagValue.hasValue()) {
                this.updateFixedMagnetization3(fixMagValue.getValue());
            }

            if (totMagExtValue.hasValue()) {
                this.updateTotMagnetizationExt(totMagExtValue.getValue());
            } else {
                this.updateTotMagnetizationExt(null);
            }
        });
    }

    private void updateNoncolin(QEValue value) {
        if (this.busyNSpin) {
            return;
        }

        this.busyNSpin = true;

        if (value != null && value.getLogicalValue()) {
            // non-colinear case
            this.nmlSystem.setValue("!nspin = 4");
            this.nmlSystem.removeValue("nspin");

        } else {
            // colinear case
            QEValue nspinValue = this.nmlSystem.getValue("nspin");
            if (nspinValue != null) {
                this.nmlSystem.setValue("!nspin = " + nspinValue.getIntegerValue());
            } else {
                this.nmlSystem.setValue("!nspin = 1");
            }
        }

        this.letStartingMagnetization1Be();

        this.busyNSpin = false;
    }

    private void setupConstrainedMagnetizationExt() {
        QEValueBuffer valueBuffer = this.nmlSystem.getValueBuffer("!constrained_magnetization");

        if (valueBuffer.hasValue()) {
            this.updateConstrainedMagnetizationExt(valueBuffer.getValue());
        } else {
            this.updateConstrainedMagnetizationExt(null);
        }

        valueBuffer.addListener(value -> {
            this.updateConstrainedMagnetizationExt(value);
        });
    }

    private void updateConstrainedMagnetizationExt(QEValue value) {
        if (this.busyConstMag) {
            return;
        }

        this.busyConstMag = true;

        boolean toCopyValue = true;
        QEValue noncolinValue = this.nmlSystem.getValue("noncolin");
        // colinear and constrained_magnetization = "total"
        if (noncolinValue == null || (!noncolinValue.getLogicalValue())) {
            if (value != null && "total".equals(value.getCharacterValue())) {
                toCopyValue = false;
            }
        }

        if (toCopyValue) {
            if (value == null) {
                this.nmlSystem.removeValue("constrained_magnetization");
            } else {
                String str = value.getCharacterValue();
                this.nmlSystem.setValue("constrained_magnetization = " + str);
            }

        } else {
            this.nmlSystem.removeValue("constrained_magnetization");
        }

        this.busyConstMag = false;
    }

    private void setupConstrainedMagnetization() {
        QEValueBuffer valueBuffer = this.nmlSystem.getValueBuffer("constrained_magnetization");

        if (valueBuffer.hasValue()) {
            this.updateConstrainedMagnetization(valueBuffer.getValue());
        } else {
            this.updateConstrainedMagnetization(null);
        }

        valueBuffer.addListener(value -> {
            this.updateConstrainedMagnetization(value);
        });
    }

    private void updateConstrainedMagnetization(QEValue value) {
        if (this.busyConstMag) {
            return;
        }

        this.busyConstMag = true;

        if (value == null) {
            this.nmlSystem.removeValue("!constrained_magnetization");
        } else {
            String str = value.getCharacterValue();
            this.nmlSystem.setValue("!constrained_magnetization = " + str);
        }

        QEValue noncolinValue = this.nmlSystem.getValue("noncolin");
        // colinear and constrained_magnetization = "total"
        if (noncolinValue == null || (!noncolinValue.getLogicalValue())) {
            if (value != null && "total".equals(value.getCharacterValue())) {
                this.nmlSystem.removeValue("constrained_magnetization");
            }
        }

        this.busyConstMag = false;
    }

    private void setupTotMagnetizationExt() {
        QEValueBuffer valueBuffer = this.nmlSystem.getValueBuffer("!tot_magnetization");

        if (valueBuffer.hasValue()) {
            this.updateTotMagnetizationExt(valueBuffer.getValue());
        } else {
            this.updateTotMagnetizationExt(null);
        }

        valueBuffer.addListener(value -> {
            this.updateTotMagnetizationExt(value);
        });
    }

    private void updateTotMagnetizationExt(QEValue value) {
        if (this.busyTotMag) {
            return;
        }

        this.busyTotMag = true;

        QEValue noncolinValue = this.nmlSystem.getValue("noncolin");
        if (noncolinValue != null && noncolinValue.getLogicalValue()) {
            // non-colinear case
            if (value == null) {
                this.nmlSystem.removeValue("fixed_magnetization(3)");
            } else {
                this.nmlSystem.setValue("fixed_magnetization(3) = " + value.getRealValue());
            }
            this.nmlSystem.removeValue("tot_magnetization");

        } else {
            // colinear case
            if (value == null) {
                this.nmlSystem.removeValue("tot_magnetization");
            } else {
                this.nmlSystem.setValue("tot_magnetization = " + value.getRealValue());
                this.letConstrainedMagnetizationExtBeTotal();
            }
            this.nmlSystem.removeValue("fixed_magnetization(3)");
        }

        this.busyTotMag = false;
    }

    private void setupTotMagnetization() {
        QEValueBuffer valueBuffer = this.nmlSystem.getValueBuffer("tot_magnetization");

        if (valueBuffer.hasValue()) {
            this.updateTotMagnetization(valueBuffer.getValue());
        } else {
            this.updateTotMagnetization(null);
        }

        valueBuffer.addListener(value -> {
            this.updateTotMagnetization(value);
        });
    }

    private void updateTotMagnetization(QEValue value) {
        if (this.busyTotMag) {
            return;
        }

        this.busyTotMag = true;

        QEValue noncolinValue = this.nmlSystem.getValue("noncolin");
        if (noncolinValue != null && noncolinValue.getLogicalValue()) {
            // non-colinear case
            // NOP

        } else {
            // colinear case
            if (value == null) {
                this.nmlSystem.removeValue("!tot_magnetization");
            } else {
                this.nmlSystem.setValue("!tot_magnetization = " + value.getRealValue());
                this.letConstrainedMagnetizationExtBeTotal();
            }
        }

        this.busyTotMag = false;
    }

    private void setupFixedMagnetization3() {
        QEValueBuffer valueBuffer = this.nmlSystem.getValueBuffer("fixed_magnetization(3)");

        if (valueBuffer.hasValue()) {
            this.updateFixedMagnetization3(valueBuffer.getValue());
        } else {
            this.updateFixedMagnetization3(null);
        }

        valueBuffer.addListener(value -> {
            this.updateFixedMagnetization3(value);
        });
    }

    private void updateFixedMagnetization3(QEValue value) {
        if (this.busyTotMag) {
            return;
        }

        this.busyTotMag = true;

        QEValue noncolinValue = this.nmlSystem.getValue("noncolin");
        if (noncolinValue != null && noncolinValue.getLogicalValue()) {
            // non-colinear case
            if (value == null) {
                this.nmlSystem.removeValue("!tot_magnetization");
            } else {
                this.nmlSystem.setValue("!tot_magnetization = " + value.getRealValue());
            }

        } else {
            // colinear case
            // NOP
        }

        this.busyTotMag = false;
    }

    private void letStartingMagnetization1Be() {
        QEValue nspinValue = this.nmlSystem.getValue("!nspin");
        int nspin = nspinValue == null ? 1 : nspinValue.getIntegerValue();

        if (nspin > 1) {
            if (this.nmlSystem.getValue("starting_magnetization(1)") == null) {
                this.nmlSystem.setValue("starting_magnetization(1) = 0.0");
            }
        }
    }

    private void letConstrainedMagnetizationExtBeTotal() {
        QEValue constMagValue = this.nmlSystem.getValue("!constrained_magnetization");
        String constMagStr = constMagValue == null ? null : constMagValue.getCharacterValue();
        if (constMagStr == null || constMagStr.isEmpty() || "none".equals(constMagStr)) {
            this.nmlSystem.setValue("!constrained_magnetization = total");
        }
    }
}
