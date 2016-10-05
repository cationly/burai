/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.com.env;

public class WindowsCPUInfo extends CPUInfo {

    private static final String NUM_PROC_VAR = "NUMBER_OF_PROCESSORS";

    public WindowsCPUInfo() {
        super();
    }

    @Override
    protected int countNumCPUs() {
        try {
            String strNcpu = System.getenv(NUM_PROC_VAR);
            if (strNcpu != null) {
                int ncpu = Integer.parseInt(strNcpu);
                return Math.max(ncpu, 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;
    }
}
