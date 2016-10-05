/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.com.env;

public class MacCPUInfo extends CPUInfo {

    public MacCPUInfo() {
        super();
    }

    @Override
    protected int countNumCPUs() {

        /*
         *  TODO: get #cpus for MacOS.
         */

        return 1;
    }
}
