/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.com.env;

public abstract class CPUInfo {

    private static CPUInfo instance = null;

    public static CPUInfo getInstance() {
        if (instance == null) {
            if (Environments.isWindows()) {
                instance = new WindowsCPUInfo();

            } else if (Environments.isMac()) {
                instance = new MacCPUInfo();

            } else if (Environments.isLinux()) {
                instance = new LinuxCPUInfo();
            }
        }

        return instance;
    }

    private int numCPUs;

    protected CPUInfo() {
        this.numCPUs = this.countNumCPUs();
    }

    public final int getNumCPUs() {
        return this.numCPUs;
    }

    protected abstract int countNumCPUs();

}
