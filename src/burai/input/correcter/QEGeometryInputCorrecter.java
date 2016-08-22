/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input.correcter;

import burai.atoms.element.ElementUtil;
import burai.com.consts.Constants;
import burai.com.env.Environments;
import burai.input.QEInput;
import burai.input.namelist.QEValue;
import burai.pseudo.PseudoLibrary;
import burai.pseudo.PseudoPotential;

public class QEGeometryInputCorrecter extends QEInputCorrecter {

    public QEGeometryInputCorrecter(QEInput input) {
        super(input);
    }

    @Override
    public void correctInput() {
        this.correctNamelistControl();
        this.correctLatticeParameters();
        this.correctCellParameters();
        this.correctAtomicSpecies();
        this.removeNotUsedItems();
    }

    private void correctNamelistControl() {
        if (this.nmlControl == null) {
            return;
        }

        /*
         * pseudo_dir
         */
        this.nmlControl.setValue("pseudo_dir = '" + Environments.getPseudosPath() + "'");
    }

    private void correctLatticeParameters() {
        if (this.nmlSystem == null) {
            return;
        }

        double alat = 1.0;
        QEValue value = null;

        value = this.nmlSystem.getValue("celldm(1)");
        if (value != null) {
            if (this.nmlSystem.getValue("a") == null) {
                this.nmlSystem.setValue("a = " + (Constants.BOHR_RADIUS_ANGS * value.getRealValue()));
            }
            this.nmlSystem.removeValue(value);
        }

        value = this.nmlSystem.getValue("a");
        if (value != null) {
            alat = value.getRealValue();
        }

        value = this.nmlSystem.getValue("celldm(2)");
        if (value != null) {
            if (this.nmlSystem.getValue("b") == null) {
                this.nmlSystem.setValue("b = " + (alat * value.getRealValue()));
            }
            this.nmlSystem.removeValue(value);
        }

        value = this.nmlSystem.getValue("celldm(3)");
        if (value != null) {
            if (this.nmlSystem.getValue("c") == null) {
                this.nmlSystem.setValue("c = " + (alat * value.getRealValue()));
            }
            this.nmlSystem.removeValue(value);
        }

        value = this.nmlSystem.getValue("celldm(4)");
        if (value != null) {
            if (this.nmlSystem.getValue("cosbc") == null) {
                this.nmlSystem.setValue("cosbc = " + value.getRealValue());
            }
            this.nmlSystem.removeValue(value);
        }

        value = this.nmlSystem.getValue("celldm(5)");
        if (value != null) {
            if (this.nmlSystem.getValue("cosac") == null) {
                this.nmlSystem.setValue("cosac = " + value.getRealValue());
            }
            this.nmlSystem.removeValue(value);
        }

        value = this.nmlSystem.getValue("celldm(6)");
        if (value != null) {
            if (this.nmlSystem.getValue("cosab") == null) {
                this.nmlSystem.setValue("cosab = " + value.getRealValue());
            }
            this.nmlSystem.removeValue(value);
        }
    }

    private void correctCellParameters() {
        if (this.nmlSystem == null) {
            return;
        }

        if (this.cardCell == null) {
            return;
        }

        QEValue value = this.nmlSystem.getValue("ibrav");
        if (value != null && value.getIntegerValue() != 0) {
            double[][] lattice = this.input.getLattice();
            this.cardCell.setAngstrom();
            this.cardCell.setVector(1, lattice[0]);
            this.cardCell.setVector(2, lattice[1]);
            this.cardCell.setVector(3, lattice[2]);
        }
    }

    private void correctAtomicSpecies() {
        if (this.cardSpecies == null) {
            return;
        }

        int numSpecies = this.cardSpecies.numSpecies();
        for (int i = 0; i < numSpecies; i++) {
            if (!this.cardSpecies.hasPseudoPotential(i)) {
                String label = this.cardSpecies.getLabel(i);
                String element = label == null ? null : ElementUtil.toElementName(label.trim());
                PseudoPotential pseudoPot =
                        element == null ? null : PseudoLibrary.getInstance().getPseudoPotential(element);
                String pseudoName = pseudoPot == null ? null : pseudoPot.getName();
                if(pseudoName!= null) {
                    this.cardSpecies.setPseudoPotential(i, pseudoName);
                }
            }
        }
    }

    private void removeNotUsedItems() {
        if (this.nmlControl != null) {
            QEValue[] values = this.nmlControl.listQEValues();
            for (QEValue value : values) {
                String name = value.getName();
                if ("pseudo_dir".equalsIgnoreCase(name)) {
                    continue;
                }

                this.nmlControl.removeValue(value);
            }
        }

        if (this.nmlSystem != null) {
            QEValue[] values = this.nmlSystem.listQEValues();
            for (QEValue value : values) {
                String name = value.getName();
                if ("ibrav".equalsIgnoreCase(name)) {
                    continue;
                } else if ("a".equalsIgnoreCase(name)) {
                    continue;
                } else if ("b".equalsIgnoreCase(name)) {
                    continue;
                } else if ("c".equalsIgnoreCase(name)) {
                    continue;
                } else if ("cosbc".equalsIgnoreCase(name)) {
                    continue;
                } else if ("cosac".equalsIgnoreCase(name)) {
                    continue;
                } else if ("cosab".equalsIgnoreCase(name)) {
                    continue;
                } else if ("ntyp".equalsIgnoreCase(name)) {
                    continue;
                } else if ("nat".equalsIgnoreCase(name)) {
                    continue;
                }

                this.nmlSystem.removeValue(value);
            }
        }
    }
}
