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
import burai.input.card.QEAtomicSpecies;
import burai.input.card.QECard;
import burai.input.namelist.QENamelist;
import burai.input.namelist.QEValue;
import burai.pseudo.PseudoData;
import burai.pseudo.PseudoPotential;

public class CutoffCorrector {

    private static final double ZERO = 1.0e-8;

    private static final double MINIMUM_CUTOFF_WF = 25.0; // Ry
    private static final double MINIMUM_CUTOFF_CHARGE = 225.0; // Ry

    private QEInput input;

    private QENamelist nmlSystem;

    private QEAtomicSpecies cardSpecies;

    public CutoffCorrector(QEInput input) {
        if (input == null) {
            throw new IllegalArgumentException("input is null.");
        }

        this.input = input;

        this.nmlSystem = this.input.getNamelist(QEInput.NAMELIST_SYSTEM);

        QECard card = this.input.getCard(QEAtomicSpecies.CARD_NAME);
        this.cardSpecies = (card != null && card instanceof QEAtomicSpecies) ? ((QEAtomicSpecies) card) : null;
    }

    public boolean isAvailable() {
        if (this.cardSpecies != null && this.cardSpecies.numSpecies() > 0) {
            return true;
        }

        return false;
    }

    public double getCutoffOfWF() {
        if (this.cardSpecies == null) {
            return -1.0;
        }

        double cutoff = 0.0;

        int numElems = this.cardSpecies.numSpecies();
        for (int i = 0; i < numElems; i++) {
            PseudoPotential pseudoPot = this.cardSpecies.getPseudoPotential(i);

            double cutoff2 = 0.0;
            if (pseudoPot != null && pseudoPot.isAvairable()) {
                cutoff2 = pseudoPot.getData().getWfcCutoff();
            }

            cutoff = Math.max(cutoff, cutoff2);
        }

        cutoff = Math.max(cutoff, MINIMUM_CUTOFF_WF);

        return cutoff;
    }

    public double getCutoffOfCharge() {
        if (this.cardSpecies == null) {
            return -1.0;
        }

        double cutoff = 0.0;
        boolean hasUspp = false;

        int numElems = this.cardSpecies.numSpecies();
        for (int i = 0; i < numElems; i++) {
            PseudoPotential pseudoPot = this.cardSpecies.getPseudoPotential(i);

            double cutoff2 = 0.0;
            int pseudoType = PseudoData.PSEUDO_TYPE_UNKNOWN;
            if (pseudoPot != null && pseudoPot.isAvairable()) {
                cutoff2 = pseudoPot.getData().getRhoCutoff();
                pseudoType = pseudoPot.getData().getPseudoType();
            }

            cutoff = Math.max(cutoff, cutoff2);

            if (pseudoType == PseudoData.PSEUDO_TYPE_US || pseudoType == PseudoData.PSEUDO_TYPE_PAW) {
                hasUspp = true;
            }
        }

        double minCutoff = -1.0;
        if (this.nmlSystem != null) {
            QEValue value = this.nmlSystem.getValue("ecutwfc");
            if (value != null) {
                double rate = hasUspp ? 9.0 : 4.0;
                minCutoff = rate * value.getRealValue();
            }
        }

        if (minCutoff <= ZERO) {
            minCutoff = MINIMUM_CUTOFF_CHARGE;
        }

        cutoff = Math.max(cutoff, minCutoff);

        return cutoff;
    }
}
