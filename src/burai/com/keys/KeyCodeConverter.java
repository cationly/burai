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

public final class KeyCodeConverter {

    private KeyCodeConverter() {
        // NOP
    }

    public static char toCharacter(KeyCode code) {
        String codeStr = null;
        if (code != null) {
            codeStr = code.getName();
        }
        if (codeStr == null || codeStr.isEmpty()) {
            return 0;
        }

        if (codeStr.length() == 1) {
            return codeStr.charAt(0);
        }

        char codeChar = 0;

        if (KeyCode.MINUS.equals(code)) {
            codeChar = '-';

        } else if (KeyCode.PLUS.equals(code)) {
            codeChar = '+';

        } else if (KeyCode.CIRCUMFLEX.equals(code)) {
            codeChar = '^';

        } else if (KeyCode.BACK_SLASH.equals(code)) {
            codeChar = '\\';

        } else if (KeyCode.AT.equals(code)) {
            codeChar = '@';

        } else if (KeyCode.COLON.equals(code)) {
            codeChar = ':';

        } else if (KeyCode.SEMICOLON.equals(code)) {
            codeChar = ';';

        } else if (KeyCode.OPEN_BRACKET.equals(code)) {
            codeChar = '[';

        } else if (KeyCode.CLOSE_BRACKET.equals(code)) {
            codeChar = ']';

        } else if (KeyCode.COMMA.equals(code)) {
            codeChar = ',';

        } else if (KeyCode.PERIOD.equals(code)) {
            codeChar = '.';

        } else if (KeyCode.SLASH.equals(code)) {
            codeChar = '/';

        } else if (KeyCode.UNDERSCORE.equals(code)) {
            codeChar = '_';
        }

        return codeChar;
    }
}
