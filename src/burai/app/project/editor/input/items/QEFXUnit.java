/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.items;

import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import burai.com.consts.Constants;
import burai.com.math.Calculator;
import burai.input.namelist.QEReal;

public class QEFXUnit {

    public static final int UNIT_TYPE_REAL_TIME = 0;
    public static final int UNIT_TYPE_LENGTH_BOHR = 1;
    public static final int UNIT_TYPE_LENGTH_ANGS = 2;
    public static final int UNIT_TYPE_ANGLE = 3;
    public static final int UNIT_TYPE_ENERGY_RY = 4;
    public static final int UNIT_TYPE_ENERGY_HA = 5;
    public static final int UNIT_TYPE_ENERGY_EV = 6;
    public static final int UNIT_TYPE_FORCE_RY = 7;
    public static final int UNIT_TYPE_FORCE_HA = 8;
    public static final int UNIT_TYPE_PRESSURE = 9;
    public static final int UNIT_TYPE_MASS = 10;
    public static final int UNIT_TYPE_TIME_RY = 11;
    public static final int UNIT_TYPE_TIME_HA = 12;
    public static final int UNIT_TYPE_TEMPERATURE = 13;

    private static final String[] ITEMS_REAL_TIME = {
            "sec",
            "min",
            "hour",
            "day",
            "week"
    };

    private static final double[] SCALES_REAL_TIME = {
            1.0, // sec
            60.0, // min
            60.0 * 60.0, // hour
            24.0 * 60.0 * 60.0, // day
            7.0 * 24.0 * 60.0 * 60.0 // week
    };

    private static final String[] ITEMS_LENGTH_BOHR = {
            "Bohr",
            "Angs.",
            "nm"
    };

    private static final double[] SCALES_LENGTH_BOHR = {
            1.0, // Bohr
            1.0 / Constants.BOHR_RADIUS_ANGS, // Angs.
            10.0 / Constants.BOHR_RADIUS_ANGS // nm
    };

    private static final String[] ITEMS_LENGTH_ANGS = {
            "Angs.",
            "Bohr",
            "nm"
    };

    private static final double[] SCALES_LENGTH_ANGS = {
            1.0, // Angs.
            Constants.BOHR_RADIUS_ANGS, // Bohr
            10.0 // nm
    };

    private static final String[] ITEMS_ANGLE = {
            "Degree",
            "Radian",
            "PI"
    };

    private static final double[] SCALES_ANGLE = {
            1.0, // Degree
            180.0 / Math.PI, // Radian
            180.0 // Pi
    };

    private static final String[] ITEMS_ENERGY_RY = {
            "Ry",
            "Ha",
            "eV"
    };

    private static final double[] SCALES_ENERGY_RY = {
            1.0, // Ry
            2.0, // Ha
            1.0 / Constants.RYTOEV // eV
    };

    private static final String[] ITEMS_ENERGY_EV = {
            "eV",
            "Ry",
            "Ha"
    };

    private static final double[] SCALES_ENERGY_EV = {
            1.0, // eV
            Constants.RYTOEV, // Ry
            2.0 * Constants.RYTOEV // Ha
    };

    private static final String[] ITEMS_ENERGY_HA = {
            "Ha",
            "Ry",
            "eV"
    };

    private static final double[] SCALES_ENERGY_HA = {
            1.0, // Ha
            0.5, // Ry
            0.5 / Constants.RYTOEV // eV
    };

    private static final String[] ITEMS_FORCE_RY = {
            "Ry/Bohr",
            "Ha/Bohr",
            "eV/Angs."
    };

    private static final double[] SCALES_FORCE_RY = {
            1.0, // Ry/Bohr
            2.0, // Ha/Bohr
            Constants.BOHR_RADIUS_ANGS / Constants.RYTOEV // eV/Angs.
    };

    private static final String[] ITEMS_FORCE_HA = {
            "Ha/Bohr",
            "Ry/Bohr",
            "eV/Angs."
    };

    private static final double[] SCALES_FORCE_HA = {
            1.0, // Ha/Bohr
            0.5, // Ry/Bohr
            0.5 * Constants.BOHR_RADIUS_ANGS / Constants.RYTOEV // eV/Angs.
    };

    private static final String[] ITEMS_PRESSURE = {
            "kbar",
            "GPa"
    };

    private static final double[] SCALES_PRESSURE = {
            1.0, // kbar
            10.0 // GPa
    };

    private static final String[] ITEMS_MASS = {
            "g/mol"
    };

    private static final double[] SCALES_MASS = {
            1.0 // g/mol
    };

    private static final String[] ITEMS_TIME_RY = {
            "a.u. (Ry)",
            "a.u. (Ha)",
            "ps",
            "fs"
    };

    private static final double[] SCALES_TIME_RY = {
            1.0, // a.u. (Ry)
            0.5, // a.u. (Ha)
            0.5 / Constants.AU_PS, // ps
            0.5 / (Constants.AU_PS * 1000.0) // fs
    };

    private static final String[] ITEMS_TIME_HA = {
            "a.u. (Ha)",
            "a.u. (Ry)",
            "ps",
            "fs"
    };

    private static final double[] SCALES_TIME_HA = {
            1.0, // a.u. (Ha)
            2.0, // a.u. (Ry)
            1.0 / Constants.AU_PS, // ps
            1.0 / (Constants.AU_PS * 1000.0) // fs
    };

