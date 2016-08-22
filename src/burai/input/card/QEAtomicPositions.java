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

public class QEAtomicPositions extends QECard {

    public static final String CARD_NAME = "ATOMIC_POSITIONS";

    private static final String OPTION_ALAT = "alat";
    private static final String OPTION_BOHR = "bohr";
    private static final String OPTION_ANGSTROM = "angstrom";
    private static final String OPTION_CRYSTAL = "crystal";

    private List<String> labels;
    private List<double[]> positions;
    private List<boolean[]> mobiles;

    private boolean skippingListeners;

    public QEAtomicPositions() {
        super(CARD_NAME);

        this.labels = new ArrayList<String>();
        this.positions = new ArrayList<double[]>();
        this.mobiles = new ArrayList<boolean[]>();
        this.skippingListeners = false;

        this.setDefaultOption();
    }

    public void stopListeners() {
        this.skippingListeners = true;
    }

    public void restartListeners() {
        this.skippingListeners = false;

        if (this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    public int numPositions() {
        return this.labels.size();
    }

    public String getLabel(int i) {
        if (i < 0 || i >= this.labels.size()) {
            throw new IllegalArgumentException("index of label is incorrect.");
        }

        String label = this.labels.get(i);

        return label;
    }

    public double[] getPosition(int i) {
        if (i < 0 || i >= this.positions.size()) {
            throw new IllegalArgumentException("index of position is incorrect.");
        }

        double[] position = this.positions.get(i);

        double[] posOut = new double[3];
        posOut[0] = position[0];
        posOut[1] = position[1];
        posOut[2] = position[2];

        return posOut;
    }

    public boolean[] getMobile(int i) {
        if (i < 0 || i >= this.mobiles.size()) {
            throw new IllegalArgumentException("index of mobile is incorrect.");
        }

        boolean[] mobile = this.mobiles.get(i);

        boolean[] mobOut = new boolean[3];
        mobOut[0] = mobile[0];
        mobOut[1] = mobile[1];
        mobOut[2] = mobile[2];

        return mobOut;
    }

    public void setLabel(int i, String label) {
        if (i < 0 || i >= this.labels.size()) {
            throw new IllegalArgumentException("index of label is incorrect.");
        }

        if (label == null || label.isEmpty()) {
            return;
        }

        this.labels.set(i, label);

        if ((!this.skippingListeners) && this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_ATOM_CHANGED);
            event.setAtomIndex(i);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    public void setPosition(int i, double[] position) {
        if (i < 0 || i >= this.positions.size()) {
            throw new IllegalArgumentException("index of position is incorrect.");
        }

        if (position == null || position.length < 3) {
            return;
        }

        double[] posIn = new double[3];
        posIn[0] = position[0];
        posIn[1] = position[1];
        posIn[2] = position[2];

        this.positions.set(i, posIn);

        if ((!this.skippingListeners) && this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_ATOM_MOVED);
            event.setAtomIndex(i);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    public void setMobile(int i, boolean[] mobile) {
        if (i < 0 || i >= this.mobiles.size()) {
            throw new IllegalArgumentException("index of mobile is incorrect.");
        }

        if (mobile == null || mobile.length < 3) {
            return;
        }

        boolean[] mobIn = new boolean[3];
        mobIn[0] = mobile[0];
        mobIn[1] = mobile[1];
        mobIn[2] = mobile[2];

        this.mobiles.set(i, mobIn);

        if ((!this.skippingListeners) && this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_ATOM_CHANGED);
            event.setAtomIndex(i);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    public void addPosition(String label, double[] position, boolean[] mobile) {
        if (label == null || label.isEmpty()) {
            return;
        }

        if (position == null || position.length < 3) {
            return;
        }

        if (mobile == null || mobile.length < 3) {
            return;
        }

        double[] posIn = new double[3];
        posIn[0] = position[0];
        posIn[1] = position[1];
        posIn[2] = position[2];

        boolean[] mobIn = new boolean[3];
        mobIn[0] = mobile[0];
        mobIn[1] = mobile[1];
        mobIn[2] = mobile[2];

        this.labels.add(label);
        this.positions.add(posIn);
        this.mobiles.add(mobIn);

        if ((!this.skippingListeners) && this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_ATOM_ADDED);
            event.setAtomIndex(this.labels.size() - 1);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    public void removePosition(int index) {
        if (index < 0 || index >= this.labels.size()) {
            return;
        }

        this.labels.remove(index);
        this.positions.remove(index);
        this.mobiles.remove(index);

        if ((!this.skippingListeners) && this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_ATOM_REMOVED);
            event.setAtomIndex(index);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    public void setAlat() {
        this.setOption(OPTION_ALAT);
    }

    public void setBohr() {
        this.setOption(OPTION_BOHR);
    }

    public void setAngstrom() {
        this.setOption(OPTION_ANGSTROM);
    }

    public void setCrystal() {
        this.setOption(OPTION_CRYSTAL);
    }

    private void setOption(String option) {
        this.option = option;

        if ((!this.skippingListeners) && this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_UNIT_CHANGED);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    public boolean isAlat() {
        return OPTION_ALAT.equals(this.option);
    }

    public boolean isBohr() {
        return OPTION_BOHR.equals(this.option);
    }

    public boolean isAngstrom() {
        return OPTION_ANGSTROM.equals(this.option);
    }

    public boolean isCrystal() {
        return OPTION_CRYSTAL.equals(this.option);
    }

    private void checkOption() {
        if (this.option == null || this.option.isEmpty()) {
            this.setDefaultOption();
        }

        this.option = this.option.trim().toLowerCase();

        if (OPTION_ALAT.equals(this.option)) {
            // NOP
        } else if (OPTION_BOHR.equals(this.option)) {
            // NOP
        } else if (OPTION_ANGSTROM.equals(this.option)) {
            // NOP
        } else if (OPTION_CRYSTAL.equals(this.option)) {
            // NOP
        } else {
            this.setDefaultOption();
        }
    }

    private void setDefaultOption() {
        this.option = OPTION_ALAT;
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

        this.checkOption();

        for (int i = startingLine; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] subLines = line.split("[\\s,]+");
            if (subLines == null || subLines.length < 4) {
                //throw new IOException("incorrect line in reading " + this.cardName + ": " + line);
                break;
            }

            String label = null;
            double[] position = { 0.0, 0.0, 0.0 };
            int[] intMobile = { 1, 1, 1 };

            try {
                if (subLines.length > 3) {
                    label = subLines[0];
                    position[0] = Calculator.expr(subLines[1]);
                    position[1] = Calculator.expr(subLines[2]);
                    position[2] = Calculator.expr(subLines[3]);
                }

                if (subLines.length > 6) {
                    intMobile[0] = Integer.parseInt(subLines[4]);
                    intMobile[1] = Integer.parseInt(subLines[5]);
                    intMobile[2] = Integer.parseInt(subLines[6]);
                }
            } catch (NumberFormatException e) {
                //throw new IOException("incorrect data in reading " + this.cardName + ": " + line);
                break;
            }

            boolean[] mobile = new boolean[3];
            for (int j = 0; j < mobile.length; j++) {
                mobile[j] = (intMobile[j] > 0);
            }

            this.labels.add(label);
            this.positions.add(position);
            this.mobiles.add(mobile);
        }

        if ((!this.skippingListeners) && this.listeners != null) {
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
        if (!(card instanceof QEAtomicPositions)) {
            throw new IllegalArgumentException("card is incorrect.");
        }

        QEAtomicPositions atomicPositions = (QEAtomicPositions) card;

        atomicPositions.option = this.option;

        if (atomicPositions.labels == null) {
            atomicPositions.labels = new ArrayList<String>();
        } else {
            atomicPositions.labels.clear();
        }
        for (String label : this.labels) {
            atomicPositions.labels.add(label);
        }

        if (atomicPositions.positions == null) {
            atomicPositions.positions = new ArrayList<double[]>();
        } else {
            atomicPositions.positions.clear();
        }
        for (double[] position : this.positions) {
            double[] position_ = { position[0], position[1], position[2] };
            atomicPositions.positions.add(position_);
        }

        if (atomicPositions.mobiles == null) {
            atomicPositions.mobiles = new ArrayList<boolean[]>();
        } else {
            atomicPositions.mobiles.clear();
        }
        for (boolean[] mobile : this.mobiles) {
            boolean[] mobile_ = { mobile[0], mobile[1], mobile[2] };
            atomicPositions.mobiles.add(mobile_);
        }

        if ((!atomicPositions.skippingListeners) && atomicPositions.listeners != null) {
            QECardEvent event = new QECardEvent(atomicPositions);
            for (QECardListener listener : atomicPositions.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    @Override
    public void clear() {
        this.setDefaultOption();

        if (this.labels != null) {
            this.labels.clear();
        }

        if (this.positions != null) {
            this.positions.clear();
        }

        if (this.mobiles != null) {
            this.mobiles.clear();
        }

        if ((!this.skippingListeners) && this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
            event.setEventType(QECardEvent.EVENT_TYPE_ATOM_CLEARED);
            for (QECardListener listener : this.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    @Override
    public String toString() {
        String str = CARD_NAME + " {" + this.option + "}" + System.lineSeparator();

        int numPos = this.numPositions();
        for (int i = 0; i < numPos; i++) {
            String label = this.labels.get(i);
            double[] position = this.positions.get(i);
            boolean[] mobile = this.mobiles.get(i);
            int[] intMobile = { mobile[0] ? 1 : 0, mobile[1] ? 1 : 0, mobile[2] ? 1 : 0 };
            str = str + String.format("%-5s %10.6f %10.6f %10.6f  %d %d %d%n",
                    label, position[0], position[1], position[2], intMobile[0], intMobile[1], intMobile[2]);
        }

        return str;
    }
}
