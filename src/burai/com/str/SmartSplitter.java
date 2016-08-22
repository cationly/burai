/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.com.str;

import java.util.ArrayList;
import java.util.List;

public final class SmartSplitter {

    private static final int NOT_QUOTED = 0;
    private static final int SINGLE_QUOTED = 1;
    private static final int DOUBLE_QUOTED = 2;
    private static final int ROUND_BRACKET = 3;
    private static final int SQUARE_BRACKET = 4;
    private static final int WAVED_BRACKET = 5;

    private SmartSplitter() {
        // NOP
    }

    public static String[] split(String line) {
        return split(line, true, ' ', '\t', '\n', '\u000b', '\f', '\r');
    }

    public static String[] split(String line, char... regex) {
        return split(line, true, regex);
    }

    public static String[] split(String line, boolean splitQuotation, char... regex) {
        if (line == null || line.trim().isEmpty()) {
            return new String[0];
        }

        String lineTrim = line.trim();
        List<String> subLine = new ArrayList<String>();

        String str = "";
        int iQuoted = NOT_QUOTED;

        for (int i = 0; i < lineTrim.length(); i++) {
            char c = lineTrim.charAt(i);

            if (iQuoted == SINGLE_QUOTED) {
                if (c == '\'') {
                    if (splitQuotation) {
                        subLine.add(str);
                        str = "";
                    } else {
                        str = str + c;
                    }
                    iQuoted = NOT_QUOTED;
                } else {
                    str = str + c;
                }

            } else if (iQuoted == DOUBLE_QUOTED) {
                if (c == '\"') {
                    if (splitQuotation) {
                        subLine.add(str);
                        str = "";
                    } else {
                        str = str + c;
                    }
                    iQuoted = NOT_QUOTED;
                } else {
                    str = str + c;
                }

            } else if (iQuoted == ROUND_BRACKET) {
                str = str + c;
                if (c == ')') {
                    iQuoted = NOT_QUOTED;
                }

            } else if (iQuoted == SQUARE_BRACKET) {
                str = str + c;
                if (c == ']') {
                    iQuoted = NOT_QUOTED;
                }

            } else if (iQuoted == WAVED_BRACKET) {
                str = str + c;
                if (c == '}') {
                    iQuoted = NOT_QUOTED;
                }

            } else {
                if (c == '\'') {
                    if (splitQuotation) {
                        if (!str.isEmpty()) {
                            subLine.add(str);
                            str = "";
                        }
                    } else {
                        str = str + c;
                    }
                    iQuoted = SINGLE_QUOTED;

                } else if (c == '\"') {
                    if (splitQuotation) {
                        if (!str.isEmpty()) {
                            subLine.add(str);
                            str = "";
                        }
                    } else {
                        str = str + c;
                    }
                    iQuoted = DOUBLE_QUOTED;

                } else if (c == '(') {
                    str = str + c;
                    iQuoted = ROUND_BRACKET;

                } else if (c == '[') {
                    str = str + c;
                    iQuoted = SQUARE_BRACKET;

                } else if (c == '{') {
                    str = str + c;
                    iQuoted = WAVED_BRACKET;

                } else if (containsChar(c, regex)) {
                    if (!str.isEmpty()) {
                        subLine.add(str);
                        str = "";
                    }

                } else {
                    str = str + c;
                }
            }
        }

        if (!str.isEmpty()) {
            subLine.add(str);
            str = "";
        }

        if (subLine.isEmpty()) {
            return new String[0];
        }

        return subLine.toArray(new String[subLine.size()]);
    }

    private static boolean containsChar(char c, char[] cList) {
        if (cList == null || cList.length < 1) {
            return false;
        }

        for (char c_ : cList) {
            if (c == c_) {
                return true;
            }
        }

        return false;
    }
}