    private static final String[] ITEMS_TEMPERATURE = {
            "Kelvin",
            "Centig."
    };

    private static final double[] SCALES_TEMPERATURE = {
            0.0, // Kelvin
            Constants.CENTIGRADE_ZERO // Centigrade
    };

    private ComboBox<String> comboBox;

    private int unitType;

    private Map<String, Double> scaleMap;

    private boolean addingMode;

    public QEFXUnit(ComboBox<String> comboBox, int unitType) {
        if (comboBox == null) {
            throw new IllegalArgumentException("comboBox is null.");
        }

        this.comboBox = comboBox;
        this.unitType = unitType;
        this.scaleMap = new HashMap<String, Double>();
        this.addingMode = false;
        this.setupComboBox();
    }

    public double convertToValue(double text) {
        String item = this.comboBox.getValue();
        double scale = this.scaleMap.get(item);
        double value = text;
        if (this.addingMode) {
            value += scale;
        } else {
            value *= scale;
        }
        return value;
    }

    public String convertToValue(String text) {
        if (text == null) {
            return null;
        }

        String value = null;
        try {
            double dbleText = Calculator.expr(text);
            double dbleValue = this.convertToValue(dbleText);
            value = Double.toString(dbleValue);
        } catch (NumberFormatException e) {
            value = text;
        }

        return value;
    }

    public double convertToText(double value) {
        String item = this.comboBox.getValue();
        double scale = this.scaleMap.get(item);
        double text = value;
        if (this.addingMode) {
            text -= scale;
        } else {
            text /= scale;
        }
        return text;
    }

    public String convertToText(String value) {
        if (value == null) {
            return null;
        }

        String text = null;
        try {
            double dbleValue = Calculator.expr(value);
            double dbleText = this.convertToText(dbleValue);
            text = new QEReal("x", dbleText).getCharacterValue();
        } catch (NumberFormatException e) {
            text = value;
        }

        return text;
    }

    private void setupComboBox() {
        String[] items = null;
        double[] scales = null;

        switch (this.unitType) {
        case UNIT_TYPE_REAL_TIME:
            items = ITEMS_REAL_TIME;
            scales = SCALES_REAL_TIME;
            this.addingMode = false;
            break;

        case UNIT_TYPE_LENGTH_BOHR:
            items = ITEMS_LENGTH_BOHR;
            scales = SCALES_LENGTH_BOHR;
            this.addingMode = false;
            break;

        case UNIT_TYPE_LENGTH_ANGS:
            items = ITEMS_LENGTH_ANGS;
            scales = SCALES_LENGTH_ANGS;
            this.addingMode = false;
            break;

        case UNIT_TYPE_ANGLE:
            items = ITEMS_ANGLE;
            scales = SCALES_ANGLE;
            this.addingMode = false;
            break;

        case UNIT_TYPE_ENERGY_RY:
            items = ITEMS_ENERGY_RY;
            scales = SCALES_ENERGY_RY;
            this.addingMode = false;
            break;

        case UNIT_TYPE_ENERGY_HA:
            items = ITEMS_ENERGY_HA;
            scales = SCALES_ENERGY_HA;
            this.addingMode = false;
            break;

        case UNIT_TYPE_ENERGY_EV:
            items = ITEMS_ENERGY_EV;
            scales = SCALES_ENERGY_EV;
            this.addingMode = false;
            break;

        case UNIT_TYPE_FORCE_RY:
            items = ITEMS_FORCE_RY;
            scales = SCALES_FORCE_RY;
            this.addingMode = false;
            break;

        case UNIT_TYPE_FORCE_HA:
            items = ITEMS_FORCE_HA;
            scales = SCALES_FORCE_HA;
            this.addingMode = false;
            break;

        case UNIT_TYPE_PRESSURE:
            items = ITEMS_PRESSURE;
            scales = SCALES_PRESSURE;
            this.addingMode = false;
            break;

        case UNIT_TYPE_MASS:
            items = ITEMS_MASS;
            scales = SCALES_MASS;
            this.addingMode = false;
            break;

        case UNIT_TYPE_TIME_RY:
            items = ITEMS_TIME_RY;
            scales = SCALES_TIME_RY;
            this.addingMode = false;
            break;

        case UNIT_TYPE_TIME_HA:
            items = ITEMS_TIME_HA;
            scales = SCALES_TIME_HA;
            this.addingMode = false;
            break;

        case UNIT_TYPE_TEMPERATURE:
            items = ITEMS_TEMPERATURE;
            scales = SCALES_TEMPERATURE;
            this.addingMode = true;
            break;

        default:
            throw new RuntimeException("unitType is incorrect: " + this.unitType);
        }

        if (items == null || items.length < 1) {
            throw new RuntimeException("items is empty.");
        }

        if (scales == null || scales.length < 1) {
            throw new RuntimeException("scales is empty.");
        }

        if (items.length != scales.length) {
            throw new RuntimeException("items.length != scales.length");
        }

        this.comboBox.getItems().clear();
        this.comboBox.getItems().addAll(items);
        this.comboBox.setValue(items[0]);

        this.scaleMap.clear();
        for (int i = 0; i < items.length; i++) {
            this.scaleMap.put(items[i], scales[i]);
        }
    }

    public void setDisable(boolean disable) {
        this.comboBox.setDisable(disable);
    }

    public void setOnAction(EventHandler<ActionEvent> value) {
        this.comboBox.setOnAction(value);
    }
}
