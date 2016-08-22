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
import burai.input.card.QEAtomicPositions;
import burai.input.card.QEAtomicSpecies;
import burai.input.card.QECard;
import burai.input.card.QECellParameters;
import burai.input.card.QEKPoints;
import burai.input.namelist.QENamelist;

public abstract class QEInputCorrecter {

    protected QEInput input;

    protected QENamelist nmlControl;
    protected QENamelist nmlSystem;
    protected QENamelist nmlElectrons;
    protected QENamelist nmlIons;
    protected QENamelist nmlCell;
    protected QENamelist nmlDos;
    protected QENamelist nmlProjwfc;
    protected QENamelist nmlBands;

    protected QEAtomicSpecies cardSpecies;
    protected QEAtomicPositions cardPositions;
    protected QEKPoints cardKPoints;
    protected QECellParameters cardCell;

    protected QEInputCorrecter(QEInput input) {
        if (input == null) {
            throw new IllegalArgumentException("input is null.");
        }

        this.input = input;

        this.nmlControl = this.input.getNamelist(QEInput.NAMELIST_CONTROL);
        this.nmlSystem = this.input.getNamelist(QEInput.NAMELIST_SYSTEM);
        this.nmlElectrons = this.input.getNamelist(QEInput.NAMELIST_ELECTRONS);
        this.nmlIons = this.input.getNamelist(QEInput.NAMELIST_IONS);
        this.nmlCell = this.input.getNamelist(QEInput.NAMELIST_CELL);
        this.nmlDos = this.input.getNamelist(QEInput.NAMELIST_DOS);
        this.nmlProjwfc = this.input.getNamelist(QEInput.NAMELIST_PROJWFC);
        this.nmlBands = this.input.getNamelist(QEInput.NAMELIST_BANDS);

        QECard card = null;
        card = this.input.getCard(QEAtomicSpecies.CARD_NAME);
        this.cardSpecies = (card != null && card instanceof QEAtomicSpecies) ? ((QEAtomicSpecies) card) : null;
        card = this.input.getCard(QEAtomicPositions.CARD_NAME);
        this.cardPositions = (card != null && card instanceof QEAtomicPositions) ? ((QEAtomicPositions) card) : null;
        card = this.input.getCard(QEKPoints.CARD_NAME);
        this.cardKPoints = (card != null && card instanceof QEKPoints) ? ((QEKPoints) card) : null;
        card = this.input.getCard(QECellParameters.CARD_NAME);
        this.cardCell = (card != null && card instanceof QECellParameters) ? ((QECellParameters) card) : null;
    }

    public abstract void correctInput();

}
