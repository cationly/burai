/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input.correcter;

import java.util.List;

import burai.input.QEInput;
import burai.input.card.QEKPoint;
import burai.input.namelist.QEValue;

public class QEBandInputCorrecter extends QEInputCorrecter {

    public QEBandInputCorrecter(QEInput input) {
        super(input);
    }

    @Override
    public void correctInput() {
        this.correctNamelistControl();
        this.correctNamelistSystem();
        this.correctNamelistBands();
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
        if (!"bands".equals(calculation)) {
            this.nmlControl.setValue("calculation = bands");
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
    }

    private void correctNamelistBands() {
        if (this.nmlBands == null) {
            return;
        }

        QEValue value = null;

        /*
         * lsym
         */
        value = this.nmlBands.getValue("lsym");
        if (value == null) {
            this.nmlBands.setValue("lsym = .false.");
        }

        /*
         * spin_component
         */
        value = this.nmlBands.getValue("spin_component");
        if (value == null) {
            this.nmlBands.setValue("spin_component = 1");
        }
    }

    private void correctKPoints() {
        if (this.cardKPoints == null) {
            return;
        }

        int numKPoints = 0;
        if (this.cardKPoints.isTpibaB() || this.cardKPoints.isCrystalB()) {
            numKPoints = this.cardKPoints.numKPoints();
        } else {
            this.cardKPoints.clear();
            this.cardKPoints.setTpibaB();
        }

        if (numKPoints > 0) {
            return;
        }

        BrillouinPathGenerator generator = new BrillouinPathGenerator(this.input);
        if (!generator.isAvailable()) {
            return;
        }

        List<QEKPoint> kpoints = generator.getKPoints();
        if (kpoints == null || kpoints.isEmpty()) {
            return;
        }

        this.cardKPoints.clear();
        this.cardKPoints.setTpibaB();
        for (QEKPoint kpoint : kpoints) {
            if (kpoint != null) {
                this.cardKPoints.addKPoint(kpoint);
            }
        }
    }
}
