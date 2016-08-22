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
import burai.input.namelist.tracer.QEDOSTracer;

public class QEDOSInputCorrecter extends QEInputCorrecter {

    public QEDOSInputCorrecter(QEInput input) {
        super(input);
    }

    @Override
    public void correctInput() {
        this.correctNamelistControl();
        this.correctNamelistSystem();
        this.correctNamelistDos();
        this.correctKPoints();
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
        if (!"nscf".equals(calculation)) {
            this.nmlControl.setValue("calculation = nscf");
        }

        /*
         * max_seconds
         */
        value = this.nmlControl.getValue("max_seconds");
        if (value == null) {
            this.nmlControl.setValue("max_seconds = 8.64e+04");
        }
    }

    private void correctNamelistSystem() {
        if (this.nmlSystem == null) {
            return;
        }

        QEValue value = null;

        /*
         * nbnd
         */
        int nbnd = 0;
        value = this.nmlSystem.getValue("nbnd");
        if (value != null) {
            nbnd = value.getIntegerValue();
        } else {
            BandCorrector bandCorrector = new BandCorrector(this.input);
            nbnd = bandCorrector.isAvailable() ? bandCorrector.getNumBands() : 0;
            if (nbnd > 0) {
                this.nmlSystem.setValue("nbnd = " + nbnd);
            }
        }

        /*
         * occupations
         */
        String occupations = "";
        value = this.nmlSystem.getValue("occupations");
        if (value != null) {
            occupations = value.getCharacterValue();
        } else {
            occupations = "smearing";
            this.nmlSystem.setValue("occupations = " + occupations);
        }

        /*
         * smearing
         */
        String smearing = "";
        value = this.nmlSystem.getValue("smearing");
        if (value != null) {
            smearing = value.getCharacterValue();
        } else {
            if ("smearing".equals(occupations)) {
                smearing = "gaussian";
                this.nmlSystem.setValue("smearing = " + smearing);
            }
        }

        if ("gauss".equals(smearing)) {
            smearing = "gaussian";
            this.nmlSystem.setValue("smearing = " + smearing);
        } else if ("m-p".equals(smearing) || "mp".equals(smearing)) {
            smearing = "methfessel-paxton";
            this.nmlSystem.setValue("smearing = " + smearing);
        } else if ("cold".equals(smearing) || "m-v".equals(smearing) || "mv".equals(smearing)) {
            smearing = "marzari-vanderbilt";
            this.nmlSystem.setValue("smearing = " + smearing);
        } else if ("f-d".equals(smearing) || "fd".equals(smearing)) {
            smearing = "fermi-dirac";
            this.nmlSystem.setValue("smearing = " + smearing);
        }

        /*
         * degauss
         */
        double degauss = 0.0;
        value = this.nmlSystem.getValue("degauss");
        if (value != null) {
            degauss = value.getRealValue();
        } else {
            if ("smearing".equals(occupations)) {
                degauss = 0.01;
                this.nmlSystem.setValue("degauss = " + degauss);
            }
        }
    }

    private void correctNamelistDos() {
        if (this.nmlDos == null) {
            return;
        }

        QEValue value = null;

        /*
         * Emin
         */
        double emin = 0.0;
        value = this.nmlDos.getValue("emin");
        if (value != null) {
            emin = value.getRealValue();
        } else {
            emin = -50.0;
            this.nmlDos.setValue("emin = " + emin);
        }

        /*
         * Emax
         */
        double emax = 0.0;
        value = this.nmlDos.getValue("emax");
        if (value != null) {
            emax = value.getRealValue();
        } else {
            emax = Math.max(50.0, emin + 10.0);
            this.nmlDos.setValue("emax = " + emax);
        }

        /*
         * DeltaE
         */
        value = this.nmlDos.getValue("deltae");
        if (value == null) {
            this.nmlDos.setValue("deltae = 0.01");
        }

        QEValue occupValue = this.nmlSystem == null ? null : this.nmlSystem.getValue("occupations");
        String occupations = occupValue == null ? "" : occupValue.getCharacterValue();
        if (!"tetrahedra".equals(occupations)) {
            /*
             * ngauss
             */
            value = this.nmlDos.getValue("ngauss");
            if (value == null) {
                this.nmlDos.setValue("ngauss = 0");
            }

            /*
             * degauss
             */
            value = this.nmlDos.getValue("degauss");
            if (value == null) {
                QEValue degaussValue = this.nmlSystem == null ? null : this.nmlSystem.getValue("degauss");
                double degauss = degaussValue == null ? 0.0 : degaussValue.getRealValue();
                if (degauss <= 0.0) {
                    degauss = 0.01;
                }
                this.nmlDos.setValue("degauss = " + degauss);
            }

        } else {
            /*
             * !ngauss
             */
            value = this.nmlDos.getValue("!ngauss");
            if (value == null) {
                QEValue ngaussValue = this.nmlDos.getValue("ngauss");
                QEValue degaussValue = this.nmlDos.getValue("degauss");
                if (ngaussValue == null && degaussValue == null) {
                    this.nmlDos.setValue("!ngauss = " + QEDOSTracer.NGAUSS_TETRAHEDRON);
                }
            }
        }
    }

    private void correctKPoints() {
        if (this.cardKPoints == null) {
            return;
        }

        if (this.cardKPoints.isGamma()) {
            return;

        } else if (this.cardKPoints.isAutomatic()) {
            boolean hasData = false;
            int[] kGrid = this.cardKPoints.getKGrid();
            if (kGrid != null && kGrid.length > 2) {
                if (kGrid[0] > 0 && kGrid[1] > 0 && kGrid[2] > 0) {
                    hasData = true;
                }
            }

            if (!hasData) {
                this.cardKPoints.setAccurateCondition(this.input);
            }

        } else {
            if (this.cardKPoints.numKPoints() < 1) {
                this.cardKPoints.setAccurateCondition(this.input);
            }
        }
    }
}
