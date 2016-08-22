/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input.namelist;


public class QEValueChecker {

    private String data;

    public QEValueChecker(String data) {
        this.data = data;
    }

    public boolean isInteger() {
        if (this.data == null) {
            return false;
        }

        try {
            Integer.parseInt(this.data);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public int getInteger() {
        return Integer.parseInt(this.data);
    }

    public boolean isReal() {
        if (this.data == null) {
            return false;
        }

        try {
            Double.parseDouble(this.data.replace('d', 'e').replace('D', 'E'));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public double getReal() {
        return Double.parseDouble(this.data.replace('d', 'e').replace('D', 'E'));
    }

    public boolean isLogical() {
        if (this.isTrue()) {
            return true;
        }

        if (this.isFalse()) {
            return true;
        }

        return false;
    }

    public boolean getLogical() {
        if(isTrue()) {
            return true;
        }

        if(isFalse()) {
            return false;
        }

        throw new IllegalArgumentException(this.data + " is not logical.");
    }

    private boolean isTrue() {
        if (this.data == null) {
            return false;
        }

        String data2 = this.data.trim().toUpperCase();

        if ("T".equals(data2)) {
            return true;
        }
        if (".T.".equals(data2)) {
            return true;
        }
        if ("TRUE".equals(data2)) {
            return true;
        }
        if (".TRUE.".equals(data2)) {
            return true;
        }

        return false;
    }

    private boolean isFalse() {
        if (this.data == null) {
            return false;
        }

        String data2 = this.data.trim().toUpperCase();

        if ("F".equals(data2)) {
            return true;
        }
        if (".F.".equals(data2)) {
            return true;
        }
        if ("FALSE".equals(data2)) {
            return true;
        }
        if (".FALSE.".equals(data2)) {
            return true;
        }

        return false;
    }
}
