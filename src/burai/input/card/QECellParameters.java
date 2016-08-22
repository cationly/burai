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
import java.util.List;

import burai.com.math.Calculator;

public class QECellParameters extends QECard {

    public static final String CARD_NAME = "CELL_PARAMETERS";

    private static final String OPTION_NONE = "none";
    private static final String OPTION_ALAT = "alat";
    private static final String OPTION_BOHR = "bohr";
    private static final String OPTION_ANGSTROM = "angstrom";

    private double[] vector1;
    private double[] vector2;
    private double[] vector3;

    private boolean skippingListeners;

    public QECellParameters() {
        super(CARD_NAME);

        this.vector1 = new double[] { 1.0, 0.0, 0.0 };
        this.vector2 = new double[] { 0.0, 1.0, 0.0 };
        this.vector3 = new double[] { 0.0, 0.0, 1.0 };
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

    public double[] getVector1() {
        double[] vecOut = new double[3];
        vecOut[0] = this.vector1[0];
        vecOut[1] = this.vector1[1];
        vecOut[2] = this.vector1[2];

        return vecOut;
    }

    public double[] getVector2() {
        double[] vecOut = new double[3];
        vecOut[0] = this.vector2[0];
        vecOut[1] = this.vector2[1];
        vecOut[2] = this.vector2[2];

        return vecOut;
    }

    public double[] getVector3() {
        double[] vecOut = new double[3];
        vecOut[0] = this.vector3[0];
        vecOut[1] = this.vector3[1];
        vecOut[2] = this.vector3[2];

        return vecOut;
    }

    public void setVector(int index, double[] vector) {
        if (index < 1 || index > 3) {
            return;
        }

        if (vector == null || vector.length < 3) {
            return;
        }

        if (index == 1) {
            this.vector1[0] = vector[0];
            this.vector1[1] = vector[1];
            this.vector1[2] = vector[2];

        } else if (index == 2) {
            this.vector2[0] = vector[0];
            this.vector2[1] = vector[1];
            this.vector2[2] = vector[2];

        } else if (index == 3) {
            this.vector3[0] = vector[0];
            this.vector3[1] = vector[1];
            this.vector3[2] = vector[2];
        }

        if ((!this.skippingListeners) && this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
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

    private void checkOption() throws IOException {
        if (this.option == null || this.option.isEmpty()) {
            this.setDefaultOption();
        }

        this.option = this.option.trim().toLowerCase();

        if (OPTION_NONE.equals(this.option)) {
            // NOP
        } else if (OPTION_ALAT.equals(this.option)) {
            // NOP
        } else if (OPTION_BOHR.equals(this.option)) {
            // NOP
        } else if (OPTION_ANGSTROM.equals(this.option)) {
            // NOP
        } else {
            this.setDefaultOption();
        }
    }

    private void setDefaultOption() {
        this.option = OPTION_NONE;
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

        for (int i = 0; i < 3; i++) {
            int iLine = startingLine + i;
            if (iLine >= lines.size()) {
                throw new IOException("there are not enough lines in reading " + this.cardName);
            }

            String line = lines.get(iLine);
            String[] subLines = line.split("[\\s,]+");
            if (subLines == null || subLines.length < 3) {
                throw new IOException("incorrect line in reading " + this.cardName + ": " + line);
            }

            double[] vector = { 0.0, 0.0, 0.0 };

            try {
                vector[0] = Calculator.expr(subLines[0]);
                vector[1] = Calculator.expr(subLines[1]);
                vector[2] = Calculator.expr(subLines[2]);
            } catch (NumberFormatException e) {
                throw new IOException("incorrect data in reading " + this.cardName + ": " + line);
            }

            int index = i + 1;
            if (index == 1) {
                this.vector1[0] = vector[0];
                this.vector1[1] = vector[1];
                this.vector1[2] = vector[2];

            } else if (index == 2) {
                this.vector2[0] = vector[0];
                this.vector2[1] = vector[1];
                this.vector2[2] = vector[2];

            } else if (index == 3) {
                this.vector3[0] = vector[0];
                this.vector3[1] = vector[1];
                this.vector3[2] = vector[2];
            }
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
        if (!(card instanceof QECellParameters)) {
            throw new IllegalArgumentException("card is incorrect.");
        }

        QECellParameters cellParameters = (QECellParameters) card;

        cellParameters.option = this.option;

        if (cellParameters.vector1 == null) {
            cellParameters.vector1 = new double[3];
        }
        cellParameters.vector1[0] = this.vector1[0];
        cellParameters.vector1[1] = this.vector1[1];
        cellParameters.vector1[2] = this.vector1[2];

        if (cellParameters.vector2 == null) {
            cellParameters.vector2 = new double[3];
        }
        cellParameters.vector2[0] = this.vector2[0];
        cellParameters.vector2[1] = this.vector2[1];
        cellParameters.vector2[2] = this.vector2[2];

        if (cellParameters.vector3 == null) {
            cellParameters.vector3 = new double[3];
        }
        cellParameters.vector3[0] = this.vector3[0];
        cellParameters.vector3[1] = this.vector3[1];
        cellParameters.vector3[2] = this.vector3[2];

        if ((!cellParameters.skippingListeners) && cellParameters.listeners != null) {
            QECardEvent event = new QECardEvent(cellParameters);
            for (QECardListener listener : cellParameters.listeners) {
                if (listener != null) {
                    listener.onCardChanged(event);
                }
            }
        }
    }

    @Override
    public void clear() {
        this.setDefaultOption();

        if (this.vector1 == null) {
            this.vector1 = new double[3];
        }
        this.vector1[0] = 1.0;
        this.vector1[1] = 0.0;
        this.vector1[2] = 0.0;

        if (this.vector2 == null) {
            this.vector2 = new double[3];
        }
        this.vector2[0] = 0.0;
        this.vector2[1] = 1.0;
        this.vector2[2] = 0.0;

        if (this.vector3 == null) {
            this.vector3 = new double[3];
        }
        this.vector3[0] = 0.0;
        this.vector3[1] = 0.0;
        this.vector3[2] = 1.0;

        if ((!this.skippingListeners) && this.listeners != null) {
            QECardEvent event = new QECardEvent(this);
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
        String strFormat = "%12.6f %12.6f %12.6f%n";
        str = str + String.format(strFormat, this.vector1[0], this.vector1[1], this.vector1[2]);
        str = str + String.format(strFormat, this.vector2[0], this.vector2[1], this.vector2[2]);
        str = str + String.format(strFormat, this.vector3[0], this.vector3[1], this.vector3[2]);
        return str;
    }
}
