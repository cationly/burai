/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.project;

import java.io.File;
import java.io.IOException;

import burai.input.QEInput;

@FunctionalInterface
public interface InputGenerator {

    public abstract QEInput generate(File file) throws IOException;

}
