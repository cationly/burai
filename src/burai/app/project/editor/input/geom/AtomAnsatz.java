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

public class AtomAnsatz {

    private static final String CHARACTER_SPLIT = "#";
    private static final String CHARACTER_SPLIT_REGEX = "[\\s]*" + CHARACTER_SPLIT;
    private static final String CHARACTER_MOBILE = "mobile";
    private static final String CHARACTER_FIXED = "fixed";

    public static String extractPosition(String item) {
        if (item == null || item.trim().isEmpty()) {
            return null;
        }

        String[] subItem = item.trim().split(CHARACTER_SPLIT_REGEX);
        if (subItem == null || subItem.length < 1) {
            return null;
        }

        return subItem[0];
    }

    public static boolean extractMobile(String item) {
        String strMobile = extractMobileString(item);
        if (strMobile == null) {
            return true;
        }

        if (strMobile.equals(CHARACTER_MOBILE)) {
            return true;
        } else if (strMobile.equals(CHARACTER_FIXED)) {
            return false;
        }

        return true;
    }

    public static boolean containsMobile(String item) {
        String strMobile = extractMobileString(item);
        if (strMobile == null) {
            return false;
        }

        if (strMobile.equals(CHARACTER_MOBILE)) {
            return true;
        } else if (strMobile.equals(CHARACTER_FIXED)) {
            return true;
        }

        return false;
    }

    private static String extractMobileString(String item) {
        if (item == null || item.trim().isEmpty()) {
            return null;
        }

        String[] subItem = item.trim().split(CHARACTER_SPLIT_REGEX);
        if (subItem == null || subItem.length < 2) {
            return null;
        }

        return subItem[1];
    }

    private IntegerProperty index;
    private StringProperty element;
    private StringProperty x;
    private StringProperty y;
    private StringProperty z;

