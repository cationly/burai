/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.com.env;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MacCPUInfo extends CPUInfo {

    private static final String CHK_MAC_CPUCOM = "system_profiler SPHardwareDataType";

    private static final String CPU_WORD = "Cores";

    public MacCPUInfo() {
        super();
    }

    @Override
    protected int countNumCPUs() {

        /*
         *  TODO: get #cpus for MacOS.
         */

        int numCPUs = 1;

        try {
            Process process = Runtime.getRuntime().exec(CHK_MAC_CPUCOM);
            BufferedReader in = new BufferedReader(new InputStreamReader(process
                .getInputStream()));

            String line = null;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.contains(CPU_WORD)){
                    String[] nCPULine = line.split(" ", 0);
                    if (nCPULine == null) {
                        continue;
                    }
                    if (nCPULine.length == 0){
                        numCPUs = 1;
                        break;
                    }
                    else {
                        numCPUs = Integer.parseInt(nCPULine[nCPULine.length -1]);
                        if (numCPUs <= 0){
                            numCPUs = 1;
                            break;
                        }
                    }
                }
            }

        } catch (IOException e) {
            // TODO
            numCPUs = 1;
            e.printStackTrace();
        }

        return numCPUs;
    }
}
