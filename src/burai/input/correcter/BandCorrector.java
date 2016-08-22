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
import burai.input.card.QEAtomicPositions;
import burai.input.card.QEAtomicSpecies;
import burai.input.card.QECard;
import burai.pseudo.PseudoPotential;

public class BandCorrector {

    private QEInput input;

    private QEAtomicSpecies cardSpecies;

    private QEAtomicPositions cardPositions;

    public BandCorrector(QEInput input) {
        if (input == null) {
            throw new IllegalArgumentException("input is null.");
        }

        this.input = input;

        QECard card = null;

        card = this.input.getCard(QEAtomicSpecies.CARD_NAME);
        this.cardSpecies = (card != null && card instanceof QEAtomicSpecies) ? ((QEAtomicSpecies) card) : null;

        card = this.input.getCard(QEAtomicPositions.CARD_NAME);
        this.cardPositions = (card != null && card instanceof QEAtomicPositions) ? ((QEAtomicPositions) card) : null;
    }

    public boolean isAvailable() {
        if (this.cardSpecies == null || this.cardSpecies.numSpecies() < 1) {
            return false;
        }

        if (this.cardPositions == null || this.cardPositions.numPositions() < 1) {
            return false;
        }

        return true;
    }

    public int getNumBands() {
        if (this.cardSpecies == null || this.cardPositions == null) {
            return 0;
        }

        int numElems = this.cardSpecies.numSpecies();
        int[] nbandList = new int[numElems];

        for (int i = 0; i < numElems; i++) {
            String name = this.cardSpecies.getLabel(i);
            if (name == null || name.trim().isEmpty()) {
                nbandList[i] = 0;
                continue;
            }

            int valence = Math.max(0, ElementUtil.getValence(name));

            int numOrb = 0;
            if (ElementUtil.isLanthanoid(name) || ElementUtil.isActinoid(name)) {
                numOrb = 1 + 3 + 5 + 7;
            } else if (ElementUtil.isTransitionMetal(name)) {
                numOrb = 1 + 3 + 5;
            } else {
                numOrb = 1 + 3;
            }

            PseudoPotential pseudoPot = this.cardSpecies.getPseudoPotential(i);
            if (pseudoPot == null) {
                nbandList[i] = numOrb;
                continue;
            }

            double zValence = pseudoPot.getData().getZValence();
            double dValence = zValence - (double) valence;
            double occCore = 0.5 * dValence;
            int numCore = Math.max(0, (int) (occCore + 1.0 - 1.0e-6));
            nbandList[i] = numOrb + numCore;
        }

        int nbands = 0;
        int numAtoms = this.cardPositions.numPositions();
        for (int i = 0; i < numAtoms; i++) {
            String name = this.cardPositions.getLabel(i);
            int index = this.cardSpecies.indexOfSpecies(name);
            nbands += nbandList[index];
        }

        return nbands;
    }
}
