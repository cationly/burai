/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input.correcter;

import java.util.ArrayList;
import java.util.List;

import burai.com.math.Matrix3D;
import burai.input.QEInput;
import burai.input.card.QEKPoint;
import burai.input.namelist.QENamelist;
import burai.input.namelist.QEValue;

public class SymmetricKPointsGenerator {

    private QEInput input;

    private QENamelist nmlSystem;

    public SymmetricKPointsGenerator(QEInput input) {
        if (input == null) {
            throw new IllegalArgumentException("input is null.");
        }

        this.input = input;

        this.nmlSystem = this.input.getNamelist(QEInput.NAMELIST_SYSTEM);
    }

    public boolean isAvailable() {
        if (this.nmlSystem != null) {
            return true;
        }

        return false;
    }

    public List<QEKPoint> getKPoints() {
        if (this.nmlSystem == null) {
            return null;
        }

        int ibz = this.getBZType();
        return this.listKPoints(ibz);
    }

    protected int getBZType() {
        if (this.nmlSystem == null) {
            return -1;
        }

        QEValue ibravValue = this.nmlSystem.getValue("ibrav");
        QEValue aValue = this.nmlSystem.getValue("a");
        QEValue bValue = this.nmlSystem.getValue("b");
        QEValue cValue = this.nmlSystem.getValue("c");
        QEValue cosbcValue = this.nmlSystem.getValue("cosbc");

        int ibrav = ibravValue == null ? 0 : ibravValue.getIntegerValue();
        double a = aValue == null ? 0.0 : aValue.getRealValue();
        double b = bValue == null ? 0.0 : bValue.getRealValue();
        double c = cValue == null ? 0.0 : cValue.getRealValue();
        double cosBC = cosbcValue == null ? 0.0 : cosbcValue.getRealValue();
        double celldm1 = a;
        double celldm2 = a == 0.0 ? 1.0 : b / a;
        double celldm3 = a == 0.0 ? 1.0 : c / a;
        double celldm4 = cosBC;

        int ibz = 0;

        if (ibrav == 1) {
            ibz = 1;

        } else if (ibrav == 2) {
            ibz = 2;

        } else if (ibrav == 3) {
            ibz = 3;

        } else if (ibrav == 4) {
            ibz = 13;

        } else if (ibrav == 5) {
            if (celldm4 > 0.0) {
                ibz = 14;
            } else {
                ibz = 15;
            }

        } else if (ibrav == 6) {
            ibz = 4;

        } else if (ibrav == 7) {
            if (celldm3 > 1.0) {
                ibz = 6;
            } else {
                ibz = 5;
            }

        } else if (ibrav == 8) {
            ibz = 7;

        } else if (ibrav == 9 || ibrav == -9) {
            ibz = 12;

        } else if (ibrav == 10) {
            if ((celldm2 < 1.0) || (celldm3 < 1.0) || (celldm2 > celldm3)) {
                if (celldm2 > celldm3) {
                    double buffer = celldm2;
                    celldm2 = celldm3;
                    celldm3 = buffer;
                }

                if (celldm2 < 1.0 && celldm3 > 1.0) {
                    celldm1 = celldm2 * celldm1;
                    celldm3 = celldm2 == 0.0 ? 1.0 : celldm3 / celldm2;
                    celldm2 = celldm2 == 0.0 ? 1.0 : 1.0 / celldm2;

                } else if (celldm2 < 1.0) {
                    double buffer = celldm3;
                    celldm1 = celldm2 * celldm1;
                    celldm3 = celldm2 == 0.0 ? 1.0 : 1.0 / celldm2;
                    celldm2 = celldm2 == 0.0 ? 1.0 : buffer / celldm2;
                }
            }

            double value = 1.0;
            if (celldm2 != 0.0 && celldm3 != 0.0) {
                value = 1.0 / celldm2 / celldm2 + 1.0 / celldm3 / celldm3;
            }
            if (value < (1.0 - 1.0e-8)) {
                ibz = 8;
            } else if (value > (1.0 + 1.0e-8)) {
                ibz = 9;
            } else {
                ibz = 10;
            }

        } else if (ibrav == 11) {
            ibz = 11;

        } else if (ibrav == 12 || ibrav == -12) {
            ibz = 16;
        }

        return ibz;
    }

