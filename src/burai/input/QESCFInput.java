/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input;

import java.io.File;
import java.io.IOException;

import burai.input.card.QEAtomicPositions;
import burai.input.card.QEAtomicSpecies;
import burai.input.card.QECellParameters;
import burai.input.card.QEKPoints;
import burai.input.correcter.QEInputCorrecter;
import burai.input.correcter.QESCFInputCorrecter;
import burai.input.namelist.QENamelist;
import burai.input.namelist.tracer.QEHubbardTracer;
import burai.input.namelist.tracer.QESpinTracer;

public class QESCFInput extends QESecondaryInput {

    public QESCFInput() {
        super();
    }

    public QESCFInput(String fileName) throws IOException {
        super(fileName);
    }

    public QESCFInput(File file) throws IOException {
        super(file);
    }

    @Override
    protected void setupNamelists(QEInputReader reader) throws IOException {
        boolean hasNmlSystem = this.namelists.containsKey(NAMELIST_SYSTEM);

        this.setupNamelist(NAMELIST_CONTROL, reader);
        this.setupNamelist(NAMELIST_SYSTEM, reader);
        this.setupNamelist(NAMELIST_ELECTRONS, reader);

        if (!hasNmlSystem) {
            QENamelist nmlSystem = this.namelists.get(NAMELIST_SYSTEM);
            nmlSystem.addDeletingValue("celldm(1)");
            nmlSystem.addDeletingValue("celldm(2)");
            nmlSystem.addDeletingValue("celldm(3)");
            nmlSystem.addDeletingValue("celldm(4)");
            nmlSystem.addDeletingValue("celldm(5)");
            nmlSystem.addDeletingValue("celldm(6)");
            nmlSystem.addBindingValue("a");
            nmlSystem.addBindingValue("b");
            nmlSystem.addBindingValue("c");
            nmlSystem.addBindingValue("cosab");
            nmlSystem.addBindingValue("cosac");
            nmlSystem.addBindingValue("cosbc");
            nmlSystem.addBindingValue("nspin");
            nmlSystem.addBindingValue("noncolin");
            nmlSystem.addBindingValue("constrained_magnetization");
            nmlSystem.addBindingValue("tot_magnetization");
            nmlSystem.addBindingValue("fixed_magnetization(3)");

            QESpinTracer spinTracer = new QESpinTracer(nmlSystem);
            spinTracer.traceSpin();

            QEHubbardTracer hubbardTracer = new QEHubbardTracer(nmlSystem);
            hubbardTracer.traceHubbard();
        }
    }

    @Override
    protected void setupCards(QEInputReader reader) throws IOException {
        this.setupCard(new QEKPoints(), reader);
        this.setupCard(new QECellParameters(), reader);
        this.setupCard(new QEAtomicSpecies(), reader);
        this.setupCard(new QEAtomicPositions(), reader);
    }

    @Override
    public QESCFInput copy() {
        QESCFInput input = new QESCFInput();
        QEInputCopier copier = new QEInputCopier(this);
        copier.copyTo(input, false);
        return input;
    }

    @Override
    protected QEInputCorrecter createInputCorrector() {
        return new QESCFInputCorrecter(this);
    }
}
