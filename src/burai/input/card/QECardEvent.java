/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input.card;

public class QECardEvent {

    public static final int EVENT_TYPE_NULL = 0;

    public static final int EVENT_TYPE_UNIT_CHANGED = 1;
    public static final int EVENT_TYPE_SPECIES_CHANGED = 2;
    public static final int EVENT_TYPE_SPECIES_ADDED = 3;
    public static final int EVENT_TYPE_SPECIES_REMOVED = 4;
    public static final int EVENT_TYPE_SPECIES_CLEARED = 5;
    public static final int EVENT_TYPE_ATOM_CHANGED = 6;
    public static final int EVENT_TYPE_ATOM_MOVED = 7;
    public static final int EVENT_TYPE_ATOM_ADDED = 8;
    public static final int EVENT_TYPE_ATOM_REMOVED = 9;
    public static final int EVENT_TYPE_ATOM_CLEARED = 10;
    public static final int EVENT_TYPE_KPOINT_CHANGED = 11;
    public static final int EVENT_TYPE_KPOINT_ADDED = 12;
    public static final int EVENT_TYPE_KPOINT_REMOVED = 13;
    public static final int EVENT_TYPE_KPOINT_CLEARED = 14;
    public static final int EVENT_TYPE_KGRID_CHANGED = 15;

    private QECard card;

    private int eventType;

    private int speciesIndex;

    private int atomIndex;

    private int kpointIndex;

    public QECardEvent(QECard card) {
        if (card == null) {
            throw new IllegalArgumentException("card is null.");
        }

        this.card = card;
        this.eventType = EVENT_TYPE_NULL;
        this.atomIndex = -1;
        this.kpointIndex = -1;
    }

    public QECard getCard() {
        return this.card;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public int getEventType() {
        return this.eventType;
    }

    public void setSpeciesIndex(int speciesIndex) {
        this.speciesIndex = speciesIndex;
    }

    public int getSpeciesIndex() {
        return this.speciesIndex;
    }

    public void setAtomIndex(int atomIndex) {
        this.atomIndex = atomIndex;
    }

    public int getAtomIndex() {
        return this.atomIndex;
    }

    public void setKPointIndex(int kpointIndex) {
        this.kpointIndex = kpointIndex;
    }

    public int getKPointIndex() {
        return this.kpointIndex;
    }
}
