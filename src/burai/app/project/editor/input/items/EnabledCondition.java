/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.items;

import burai.input.namelist.QEValue;

@FunctionalInterface
public interface EnabledCondition {

    public abstract boolean isEnabled(String name, QEValue value);

}
