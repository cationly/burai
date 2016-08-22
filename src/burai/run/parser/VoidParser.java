/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.run.parser;

import java.io.File;
import java.io.IOException;

import burai.project.property.ProjectProperty;

public class VoidParser extends LogParser {

    public VoidParser(ProjectProperty property) {
        super(property);
    }

    @Override
    public void parse(File file) throws IOException {
        // NOP
    }

    @Override
    public void startParsing(File file) {
        // NOP
    }

    @Override
    public void endParsing() {
        // NOP
    }
}
