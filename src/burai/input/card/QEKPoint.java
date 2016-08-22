/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input.card;

import burai.com.math.Calculator;

public class QEKPoint {

    private double x;

    private double y;

    private double z;

    private double weight;

    private String letter;

    public QEKPoint(String letter, double x, double y, double z, double weight) {
        if (weight < 0.0) {
            throw new IllegalArgumentException("weight is negative.");
        }

        this.x = x;
        this.y = y;
        this.z = z;
        this.weight = weight;
        this.letter = letter == null ? null : letter.trim();
    }

    public QEKPoint(double x, double y, double z, double weight) {
        this(null, x, y, z, weight);
    }

    public QEKPoint(String letter, double weight) {
        this(letter, 0.0, 0.0, 0.0, weight);

        if (this.letter == null || this.letter.isEmpty()) {
            throw new IllegalArgumentException("letter is empty.");
        }
    }

    public QEKPoint(String line) {
        if (line == null) {
            throw new IllegalArgumentException("line is null.");
        }

        String[] subLines = line.trim().split("[\\s,]+");
        if (subLines == null || subLines.length < 1) {
            throw new IllegalArgumentException("line is incorrect: " + line.trim());
        }

        boolean letterMode = false;
        try {
            Calculator.expr(subLines[0]);
        } catch (Exception e) {
            letterMode = true;
        }

        if (!letterMode) {
            try {
                this.x = Calculator.expr(subLines[0]);
                this.y = Calculator.expr(subLines[1]);
                this.z = Calculator.expr(subLines[2]);
                this.weight = Calculator.expr(subLines[3]);
                this.letter = null;
            } catch (Exception e) {
                throw new IllegalArgumentException("line is incorrect: " + line.trim());
            }

        } else {
            try {
                this.x = 0.0;
                this.y = 0.0;
                this.z = 0.0;
                this.weight = Calculator.expr(subLines[1]);
                this.letter = subLines[0].trim();
            } catch (Exception e) {
                throw new IllegalArgumentException("line is incorrect: " + line.trim());
            }
        }
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public double getWeight() {
        return this.weight;
    }

    public String getLetter() {
        return this.letter;
    }

    public boolean hasLetter() {
        return !(this.letter == null || this.letter.isEmpty());
    }

    public String toString(boolean asInteger) {
        int number = 0;
        if (asInteger) {
            number = (int) (Math.rint(this.weight) + 0.1);
        }

        if (!this.hasLetter()) {
            if (asInteger) {
                return String.format("%10.6f %10.6f %10.6f  %d", this.x, this.y, this.z, number);
            } else {
                return String.format("%10.6f %10.6f %10.6f %10.6f", this.x, this.y, this.z, this.weight);
            }

        } else {
            if (asInteger) {
                return String.format("%-5s  %d", this.letter, number);
            } else {
                return String.format("%-5s %10.6f", this.letter, this.weight);
            }
        }
    }

    @Override
    public String toString() {
        return this.toString(false);
    }
}
