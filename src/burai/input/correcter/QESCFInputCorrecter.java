/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input.correcter;

import burai.atoms.element.ElementUtil;
import burai.input.QEInput;
import burai.input.namelist.QEValue;

public class QESCFInputCorrecter extends QEInputCorrecter {

    private boolean toCheckMagnetization;

    public QESCFInputCorrecter(QEInput input) {
        super(input);
        this.toCheckMagnetization = true;
    }

    @Override
    public void correctInput() {
        this.correctNamelistControl();
        this.correctNamelistSystem();
        this.correctNamelistElectrons();
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
        this.nmlControl.setValue("calculation = scf");

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

        CutoffCorrector cutoffCorrector = new CutoffCorrector(this.input);

        QEValue value = null;

        /*
         * ecutwfc
         */
        double ecutwfc = 0.0;
        value = this.nmlSystem.getValue("ecutwfc");
        if (value != null) {
            ecutwfc = value.getRealValue();
        } else {
            if (cutoffCorrector.isAvailable()) {
                ecutwfc = cutoffCorrector.getCutoffOfWF();
                this.nmlSystem.setValue("ecutwfc = " + ecutwfc);
            }
        }

        /*
         * ecutrho
         */
        double ecutrho = 0.0;
        value = this.nmlSystem.getValue("ecutrho");
        if (value != null) {
            ecutrho = value.getRealValue();
        }
        if (ecutrho < 4.0 * ecutwfc) {
            if (cutoffCorrector.isAvailable()) {
                ecutrho = cutoffCorrector.getCutoffOfCharge();
                this.nmlSystem.setValue("ecutrho = " + ecutrho);
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

        /*
         * Magnetization (nspin & starting_magnetization)
         */
        this.correctMagnetization();
    }

    private void correctNamelistElectrons() {
        if (this.nmlElectrons == null) {
            return;
        }

        QEValue value = null;

        /*
         * electron_maxstep
         */
        value = this.nmlElectrons.getValue("electron_maxstep");
        if (value == null) {
            this.nmlElectrons.setValue("electron_maxstep = 200");
        }

        /*
         * conv_thr
         */
        value = this.nmlElectrons.getValue("conv_thr");
        if (value == null) {
            this.nmlElectrons.setValue("conv_thr = 1.0e-6");
        }

        /*
         * startingwfc
         */
        value = this.nmlElectrons.getValue("startingwfc");
        if (value == null) {
            this.nmlElectrons.setValue("startingwfc = atomic+random");
        }

        /*
         * startingpot
         */
        value = this.nmlElectrons.getValue("startingpot");
        if (value == null) {
            this.nmlElectrons.setValue("startingpot = atomic");
        }

        /*
         * mixing_beta
         */
        if (!this.toCheckMagnetization) {
            int nspin = 1;
            if (this.nmlSystem != null) {
                QEValue nspinValue = this.nmlSystem.getValue("nspin");
                if (nspinValue != null) {
                    nspin = nspinValue.getIntegerValue();
                }

                QEValue noncolinValue = this.nmlSystem.getValue("noncolin");
                if (noncolinValue != null && noncolinValue.getLogicalValue()) {
                    nspin = 4;
                }
            }

            value = this.nmlElectrons.getValue("mixing_beta");
            if (value == null) {
                if (nspin < 2) {
                    this.nmlElectrons.setValue("mixing_beta = 0.7");
                } else {
                    this.nmlElectrons.setValue("mixing_beta = 0.4");
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
                this.cardKPoints.setRecommendedCondition(this.input);
            }

        } else {
            if (this.cardKPoints.numKPoints() < 1) {
                this.cardKPoints.setRecommendedCondition(this.input);
            }
        }
    }

    private boolean hasTransitionMetals() {
        if (this.cardSpecies == null) {
            return false;
        }

        int numSpecies = this.cardSpecies.numSpecies();
        for (int i = 0; i < numSpecies; i++) {
            String label = this.cardSpecies.getLabel(i);
            if (label != null && !label.isEmpty()) {
                if (ElementUtil.isTransitionMetal(label)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void correctTransitionMetals() {
        if (this.cardSpecies == null) {
            return;
        }

        int numSpecies = this.cardSpecies.numSpecies();
        for (int i = 0; i < numSpecies; i++) {
            double startMag = 0.0;
            String label = this.cardSpecies.getLabel(i);
            if (label != null && !label.isEmpty()) {
                if (ElementUtil.isTransitionMetal(label)) {
                    startMag = 0.2;
                }
            }

            if (this.nmlSystem != null) {
                if (this.nmlSystem.getValue("starting_magnetization(" + (i + 1) + ")") == null) {
                    this.nmlSystem.setValue("starting_magnetization(" + (i + 1) + ") = " + startMag);
                }
            }
        }
    }

    private void correctMagnetization() {
        if (!this.toCheckMagnetization) {
            return;
        }

        if (this.cardSpecies != null) {
            int numSpecies = this.cardSpecies.numSpecies();
            if (numSpecies > 0) {
                this.toCheckMagnetization = false;
            }
        }

        if (this.nmlSystem != null) {
            QEValue nspinValue = this.nmlSystem.getValue("nspin");
            QEValue noncolinValue = this.nmlSystem.getValue("noncolin");
            if (nspinValue == null && noncolinValue == null) {
                if (this.hasTransitionMetals()) {
                    this.correctTransitionMetals();
                    this.nmlSystem.setValue("nspin = 2");
                }
            }
        }
    }
}
