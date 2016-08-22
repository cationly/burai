/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input.card;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class QECard {

    protected String cardName;

    protected String option;

    protected List<QECardListener> listeners;

    private boolean protedtedToCopy;

    protected QECard(String cardName) {
        if (cardName == null || cardName.isEmpty()) {
            throw new IllegalArgumentException("name of card is null or empty.");
        }

        this.cardName = cardName;
        this.option = null;
        this.listeners = null;
        this.protedtedToCopy = false;
    }

    public void addListener(QECardListener listener) {
        if (listener == null) {
            return;
        }

        if (this.listeners == null) {
            this.listeners = new ArrayList<QECardListener>();
        }

        this.listeners.add(listener);
    }

    public String getName() {
        return this.cardName;
    }

    public String getOption() {
        return this.option;
    }

    public void setProtectedToCopy(boolean protedtedToCopy) {
        this.protedtedToCopy = protedtedToCopy;
    }

    protected int readUptoMyCard(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("lines is null or empty.");
        }

        int index = -1;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            line = line.replace('(', ' ');
            line = line.replace(')', ' ');
            line = line.replace('{', ' ');
            line = line.replace('}', ' ');
            line = line.replace('[', ' ');
            line = line.replace(']', ' ');

            String[] subLines = line.split("[\\s,]+");
            if (subLines == null || subLines.length < 1) {
                continue;
            }

            if (this.cardName != null && this.cardName.equals(subLines[0])) {
                index = i + 1;
                if (subLines.length > 1) {
                    this.option = subLines[1];
                    if (this.option != null) {
                        this.option = this.option.trim().toLowerCase();
                    }
                }
                break;
            }
        }

        return index;
    }

    public abstract boolean read(List<String> lines) throws IOException;

    protected abstract void copyToCard(QECard card);

    public void copyTo(QECard card) {
        this.copyTo(card, true);
    }

    public void copyTo(QECard card, boolean protect) {
        if (protect) {
            if (card != null && card.protedtedToCopy) {
                return;
            }
        }

        this.copyToCard(card);
    }

    public abstract void clear();

    @Override
    public abstract String toString();

    @Override
    public int hashCode() {
        return this.cardName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (this.getClass() != obj.getClass()) {
            return false;
        }

        QECard other = (QECard) obj;
        return this.cardName.equals(other.cardName);
    }
}
