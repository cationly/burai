/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.reader;

import java.io.File;
import java.io.IOException;

import burai.atoms.model.Cell;
import burai.atoms.model.exception.ZeroVolumCellException;
import burai.input.QEGeometryInput;

public class QEReader extends AtomsReader {

    private QEGeometryInput input;

    public QEReader(String fileName) throws IOException {
        super();
        this.input = new QEGeometryInput(fileName);
    }

    public QEReader(File file) throws IOException {
        super();
        this.input = new QEGeometryInput(file);
    }

    public QEGeometryInput getInput() {
        return this.input;
    }

    @Override
    public Cell readCell() {
        Cell cell = null;
        if (this.input != null) {
            cell = this.input.getCell();
        }

        if (cell == null) {
            double[][] lattice = { { 1.0, 0.0, 0.0 }, { 0.0, 1.0, 0.0 }, { 0.0, 0.0, 1.0 } };
            try {
                cell = new Cell(lattice);
            } catch (ZeroVolumCellException e) {
                e.printStackTrace();
            }
        }

        return cell;
    }
}