    public AtomAnsatz(int index) {
        this.setIndex(index);
        this.element = null;
        this.x = null;
        this.y = null;
        this.z = null;
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

    public void setElement(String value) {
        this.elementProperty().set(value);
    }

    public String getElement() {
        return this.elementProperty().get();
    }

    public StringProperty elementProperty() {
        if (this.element == null) {
            this.element = new SimpleStringProperty(this, "element");
        }

        return this.element;
    }

    public void setX(String value) {
        this.xProperty().set(value);
    }

    public String getX() {
        return this.xProperty().get();
    }

    public StringProperty xProperty() {
        if (this.x == null) {
            this.x = new SimpleStringProperty(this, "x");
        }

        return this.x;
    }

    public void setX(double value, boolean mobile) {
        this.setX(this.dataToString(value, mobile));
    }

    public void setXMobile(boolean mobile) {
        String strX = this.getX();
        if (strX == null || strX.trim().isEmpty()) {
            return;
        }

        String[] subX = strX.trim().split(CHARACTER_SPLIT_REGEX);
        if (subX == null || subX.length < 1) {
            return;
        }

        String strMobile = null;
        if (mobile) {
            strMobile = CHARACTER_MOBILE;
        } else {
            strMobile = CHARACTER_FIXED;
        }

        this.setX(subX[0] + CHARACTER_SPLIT + strMobile);
    }

    public double getXValue() throws RuntimeException {
        double value = 0.0;
        try {
            value = this.stringToValue(this.getX());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    public boolean isXMobile() throws RuntimeException {
        boolean mobile = true;
        try {
            mobile = this.stringToMobile(this.getX());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mobile;
    }

    public boolean isXMobile(boolean defMobile) {
        boolean mobile = true;
        try {
            mobile = this.stringToMobile(this.getX());
        } catch (Exception e) {
            mobile = defMobile;
        }
        return mobile;
    }

    public void setY(String value) {
        this.yProperty().set(value);
    }

    public String getY() {
        return this.yProperty().get();
    }

    public StringProperty yProperty() {
        if (this.y == null) {
            this.y = new SimpleStringProperty(this, "y");
        }

        return this.y;
    }

    public void setY(double value, boolean mobile) {
        this.setY(this.dataToString(value, mobile));
    }

    public void setYMobile(boolean mobile) {
        String strY = this.getY();
        if (strY == null || strY.trim().isEmpty()) {
            return;
        }

        String[] subY = strY.trim().split(CHARACTER_SPLIT_REGEX);
        if (subY == null || subY.length < 1) {
            return;
        }

        String strMobile = null;
        if (mobile) {
            strMobile = CHARACTER_MOBILE;
        } else {
            strMobile = CHARACTER_FIXED;
        }

        this.setY(subY[0] + CHARACTER_SPLIT + strMobile);
    }

    public double getYValue() throws RuntimeException {
        double value = 0.0;
        try {
            value = this.stringToValue(this.getY());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    public boolean isYMobile() throws RuntimeException {
        boolean mobile = true;
        try {
            mobile = this.stringToMobile(this.getY());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mobile;
    }

    public boolean isYMobile(boolean defMobile) {
        boolean mobile = true;
        try {
            mobile = this.stringToMobile(this.getY());
        } catch (Exception e) {
            mobile = defMobile;
        }
        return mobile;
    }

    public void setZ(String value) {
        this.zProperty().set(value);
    }

    public String getZ() {
        return this.zProperty().get();
    }

    public StringProperty zProperty() {
        if (this.z == null) {
            this.z = new SimpleStringProperty(this, "z");
        }

        return this.z;
    }

    public void setZ(double value, boolean mobile) {
        this.setZ(this.dataToString(value, mobile));
    }

    public void setZMobile(boolean mobile) {
        String strZ = this.getZ();
        if (strZ == null || strZ.trim().isEmpty()) {
            return;
        }

        String[] subZ = strZ.trim().split(CHARACTER_SPLIT_REGEX);
        if (subZ == null || subZ.length < 1) {
            return;
        }

        String strMobile = null;
        if (mobile) {
            strMobile = CHARACTER_MOBILE;
        } else {
            strMobile = CHARACTER_FIXED;
        }

        this.setZ(subZ[0] + CHARACTER_SPLIT + strMobile);
    }

    public double getZValue() throws RuntimeException {
        double value = 0.0;
        try {
            value = this.stringToValue(this.getZ());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    public boolean isZMobile() throws RuntimeException {
        boolean mobile = true;
        try {
            mobile = this.stringToMobile(this.getZ());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mobile;
    }

    public boolean isZMobile(boolean defMobile) {
        boolean mobile = true;
        try {
            mobile = this.stringToMobile(this.getZ());
        } catch (Exception e) {
            mobile = defMobile;
        }
        return mobile;
    }

    private String dataToString(double value) {
        String strValue = String.format("%10.6f", value);
        return strValue;
    }

    private String dataToString(double value, boolean mobile) {
        String strValue = this.dataToString(value);
        String strMobile = mobile ? CHARACTER_MOBILE : CHARACTER_FIXED;
        return strValue + CHARACTER_SPLIT + strMobile;
    }

    private double stringToValue(String str) throws Exception {
        String[] subStr = str.trim().split(CHARACTER_SPLIT_REGEX);
        return Calculator.expr(subStr[0]);
    }

    private boolean stringToMobile(String str) throws Exception {
        String[] subStr = str.trim().split(CHARACTER_SPLIT_REGEX);
        if (CHARACTER_MOBILE.equals(subStr[1])) {
            return true;
        } else if (CHARACTER_FIXED.equals(subStr[1])) {
            return false;
        }

        throw new RuntimeException("incorrect mobile character.");
    }

    @Override
    public String toString() {
        String str = "";
        str = str + this.getIndex() + " [";
        str = str + this.getElement() + " ";
        str = str + this.getX() + " ";
        str = str + this.getY() + " ";
        str = str + this.getZ() + "]";
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
