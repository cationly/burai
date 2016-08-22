/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.graph;

public enum EnergyType {

    TOTAL("ene.TOT"),
    KINETIC("ene.KIN"),
    CONSTANT("ene.TOT+KIN"),
    TEMPERATURE("temp");

    private String symbol;

    private EnergyType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }
}
