/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation.editor;

import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.text.Font;
import burai.atoms.element.ElementUtil;

public final class ElementButton extends Button {

    private static final double TEXT_SIZE = 11.0;
    private static final String TEXT_FONT = "Times New Roman";

    private Dialog<ElementButton> dialog;

    private String name;

    private int x;

    private int y;

    public ElementButton(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name of element is empty.");
        }

        this.dialog = null;

        this.name = name;

        int atomNum = ElementUtil.getAtomicNumber(this.name);
        if (atomNum < 1) {
            throw new IllegalArgumentException("incorrect name of element: " + this.name);
        }

        this.createXY(atomNum);

        this.setText(this.name);
        this.setFont(Font.font(TEXT_FONT, TEXT_SIZE));
        this.setPrefWidth(3.5 * TEXT_SIZE);
        this.setPrefHeight(2.5 * TEXT_SIZE);
    }

    public void setDialog(Dialog<ElementButton> dialog) {
        if (dialog == null) {
            return;
        }

        if (this.dialog == dialog) {
            return;
        }

        this.dialog = dialog;

        this.setOnAction(event -> {
            this.dialog.setResult(this);
            this.dialog.close();
        });
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    private void createXY(int atomNum) {
        if (atomNum < 3) {
            this.x = 1;
            if (atomNum < 2) {
                this.y = atomNum;
            } else {
                this.y = atomNum + 16;
            }

        } else if (atomNum < 11) {
            this.x = 2;
            if (atomNum < 5) {
                this.y = atomNum - 2;
            } else {
                this.y = atomNum + 8;
            }

        } else if (atomNum < 19) {
            this.x = 3;
            if (atomNum < 13) {
                this.y = atomNum - 10;
            } else {
                this.y = atomNum;
            }

        } else if (atomNum < 37) {
            this.x = 4;
            this.y = atomNum - 18;

        } else if (atomNum < 55) {
            this.x = 5;
            this.y = atomNum - 36;

        } else if (atomNum < 57) {
            this.x = 6;
            this.y = atomNum - 54;

        } else if (atomNum < 72) {
            // Lanthanoid
            this.x = 9;
            this.y = atomNum - 53;

        } else if (atomNum < 87) {
            this.x = 6;
            this.y = atomNum - 68;

        } else if (atomNum < 89) {
            this.x = 7;
            this.y = atomNum - 86;

        } else {
            // Actinoid
            this.x = 10;
            this.y = atomNum - 85;
        }
    }
}