    private List<QEKPoint> listKPoints(int ibz) {
        boolean switch_a_b = false;
        boolean switch_b_c = false;
        boolean rotate_a_b_c = false;

        if (ibz == 8 || ibz == 9 || ibz == 10 || ibz == 11) {
            QEValue aValue = this.nmlSystem.getValue("a");
            QEValue bValue = this.nmlSystem.getValue("b");
            QEValue cValue = this.nmlSystem.getValue("c");
            double a = aValue == null ? 0.0 : aValue.getRealValue();
            double b = bValue == null ? 0.0 : bValue.getRealValue();
            double c = cValue == null ? 0.0 : cValue.getRealValue();
            double celldm2 = a == 0.0 ? 1.0 : b / a;
            double celldm3 = a == 0.0 ? 1.0 : c / a;
            if ((celldm2 < 1.0) || (celldm3 < 1.0) || (celldm2 > celldm3)) {
                if (celldm2 > celldm3) {
                    switch_b_c = true;
                }
                if (celldm2 < 1.0 && celldm3 > 1.0) {
                    switch_a_b = true;
                } else if (celldm2 < 1.0) {
                    rotate_a_b_c = true;
                }
            }

        } else if (ibz == 12) {
            QEValue aValue = this.nmlSystem.getValue("a");
            QEValue bValue = this.nmlSystem.getValue("b");
            double a = aValue == null ? 0.0 : aValue.getRealValue();
            double b = bValue == null ? 0.0 : bValue.getRealValue();
            double celldm2 = a == 0.0 ? 1.0 : b / a;
            if (celldm2 < 1.0) {
                switch_a_b = true;
            }
        }

        List<QEKPoint> kpoints = new ArrayList<QEKPoint>();
        kpoints.add(new QEKPoint("gG", 0.0));

        if (ibz == 1) {
            kpoints.add(new QEKPoint("M", 0.0));
            kpoints.add(new QEKPoint("X", 0.0));
            kpoints.add(new QEKPoint("R", 0.0));

        } else if (ibz == 2) {
            kpoints.add(new QEKPoint("K", 0.0));
            kpoints.add(new QEKPoint("W", 0.0));
            kpoints.add(new QEKPoint("U", 0.0));
            kpoints.add(new QEKPoint("X", 0.0));
            kpoints.add(new QEKPoint("L", 0.0));

        } else if (ibz == 3) {
            kpoints.add(new QEKPoint("N", 0.0));
            kpoints.add(new QEKPoint("P", 0.0));
            kpoints.add(new QEKPoint("H", 0.0));

        } else if (ibz == 4) {
            kpoints.add(new QEKPoint("M", 0.0));
            kpoints.add(new QEKPoint("X", 0.0));
            kpoints.add(new QEKPoint("Z", 0.0));
            kpoints.add(new QEKPoint("A", 0.0));
            kpoints.add(new QEKPoint("R", 0.0));

        } else if (ibz == 5) {
            kpoints.add(new QEKPoint("M", 0.0));
            kpoints.add(new QEKPoint("X", 0.0));
            kpoints.add(new QEKPoint("Z1", 0.0));
            kpoints.add(new QEKPoint("N", 0.0));
            kpoints.add(new QEKPoint("Z", 0.0));
            kpoints.add(new QEKPoint("P", 0.0));

        } else if (ibz == 6) {
            kpoints.add(new QEKPoint("gS", 0.0));
            kpoints.add(new QEKPoint("N", 0.0));
            kpoints.add(new QEKPoint("gS1", 0.0));
            kpoints.add(new QEKPoint("Z", 0.0));
            kpoints.add(new QEKPoint("Y1", 0.0));
            kpoints.add(new QEKPoint("P", 0.0));
            kpoints.add(new QEKPoint("X", 0.0));
            kpoints.add(new QEKPoint("Y", 0.0));

        } else if (ibz == 7) {
            kpoints.add(new QEKPoint("X", 0.0));
            kpoints.add(new QEKPoint("S", 0.0));
            kpoints.add(new QEKPoint("Y", 0.0));
            kpoints.add(new QEKPoint("U", 0.0));
            kpoints.add(new QEKPoint("R", 0.0));
            kpoints.add(new QEKPoint("T", 0.0));
            kpoints.add(new QEKPoint("Z", 0.0));

        } else if (ibz == 8) {
            QEKPoint[] kpointsTmp = new QEKPoint[10];
            kpointsTmp[2] = new QEKPoint("A", 0.0);
            kpointsTmp[3] = new QEKPoint("X", 0.0);
            kpointsTmp[4] = new QEKPoint("Z", 0.0);
            kpointsTmp[5] = new QEKPoint("L", 0.0);
            kpointsTmp[6] = new QEKPoint("X1", 0.0);
            kpointsTmp[7] = new QEKPoint("A1", 0.0);
            kpointsTmp[8] = new QEKPoint("T", 0.0);
            kpointsTmp[9] = new QEKPoint("Y", 0.0);
            if (switch_b_c) {
                kpointsTmp[2] = new QEKPoint("A1", 0.0);
                kpointsTmp[4] = new QEKPoint("Y", 0.0);
                kpointsTmp[7] = new QEKPoint("A", 0.0);
                kpointsTmp[9] = new QEKPoint("Z", 0.0);
            }
            if (switch_a_b) {
                if (switch_b_c) {
                    kpointsTmp[2] = new QEKPoint("T", 0.0);
                    kpointsTmp[3] = new QEKPoint("Z", 0.0);
                    kpointsTmp[4] = new QEKPoint("Y", 0.0);
                    kpointsTmp[7] = new QEKPoint("A", 0.0);
                    kpointsTmp[8] = new QEKPoint("A1", 0.0);
                    kpointsTmp[9] = new QEKPoint("X", 0.0);
                } else {
                    kpointsTmp[2] = new QEKPoint("T", 0.0);
                    kpointsTmp[3] = new QEKPoint("Y", 0.0);
                    kpointsTmp[8] = new QEKPoint("A", 0.0);
                    kpointsTmp[9] = new QEKPoint("X", 0.0);
                }
            } else if (rotate_a_b_c) {
                if (switch_b_c) {
                    kpointsTmp[2] = new QEKPoint("A", 0.0);
                    kpointsTmp[3] = new QEKPoint("Z", 0.0);
                    kpointsTmp[4] = new QEKPoint("X", 0.0);
                    kpointsTmp[7] = new QEKPoint("T", 0.0);
                    kpointsTmp[8] = new QEKPoint("A1", 0.0);
                    kpointsTmp[9] = new QEKPoint("Y", 0.0);
                } else {
                    kpointsTmp[2] = new QEKPoint("A1", 0.0);
                    kpointsTmp[3] = new QEKPoint("Y", 0.0);
                    kpointsTmp[4] = new QEKPoint("X", 0.0);
                    kpointsTmp[7] = new QEKPoint("T", 0.0);
                    kpointsTmp[8] = new QEKPoint("A", 0.0);
                    kpointsTmp[9] = new QEKPoint("Z", 0.0);
                }
            }

            for (int i = 2; i < kpointsTmp.length; i++) {
                kpoints.add(kpointsTmp[i]);
            }

        } else if (ibz == 9) {
            QEKPoint[] kpointsTmp = new QEKPoint[12];
            kpointsTmp[2] = new QEKPoint("X", 0.0);
            kpointsTmp[3] = new QEKPoint("D", 0.0);
            kpointsTmp[4] = new QEKPoint("C", 0.0);
            kpointsTmp[5] = new QEKPoint("Y", 0.0);
            kpointsTmp[6] = new QEKPoint("H1", 0.0);
            kpointsTmp[7] = new QEKPoint("L", 0.0);
            kpointsTmp[8] = new QEKPoint("H", 0.0);
            kpointsTmp[9] = new QEKPoint("C1", 0.0);
            kpointsTmp[10] = new QEKPoint("D1", 0.0);
            kpointsTmp[11] = new QEKPoint("Z", 0.0);
            if (switch_b_c) {
                kpointsTmp[3] = new QEKPoint("H1", 0.0);
                kpointsTmp[4] = new QEKPoint("C1", 0.0);
                kpointsTmp[5] = new QEKPoint("Z", 0.0);
                kpointsTmp[6] = new QEKPoint("D", 0.0);
                kpointsTmp[8] = new QEKPoint("D1", 0.0);
                kpointsTmp[9] = new QEKPoint("C", 0.0);
                kpointsTmp[10] = new QEKPoint("H", 0.0);
                kpointsTmp[11] = new QEKPoint("Y", 0.0);
            }
            if (switch_a_b) {
                if (switch_b_c) {
                    kpointsTmp[2] = new QEKPoint("Z", 0.0);
                    kpointsTmp[3] = new QEKPoint("C1", 0.0);
                    kpointsTmp[4] = new QEKPoint("H1", 0.0);
                    kpointsTmp[5] = new QEKPoint("X", 0.0);
                    kpointsTmp[6] = new QEKPoint("D1", 0.0);
                    kpointsTmp[8] = new QEKPoint("D", 0.0);
                    kpointsTmp[9] = new QEKPoint("H", 0.0);
                    kpointsTmp[10] = new QEKPoint("C", 0.0);
                    kpointsTmp[11] = new QEKPoint("Y", 0.0);
                } else {
                    kpointsTmp[2] = new QEKPoint("Y", 0.0);
                    kpointsTmp[3] = new QEKPoint("C", 0.0);
                    kpointsTmp[4] = new QEKPoint("D", 0.0);
                    kpointsTmp[5] = new QEKPoint("X", 0.0);
                    kpointsTmp[6] = new QEKPoint("H", 0.0);
                    kpointsTmp[8] = new QEKPoint("H1", 0.0);
                    kpointsTmp[9] = new QEKPoint("D1", 0.0);
                    kpointsTmp[10] = new QEKPoint("C1", 0.0);
                }
            } else if (rotate_a_b_c) {
                if (switch_b_c) {
                    kpointsTmp[2] = new QEKPoint("Z", 0.0);
                    kpointsTmp[3] = new QEKPoint("D1", 0.0);
                    kpointsTmp[4] = new QEKPoint("H", 0.0);
                    kpointsTmp[5] = new QEKPoint("Y", 0.0);
                    kpointsTmp[6] = new QEKPoint("C1", 0.0);
                    kpointsTmp[8] = new QEKPoint("C", 0.0);
                    kpointsTmp[9] = new QEKPoint("H1", 0.0);
                    kpointsTmp[10] = new QEKPoint("D", 0.0);
                    kpointsTmp[11] = new QEKPoint("X", 0.0);
                } else {
                    kpointsTmp[2] = new QEKPoint("Y", 0.0);
                    kpointsTmp[3] = new QEKPoint("H", 0.0);
                    kpointsTmp[4] = new QEKPoint("D1", 0.0);
                    kpointsTmp[6] = new QEKPoint("C", 0.0);
                    kpointsTmp[5] = new QEKPoint("Z", 0.0);
                    kpointsTmp[8] = new QEKPoint("C1", 0.0);
                    kpointsTmp[9] = new QEKPoint("D", 0.0);
                    kpointsTmp[10] = new QEKPoint("H1", 0.0);
                    kpointsTmp[11] = new QEKPoint("X", 0.0);
                }
            }

            for (int i = 2; i < kpointsTmp.length; i++) {
                kpoints.add(kpointsTmp[i]);
            }

        } else if (ibz == 10) {
            QEKPoint[] kpointsTmp = new QEKPoint[9];
            kpointsTmp[2] = new QEKPoint("X", 0.0);
            kpointsTmp[3] = new QEKPoint("A", 0.0);
            kpointsTmp[4] = new QEKPoint("L", 0.0);
            kpointsTmp[5] = new QEKPoint("Z", 0.0);
            kpointsTmp[6] = new QEKPoint("T", 0.0);
            kpointsTmp[7] = new QEKPoint("A1", 0.0);
            kpointsTmp[8] = new QEKPoint("Y", 0.0);
            if (switch_b_c) {
                kpointsTmp[3] = new QEKPoint("A1", 0.0);
                kpointsTmp[5] = new QEKPoint("Y", 0.0);
                kpointsTmp[7] = new QEKPoint("A", 0.0);
                kpointsTmp[8] = new QEKPoint("Z", 0.0);
            }
            if (switch_a_b) {
                if (switch_b_c) {
                    kpointsTmp[2] = new QEKPoint("Z", 0.0);
                    kpointsTmp[3] = new QEKPoint("T", 0.0);
                    kpointsTmp[5] = new QEKPoint("Y", 0.0);
                    kpointsTmp[6] = new QEKPoint("A1", 0.0);
                    kpointsTmp[7] = new QEKPoint("A", 0.0);
                    kpointsTmp[8] = new QEKPoint("X", 0.0);
                } else {
                    kpointsTmp[3] = new QEKPoint("T", 0.0);
                    kpointsTmp[6] = new QEKPoint("A", 0.0);
                    kpointsTmp[8] = new QEKPoint("X", 0.0);
                    kpointsTmp[2] = new QEKPoint("Y", 0.0);
                }
            } else if (rotate_a_b_c) {
                if (switch_b_c) {
                    kpointsTmp[2] = new QEKPoint("Z", 0.0);
                    kpointsTmp[3] = new QEKPoint("A", 0.0);
                    kpointsTmp[5] = new QEKPoint("X", 0.0);
                    kpointsTmp[6] = new QEKPoint("A1", 0.0);
                    kpointsTmp[7] = new QEKPoint("T", 0.0);
                    kpointsTmp[8] = new QEKPoint("Y", 0.0);
                } else {
                    kpointsTmp[2] = new QEKPoint("Y", 0.0);
                    kpointsTmp[3] = new QEKPoint("A1", 0.0);
                    kpointsTmp[5] = new QEKPoint("X", 0.0);
                    kpointsTmp[6] = new QEKPoint("A", 0.0);
                    kpointsTmp[7] = new QEKPoint("T", 0.0);
                    kpointsTmp[8] = new QEKPoint("Z", 0.0);
                }
            }

            for (int i = 2; i < kpointsTmp.length; i++) {
                kpoints.add(kpointsTmp[i]);
            }

        } else if (ibz == 11) {
            QEKPoint[] kpointsTmp = new QEKPoint[14];
            kpointsTmp[2] = new QEKPoint("X", 0.0);
            kpointsTmp[3] = new QEKPoint("L", 0.0);
            kpointsTmp[4] = new QEKPoint("T", 0.0);
            kpointsTmp[5] = new QEKPoint("L1", 0.0);
            kpointsTmp[6] = new QEKPoint("Y", 0.0);
            kpointsTmp[7] = new QEKPoint("R", 0.0);
            kpointsTmp[8] = new QEKPoint("W", 0.0);
            kpointsTmp[9] = new QEKPoint("S", 0.0);
            kpointsTmp[10] = new QEKPoint("X1", 0.0);
            kpointsTmp[11] = new QEKPoint("L2", 0.0);
            kpointsTmp[12] = new QEKPoint("Y1", 0.0);
            kpointsTmp[13] = new QEKPoint("Z", 0.0);
            if (switch_b_c) {
                kpointsTmp[3] = new QEKPoint("L", 0.0);
                kpointsTmp[4] = new QEKPoint("R", 0.0);
                kpointsTmp[5] = new QEKPoint("L2", 0.0);
                kpointsTmp[7] = new QEKPoint("T", 0.0);
                kpointsTmp[8] = new QEKPoint("W", 0.0);
                kpointsTmp[9] = new QEKPoint("S", 0.0);
                kpointsTmp[10] = new QEKPoint("X1", 0.0);
                kpointsTmp[11] = new QEKPoint("L1", 0.0);
                kpointsTmp[12] = new QEKPoint("Z1", 0.0);
                kpointsTmp[6] = new QEKPoint("Z", 0.0);
                kpointsTmp[13] = new QEKPoint("Y", 0.0);
            }
            if (switch_a_b) {
                if (switch_b_c) {
                    kpointsTmp[3] = new QEKPoint("L2", 0.0);
                    kpointsTmp[4] = new QEKPoint("R", 0.0);
                    kpointsTmp[5] = new QEKPoint("L", 0.0);
                    kpointsTmp[7] = new QEKPoint("S", 0.0);
                    kpointsTmp[9] = new QEKPoint("T", 0.0);
                    kpointsTmp[10] = new QEKPoint("Z1", 0.0);
                    kpointsTmp[12] = new QEKPoint("X1", 0.0);
                    kpointsTmp[2] = new QEKPoint("Z", 0.0);
                    kpointsTmp[6] = new QEKPoint("X", 0.0);
                    kpointsTmp[11] = new QEKPoint("L1", 0.0);
                    kpointsTmp[13] = new QEKPoint("Y", 0.0);
                } else {
                    kpointsTmp[2] = new QEKPoint("Y", 0.0);
                    kpointsTmp[3] = new QEKPoint("L1", 0.0);
                    kpointsTmp[5] = new QEKPoint("L", 0.0);
                    kpointsTmp[6] = new QEKPoint("X", 0.0);
                    kpointsTmp[13] = new QEKPoint("Z", 0.0);
                    kpointsTmp[7] = new QEKPoint("S", 0.0);
                    kpointsTmp[9] = new QEKPoint("R", 0.0);
                    kpointsTmp[10] = new QEKPoint("Y1", 0.0);
                    kpointsTmp[11] = new QEKPoint("L2", 0.0);
                    kpointsTmp[12] = new QEKPoint("X1", 0.0);
                }
            } else if (rotate_a_b_c) {
                if (switch_b_c) {
                    kpointsTmp[3] = new QEKPoint("L2", 0.0);
                    kpointsTmp[4] = new QEKPoint("S", 0.0);
                    kpointsTmp[5] = new QEKPoint("L1", 0.0);
                    kpointsTmp[7] = new QEKPoint("R", 0.0);
                    kpointsTmp[8] = new QEKPoint("W", 0.0);
                    kpointsTmp[9] = new QEKPoint("T", 0.0);
                    kpointsTmp[10] = new QEKPoint("Z1", 0.0);
                    kpointsTmp[11] = new QEKPoint("L", 0.0);
                    kpointsTmp[12] = new QEKPoint("Y1", 0.0);
                    kpointsTmp[2] = new QEKPoint("Z", 0.0);
                    kpointsTmp[6] = new QEKPoint("Y", 0.0);
                    kpointsTmp[13] = new QEKPoint("X", 0.0);
                } else {
                    kpointsTmp[2] = new QEKPoint("Y", 0.0);
                    kpointsTmp[3] = new QEKPoint("L1", 0.0);
                    kpointsTmp[4] = new QEKPoint("S", 0.0);
                    kpointsTmp[5] = new QEKPoint("L2", 0.0);
                    kpointsTmp[6] = new QEKPoint("Z", 0.0);
                    kpointsTmp[7] = new QEKPoint("T", 0.0);
                    kpointsTmp[9] = new QEKPoint("R", 0.0);
                    kpointsTmp[10] = new QEKPoint("Y1", 0.0);
                    kpointsTmp[11] = new QEKPoint("L", 0.0);
                    kpointsTmp[12] = new QEKPoint("Z1", 0.0);
                    kpointsTmp[13] = new QEKPoint("X", 0.0);
                }
            }

            for (int i = 2; i < kpointsTmp.length; i++) {
                kpoints.add(kpointsTmp[i]);
            }

        } else if (ibz == 12) {
            if (switch_a_b) {
                kpoints.add(new QEKPoint("Y", 0.0));
                kpoints.add(new QEKPoint("S", 0.0));
                kpoints.add(new QEKPoint("Y1", 0.0));
                kpoints.add(new QEKPoint("X", 0.0));
                kpoints.add(new QEKPoint("A", 0.0));
                kpoints.add(new QEKPoint("A1", 0.0));
                kpoints.add(new QEKPoint("R", 0.0));
                kpoints.add(new QEKPoint("T", 0.0));
                kpoints.add(new QEKPoint("Z", 0.0));
            } else {
                kpoints.add(new QEKPoint("X", 0.0));
                kpoints.add(new QEKPoint("S", 0.0));
                kpoints.add(new QEKPoint("X1", 0.0));
                kpoints.add(new QEKPoint("Y", 0.0));
                kpoints.add(new QEKPoint("T", 0.0));
                kpoints.add(new QEKPoint("A1", 0.0));
                kpoints.add(new QEKPoint("R", 0.0));
                kpoints.add(new QEKPoint("A", 0.0));
                kpoints.add(new QEKPoint("Z", 0.0));
            }

        } else if (ibz == 13) {
            kpoints.add(new QEKPoint("K", 0.0));
            kpoints.add(new QEKPoint("M", 0.0));
            kpoints.add(new QEKPoint("A", 0.0));
            kpoints.add(new QEKPoint("L", 0.0));
            kpoints.add(new QEKPoint("H", 0.0));

        } else if (ibz == 14) {
            kpoints.add(new QEKPoint("L1", 0.0));
            kpoints.add(new QEKPoint("L", 0.0));
            kpoints.add(new QEKPoint("X", 0.0));
            kpoints.add(new QEKPoint("Q", 0.0));
            kpoints.add(new QEKPoint("F", 0.0));
            kpoints.add(new QEKPoint("P", 0.0));
            kpoints.add(new QEKPoint("B", 0.0));
            kpoints.add(new QEKPoint("P1", 0.0));
            kpoints.add(new QEKPoint("Z", 0.0));
            kpoints.add(new QEKPoint("B1", 0.0));
            kpoints.add(new QEKPoint("P2", 0.0));

        } else if (ibz == 15) {
            kpoints.add(new QEKPoint("P1", 0.0));
            kpoints.add(new QEKPoint("Q1", 0.0));
            kpoints.add(new QEKPoint("P", 0.0));
            kpoints.add(new QEKPoint("Z", 0.0));
            kpoints.add(new QEKPoint("F", 0.0));
            kpoints.add(new QEKPoint("L", 0.0));
            kpoints.add(new QEKPoint("Q", 0.0));

        } else if (ibz == 16) {
            QEValue ibravValue = this.nmlSystem.getValue("ibrav");
            int ibrav = ibravValue == null ? 0 : ibravValue.getIntegerValue();
            if (ibrav == 12) {
                kpoints.add(new QEKPoint("X", 0.0));
                kpoints.add(new QEKPoint("Y", 0.0));
                kpoints.add(new QEKPoint("Z", 0.0));
                kpoints.add(new QEKPoint("D", 0.0));
                kpoints.add(new QEKPoint("A", 0.0));
            } else {
                kpoints.add(new QEKPoint("X", 0.0));
                kpoints.add(new QEKPoint("Z", 0.0));
                kpoints.add(new QEKPoint("Y", 0.0));
                kpoints.add(new QEKPoint("A", 0.0));
                kpoints.add(new QEKPoint("D", 0.0));
            }

        } else {
            return null;
        }

        return kpoints;
    }

