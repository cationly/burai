/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.geom;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import burai.com.math.Calculator;

public class ElementAnsatz {

    private IntegerProperty index;
    private StringProperty name;
    private StringProperty mass;
    private StringProperty pseudo;

    public ElementAnsatz(int index) {
        this.setIndex(index);
        this.name = null;
        this.mass = null;
        this.pseudo = null;
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

    public void setName(String value) {
        this.nameProperty().set(value);
    }

    public String getName() {
        return this.nameProperty().get();
    }

    public StringProperty nameProperty() {
        if (this.name == null) {
            this.name = new SimpleStringProperty(this, "name");
        }

        return this.name;
    }

    public void setMass(String value) {
        this.massProperty().set(value);
    }

    public void setMass(double value) {
        this.massProperty().set(String.format("%9.5f", value));
    }

    public String getMass() {
        return this.massProperty().get();
    }

    public double getMassValue() throws RuntimeException {
        double value = 0.0;
        try {
            value = Calculator.expr(this.massProperty().get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    public StringProperty massProperty() {
        if (this.mass == null) {
            this.mass = new SimpleStringProperty(this, "mass");
        }

        return this.mass;
    }

    public void setPseudo(String value) {
        this.pseudoProperty().set(value);
    }

    public String getPseudo() {
        return this.pseudoProperty().get();
    }

    public StringProperty pseudoProperty() {
        if (this.pseudo == null) {
            this.pseudo = new SimpleStringProperty(this, "pseudo");
        }

        return this.pseudo;
    }

    @Override
    public String toString() {
        String str = "";
        str = str + this.getIndex() + " [";
        str = str + this.getName() + " ";
        str = str + this.getMass() + " ";
        str = str + this.getPseudo() + "]";
        return str;
    }

    @Override
    public int hashCode() {
        return this.getIndex();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
