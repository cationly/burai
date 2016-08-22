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

public class QEDOSTracer {

    public static final int NGAUSS_TETRAHEDRON = 100;

    private boolean busyValue;
    private boolean busyGauss;

    private QENamelist nmlDos;
    private QENamelist nmlProjwfc;

    public QEDOSTracer(QENamelist nmlDos, QENamelist nmlProjwfc) {
        if (nmlDos == null) {
            throw new IllegalArgumentException("nmlDos is null.");
        }

        if (nmlProjwfc == null) {
            throw new IllegalArgumentException("nmlProjwfc is null.");
        }

        this.busyValue = false;
        this.busyGauss = false;
        this.nmlDos = nmlDos;
        this.nmlProjwfc = nmlProjwfc;
    }

    public void traceDos() {
        this.setupValue("emin");
        this.setupValue("emax");
        this.setupValue("deltae");
        this.setupValue("ngauss");
        this.setupValue("degauss");

        this.setupNGauss();
        this.setupDeGauss();
        this.setupNGaussExt();
    }

    private void setupValue(String name) {
        QEValueBuffer valueBuffer1 = this.nmlDos.getValueBuffer(name);
        QEValueBuffer valueBuffer2 = this.nmlProjwfc.getValueBuffer(name);

        if (valueBuffer1.hasValue()) {
            this.updateValue(false, name, valueBuffer1.getValue());
        }

        if (valueBuffer2.hasValue()) {
            this.updateValue(true, name, valueBuffer2.getValue());
        }

        valueBuffer1.addListener(value -> {
            this.updateValue(false, name, value);
        });

        valueBuffer2.addListener(value -> {
            this.updateValue(true, name, value);
        });
    }

    private void updateValue(boolean dosType, String name, QEValue value) {
        if (this.busyValue) {
            return;
        }

        this.busyValue = true;

        QENamelist nmlDos_ = null;
        if (dosType) {
            nmlDos_ = this.nmlDos;
        } else {
            nmlDos_ = this.nmlProjwfc;
        }

        if (value != null) {
            nmlDos_.setValue(value);
        } else {
            nmlDos_.removeValue(name);
        }

        this.busyValue = false;
    }

    private void setupNGaussExt() {
        QEValueBuffer valueBuffer = this.nmlDos.getValueBuffer("!ngauss");

        if (valueBuffer.hasValue()) {
            this.updateNGaussExt(valueBuffer.getValue());
        } else {
            this.updateNGaussExt(null);
        }

        valueBuffer.addListener(value -> {
            this.updateNGaussExt(value);
        });
    }

    private void updateNGaussExt(QEValue value) {
        if (this.busyGauss) {
            return;
        }

        this.busyGauss = true;

        if (value == null) {
            this.nmlDos.removeValue("ngauss");

        } else {
            int ngauss = value.getIntegerValue();
            if (ngauss == NGAUSS_TETRAHEDRON) {
                this.nmlDos.removeValue("ngauss");
                this.nmlDos.removeValue("degauss");
            } else {
                this.nmlDos.setValue("ngauss = " + ngauss);
            }
        }

        this.busyGauss = false;
    }

    private void setupNGauss() {
        QEValueBuffer valueBuffer = this.nmlDos.getValueBuffer("ngauss");

        if (valueBuffer.hasValue()) {
            this.updateNGauss(valueBuffer.getValue());
        } else {
            this.updateNGauss(null);
        }

        valueBuffer.addListener(value -> {
            this.updateNGauss(value);
        });
    }

    private void updateNGauss(QEValue value) {
        if (this.busyGauss) {
            return;
        }

        this.busyGauss = true;

        if (value == null) {
            QEValue degaussValue = this.nmlDos.getValue("degauss");
            if (degaussValue == null) {
                this.nmlDos.setValue("!ngauss = " + NGAUSS_TETRAHEDRON);
            } else {
                this.nmlDos.removeValue("!ngauss");
            }

        } else {
            this.nmlDos.setValue("!ngauss = " + value.getIntegerValue());
        }

        this.busyGauss = false;
    }

    private void setupDeGauss() {
        QEValueBuffer valueBuffer = this.nmlDos.getValueBuffer("degauss");

        if (valueBuffer.hasValue()) {
            this.updateDeGauss(valueBuffer.getValue());
        } else {
            this.updateDeGauss(null);
        }

        valueBuffer.addListener(value -> {
            this.updateDeGauss(value);
        });
    }

    private void updateDeGauss(QEValue value) {
        if (this.busyGauss) {
            return;
        }

        this.busyGauss = true;

        if (value == null) {
            QEValue ngaussValue = this.nmlDos.getValue("ngauss");
            if (ngaussValue == null) {
                this.nmlDos.setValue("!ngauss = " + NGAUSS_TETRAHEDRON);
            }

        } else {
            QEValue ngaussValue = this.nmlDos.getValue("ngauss");
            if (ngaussValue == null) {
                this.nmlDos.setValue("!ngauss = 0");
                this.nmlDos.setValue("ngauss = 0");
            }
        }

        this.busyGauss = false;
    }
}
