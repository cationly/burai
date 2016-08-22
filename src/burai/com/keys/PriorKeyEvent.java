/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.com.keys;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public final class PriorKeyEvent {

    private PriorKeyEvent() {
        // NOP
    }

    public static boolean isPriorKeyEvent(KeyEvent event) {
        if (event == null) {
            return false;
        }

        KeyCode keyCode = event.getCode();

        // focus next item
        if (KeyCode.TAB.equals(keyCode)) {
            return true;
        }

        // quit system
        if (event.isControlDown() && KeyCode.Q.equals(keyCode)) {
            return true;
        }

        // close window
        if (event.isControlDown() && KeyCode.W.equals(keyCode)) {
            return true;
        }

        // save data
        if (event.isControlDown() && KeyCode.S.equals(keyCode)) {
            return true;
        }

        // print screen
        if (KeyCode.PRINTSCREEN.equals(keyCode)) {
            return true;
        }

        return false;
    }
}
