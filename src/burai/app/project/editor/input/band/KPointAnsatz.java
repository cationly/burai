/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.band;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import burai.com.math.Calculator;

public class KPointAnsatz {

    private IntegerProperty index;
    private StringProperty symbol;
    private StringProperty kx;
    private StringProperty ky;
    private StringProperty kz;
    private StringProperty nk;

    public KPointAnsatz(int index) {
        this.setIndex(index);
        this.symbol = null;
        this.kx = null;
        this.ky = null;
        this.kz = null;
        this.nk = null;
    }

    public void setIndex(int value) {
        this.indexProperty().set(value);
    }

    public int getIndex() {
        return this.indexProperty().get();
    }

    public IntegerProperty indexProperty() {
        if (this.index == null) {
            this.index = new SimpleIntegerProperty(this, "index");
        }

        return this.index;
    }

    public void setSymbol(String value) {
        this.symbolProperty().set(value);
    }

    public String getSymbol() {
        return this.symbolProperty().get();
    }

    public StringProperty symbolProperty() {
        if (this.symbol == null) {
            this.symbol = new SimpleStringProperty(this, "symbol");
        }

        return this.symbol;
    }

    public void setKx(String value) {
        this.kxProperty().set(value);
    }

    public void setKx(double value) {
        this.kxProperty().set(String.format("%8.4f", value));
    }

    public String getKx() {
        return this.kxProperty().get();
    }

    public double getKxValue() throws RuntimeException {
        double value = 0.0;
        try {
            value = Calculator.expr(this.kxProperty().get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    public StringProperty kxProperty() {
        if (this.kx == null) {
            this.kx = new SimpleStringProperty(this, "kx");
        }

        return this.kx;
    }

    public void setKy(String value) {
        this.kyProperty().set(value);
    }

    public void setKy(double value) {
        this.kyProperty().set(String.format("%8.4f", value));
    }

    public String getKy() {
        return this.kyProperty().get();
    }

    public double getKyValue() throws RuntimeException {
        double value = 0.0;
        try {
            value = Calculator.expr(this.kyProperty().get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    public StringProperty kyProperty() {
        if (this.ky == null) {
            this.ky = new SimpleStringProperty(this, "ky");
        }

        return this.ky;
    }

    public void setKz(String value) {
        this.kzProperty().set(value);
    }

    public void setKz(double value) {
        this.kzProperty().set(String.format("%8.4f", value));
    }

    public String getKz() {
        return this.kzProperty().get();
    }

    public double getKzValue() throws RuntimeException {
        double value = 0.0;
        try {
            value = Calculator.expr(this.kzProperty().get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    public StringProperty kzProperty() {
        if (this.kz == null) {
            this.kz = new SimpleStringProperty(this, "kz");
        }

        return this.kz;
    }

    public void setNk(String value) {
        this.nkProperty().set(value);
    }

    public void setNk(int value) {
        this.nkProperty().set(Integer.toString(value));
    }

    public String getNk() {
        return this.nkProperty().get();
    }

    public int getNkValue() throws RuntimeException {
        int value = 0;
        try {
            value = Integer.parseInt(this.nkProperty().get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    public StringProperty nkProperty() {
        if (this.nk == null) {
            this.nk = new SimpleStringProperty(this, "nk");
        }

        return this.nk;
    }

    @Override
    public String toString() {
        String str = "";
        str = str + this.getIndex() + " [";
        str = str + this.getSymbol() + " ";
        str = str + this.getKx() + " ";
        str = str + this.getKy() + " ";
        str = str + this.getKz() + " ";
        str = str + this.getNk() + "]";
        return str;
    }

    @Override
    public int hashCode() {
        return this.getSymbol() == null ? 0 : this.getSymbol().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