    private double[][] getReciprocalLattice() {
        double[][] lattice = this.input.getLattice();
        if (lattice == null || lattice.length < 3) {
            return null;
        }

        double[] a1 = lattice[0];
        if (a1 == null || a1.length < 3) {
            return null;
        }

        double[] a2 = lattice[1];
        if (a2 == null || a2.length < 3) {
            return null;
        }

        double[] a3 = lattice[2];
        if (a3 == null || a3.length < 3) {
            return null;
        }

        double alat = Matrix3D.norm(a1);
        if (alat == 0.0) {
            return null;
        }
        for (int ip = 0; ip < 3; ip++) {
            a1[ip] /= alat;
            a2[ip] /= alat;
            a3[ip] /= alat;
        }

        double den = 0.0;
        int i = 1;
        int j = 2;
        int k = 3;
        double s = 1.0;
        while (true) {
            for (int ip = 0; ip < 3; ip++) {
                den = den + s * a1[i - 1] * a2[j - 1] * a3[k - 1];
                int l = i;
                i = j;
                j = k;
                k = l;
            }
            i = 2;
            j = 1;
            k = 3;
            s = -s;
            if (s >= 0.0) {
                break;
            }
        }

        if (den == 0.0) {
            return null;
        }

        double[] b1 = new double[3];
        double[] b2 = new double[3];
        double[] b3 = new double[3];

        i = 1;
        j = 2;
        k = 3;
        for (int ip = 0; ip < 3; ip++) {
            b1[ip] = (a2[j - 1] * a3[k - 1] - a2[k - 1] * a3[j - 1]) / den;
            b2[ip] = (a3[j - 1] * a1[k - 1] - a3[k - 1] * a1[j - 1]) / den;
            b3[ip] = (a1[j - 1] * a2[k - 1] - a1[k - 1] * a2[j - 1]) / den;
            int l = i;
            i = j;
            j = k;
            k = l;
        }

        double[][] recips = new double[3][];
        recips[0] = b1;
        recips[1] = b2;
        recips[2] = b3;
        return recips;
    }
}
