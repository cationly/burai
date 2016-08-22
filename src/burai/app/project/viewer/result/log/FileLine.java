/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.log;

public class FileLine implements Comparable<FileLine> {

    private int index;

    private String line;

    private boolean enhanced;

    public FileLine(int index, String line, boolean enhanced) {
        this.index = index;
        this.line = line;
        this.enhanced = enhanced;
    }

    public int getIndex() {
        return this.index;
    }

    public String getLine() {
        return this.line;
    }

    public boolean isEnhanced() {
        return this.enhanced;
    }

    @Override
    public int hashCode() {
        return this.getIndex();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int compareTo(FileLine fileLine) {
        if (fileLine == null) {
            return -1;
        }

        if (this.index < fileLine.index) {
            return -1;

        } else if (this.index > fileLine.index) {
            return 1;
        }

        return 0;
    }
}
