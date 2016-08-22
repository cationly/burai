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

import burai.com.math.Calculator;
import burai.pseudo.PseudoLibrary;
import burai.pseudo.PseudoPotential;

public class QEAtomicSpecies extends QECard {

    public static final String CARD_NAME = "ATOMIC_SPECIES";

    private static final String PSEUDOPOT_NULL = "unknown";

    private List<String> labels;
    private List<Double> masses;
    private List<String> pseudopots;

    public QEAtomicSpecies() {
        super(CARD_NAME);

        this.labels = new ArrayList<String>();
        this.masses = new ArrayList<Double>();
        this.pseudopots = new ArrayList<String>();
    }

    public int numSpecies() {
        return this.labels.size();
    }

    public String getLabel(int i) {
        if (i < 0 || i >= this.labels.size()) {
            throw new IllegalArgumentException("index of label is incorrect.");
        }

        return this.labels.get(i);
    }

    public double getMass(int i) {
        if (i < 0 || i >= this.masses.size()) {
            throw new IllegalArgumentException("index of mass is incorrect.");
        }

        Double objMass = this.masses.get(i);
        if (objMass == null) {
            return -1.0;
        }

        return objMass.doubleValue();
    }

    public String getPseudoName(int i) {
        if (i < 0 || i >= this.pseudopots.size()) {
            throw new IllegalArgumentException("index of pseudopotential is incorrect.");
        }

        return this.pseudopots.get(i);
    }

    public PseudoPotential getPseudoPotential(int i) {
        String pseudopot = this.getPseudoName(i);
        return pseudopot == null ? null : PseudoLibrary.getInstance().peekPseudoPotential(pseudopot);
    }

    public boolean hasPseudoPotential(int i) {
        if (i < 0 || i >= this.pseudopots.size()) {
            throw new IllegalArgumentException("index of pseudopotential is incorrect.");
        }

        String pseudopot = this.pseudopots.get(i);
        if (pseudopot == null) {
            return false;
        }

        pseudopot = pseudopot.trim();
        if (pseudopot.isEmpty()) {
            return false;
        }

        return !PSEUDOPOT_NULL.equals(pseudopot);
    }

    public boolean hasSpecies(String label) {
        if (label == null || label.isEmpty()) {
            return false;
        }

        return this.labels.contains(label);
    }

    public int indexOfSpecies(String label) {
        if (label == null || label.isEmpty()) {
            return -1;
        }

        return this.labels.indexOf(label);
    }

    public void setLabel(int i, String label) {
        if (i < 0 || i >= this.labels.size()) {
            throw new IllegalArgumentException("index of label is incorrect.");
        }

        if (label == null || label.isEmpty()) {
            return;
        }

        this.labels.set(i, label);

        if (this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_SPECIES_CHANGED);
            event.setSpeciesIndex(i);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    public void setMass(int i, double mass) {
        if (i < 0 || i >= this.masses.size()) {
            throw new IllegalArgumentException("index of mass is incorrect.");
        }

        this.masses.set(i, mass);

        if (this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_SPECIES_CHANGED);
            event.setSpeciesIndex(i);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    public void setPseudoPotential(int i, String pseudopot) {
        if (i < 0 || i >= this.pseudopots.size()) {
            throw new IllegalArgumentException("index of pseudopot is incorrect.");
        }

        if (pseudopot == null || pseudopot.isEmpty()) {
            return;
        }

        this.pseudopots.set(i, pseudopot);

        if (this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_SPECIES_CHANGED);
            event.setSpeciesIndex(i);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    public void addSpecies(String label, double mass, String pseudopot) {
        if (label == null || label.isEmpty()) {
            return;
        }

        String pseudopot2 = pseudopot == null ? null : pseudopot.trim();
        if (pseudopot2 == null || pseudopot2.isEmpty()) {
            pseudopot2 = PSEUDOPOT_NULL;
        }

        if (this.labels.contains(label)) {
            return;
        }

        this.labels.add(label);
        this.masses.add(new Double(mass));
        this.pseudopots.add(pseudopot2);

        if (this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_SPECIES_ADDED);
            event.setSpeciesIndex(this.labels.size() - 1);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    public void removeSpecies(int index) {
        if (index < 0 || index >= this.labels.size()) {
            return;
        }

        this.labels.remove(index);
        this.masses.remove(index);
        this.pseudopots.remove(index);

        if (this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_SPECIES_REMOVED);
            event.setSpeciesIndex(index);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    public void removeSpecies(String label) {
        if (label == null || label.isEmpty()) {
            return;
        }

        int index = this.labels.indexOf(label);
        this.removeSpecies(index);
    }

    @Override
    public boolean read(List<String> lines) throws IOException {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("lines is null or empty.");
        }

        int startingLine = this.readUptoMyCard(lines);
        if (startingLine < 0) {
            return false;
        }

        for (int i = startingLine; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] subLines = line.split("[\\s,]+");
            if (subLines == null || subLines.length < 3) {
                //throw new IOException("incorrect line in reading " + this.cardName + ": " + line);
                break;
            }

            String label = subLines[0];

            double mass = 0.0;
            try {
                mass = Calculator.expr(subLines[1]);
            } catch (NumberFormatException e) {
                //throw new IOException("incorrect mass in reading " + this.cardName + ": " + subLines[1]);
                break;
            }

            String pseudopot = subLines[2];

            this.labels.add(label);
            this.masses.add(mass);
            this.pseudopots.add(pseudopot);
        }

        if (this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }

        return true;
    }

    @Override
    public void copyToCard(QECard card) {
        if (!(card instanceof QEAtomicSpecies)) {
            throw new IllegalArgumentException("card is incorrect.");
        }

        QEAtomicSpecies atomicSpecies = (QEAtomicSpecies) card;

        atomicSpecies.option = this.option;

        if (atomicSpecies.labels == null) {
            atomicSpecies.labels = new ArrayList<String>();
        } else {
            atomicSpecies.labels.clear();
        }
        for (String label : this.labels) {
            atomicSpecies.labels.add(label);
        }

        if (atomicSpecies.masses == null) {
            atomicSpecies.masses = new ArrayList<Double>();
        } else {
            atomicSpecies.masses.clear();
        }
        for (Double mass : this.masses) {
            atomicSpecies.masses.add(mass);
        }

        if (atomicSpecies.pseudopots == null) {
            atomicSpecies.pseudopots = new ArrayList<String>();
        } else {
            atomicSpecies.pseudopots.clear();
        }
        for (String pseudopot : this.pseudopots) {
            atomicSpecies.pseudopots.add(pseudopot);
        }

        if (atomicSpecies.listeners != null) {
            QECardEvent event = new QECardEvent(atomicSpecies);
            for (QECardListener listener : atomicSpecies.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    @Override
    public void clear() {
        this.option = null;

        if (this.labels != null) {
            this.labels.clear();
        }

        if (this.masses != null) {
            this.masses.clear();
        }

        if (this.pseudopots != null) {
            this.pseudopots.clear();
        }

        if (this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_SPECIES_CLEARED);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    @Override
    public String toString() {
        String str = CARD_NAME + System.lineSeparator();

        int numSpec = this.numSpecies();
        for (int i = 0; i < numSpec; i++) {
            String label = this.labels.get(i);
            double mass = this.masses.get(i);
            String pseudopot = this.pseudopots.get(i);
            str = str + String.format("%-5s %9.5f  %s%n", label, mass, pseudopot);
        }

        return str;
    }
}
