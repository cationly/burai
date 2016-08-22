/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.matapi;

import java.util.HashMap;
import java.util.Map;

import burai.atoms.element.ElementUtil;

public class MaterialsAPI {

    protected static final String MATERIALS_API_URL = "http://www.materialsproject.org/rest/v1/materials/";

    private static final String[] ELEMENTS = ElementUtil.listAllElements();

    private String formula;

    private MaterialIDs matIDs;

    private Map<String, MaterialData> matDataMap;

    public MaterialsAPI(String formula) {
        if (formula == null || formula.trim().isEmpty()) {
            throw new IllegalArgumentException("formula is empty.");
        }

        this.formula = this.correctFormula(formula);
        if (this.formula == null || this.formula.trim().isEmpty()) {
            throw new IllegalArgumentException("formula is incorrect.");
        }

        this.matIDs = MaterialIDs.getInstance(this.formula);

        this.matDataMap = null;
    }

    private String correctFormula(String formula) {
        String formula2 = formula;
        formula2 = formula2 == null ? null : formula2.replace('-', ' ');
        formula2 = formula2 == null ? null : formula2.replace(',', ' ');
        if (formula2 == null) {
            return null;
        }

        formula2 = formula2.trim();
        if (formula2.isEmpty()) {
            return null;
        }

        String formula3 = null;
        String[] subFormula = formula2.split("[\\s]+");

        if (subFormula.length > 1) {
            formula3 = this.toElementSymbol(subFormula[0]);
            for (int i = 1; i < subFormula.length; i++) {
                formula3 = formula3 + "-" + this.toElementSymbol(subFormula[i]);
            }

        } else {
            formula3 = this.toChemicalFormula(subFormula[0]);
        }

        return formula3;
    }

    private String toElementSymbol(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        char head = str.charAt(0);
        head = Character.toUpperCase(head);

        String str2 = null;
        if (str.length() < 2) {
            str2 = "";
        } else {
            str2 = str.substring(1).toLowerCase();
        }

        return head + str2;
    }

    private String toChemicalFormula(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        StringBuilder strBuilder = new StringBuilder(str);

        String formula = null;
        while (true) {
            String element = this.pollElement(strBuilder);
            if (element != null) {
                formula = (formula == null ? "" : formula) + element;
            } else {
                break;
            }
        }

        return formula;
    }

    private String pollElement(StringBuilder strBuilder) {
        if (strBuilder.length() < 1) {
            return null;
        }

        /*
         * in case of number
         */
        String strNum = "";
        while (strBuilder.length() > 0) {
            char c = strBuilder.charAt(0);
            if (Character.isDigit(c)) {
                strNum = strNum + c;
                strBuilder.deleteCharAt(0);

            } else {
                break;
            }
        }

        if (!strNum.isEmpty()) {
            return strNum;
        }

        /*
         * in case of element
         */
        char c1 = Character.toUpperCase(strBuilder.charAt(0));
        strBuilder.deleteCharAt(0);

        if (strBuilder.length() < 1) {
            // 1-char element
            return Character.toString(c1);
        }

        char c2 = strBuilder.charAt(0);
        if (Character.isDigit(c2)) {
            // 1-char element
            return Character.toString(c1);
        }

        // check to be 1-char element
        boolean isElement1 = false;
        String element1 = Character.toString(c1);
        for (String element : ELEMENTS) {
            if (element.equals(element1)) {
                isElement1 = true;
                break;
            }
        }

        // check to be 2-char element
        boolean isElement2 = false;
        String element2 = Character.toString(c1) + Character.toString(Character.toLowerCase(c2));
        for (String element : ELEMENTS) {
            if (element.equals(element2)) {
                isElement2 = true;
                break;
            }
        }

        if (Character.isLowerCase(c2)) {
            if (isElement2) {
                // 2-char element
                strBuilder.deleteCharAt(0);
                return element2;
            }
            if (isElement1) {
                // 1-char element
                return element1;
            }

        } else {
            if (isElement1) {
                // 1-char element
                return element1;
            }
            if (isElement2) {
                // 2-char element
                strBuilder.deleteCharAt(0);
                return element2;
            }
        }

        // 1-char element
        return element1;
    }

    public String getFormula() {
        return this.formula;
    }

    public int numMaterialIDs() {
        return this.matIDs == null ? 0 : this.matIDs.numIDs();
    }

    public String getMaterialID(int index) throws IndexOutOfBoundsException {
        if (index < 0 || this.numMaterialIDs() <= index) {
            throw new IndexOutOfBoundsException("incorrect index: " + index);
        }

        return this.matIDs == null ? null : this.matIDs.getID(index);
    }

    public MaterialData getMaterialData(int index) throws IndexOutOfBoundsException {
        String strMatID = this.getMaterialID(index);
        if (strMatID == null) {
            return null;
        }

        if (this.matDataMap == null) {
            this.matDataMap = new HashMap<String, MaterialData>();
        }

        if (!this.matDataMap.containsKey(strMatID)) {
            MaterialData matData = MaterialData.getInstance(strMatID);
            if (matData != null) {
                this.matDataMap.put(strMatID, matData);
            }
        }

        return this.matDataMap.get(strMatID);
    }
}
