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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LinuxCPUInfo extends CPUInfo {

    private static final String CPUINFO_PATH = "/proc/cpuinfo";

    private static final String PROC_WORD = "processor";

    public LinuxCPUInfo() {
        super();
    }

    @Override
    protected int countNumCPUs() {
        try {
            return this.parseCpuinfo();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 1;
    }

    private int parseCpuinfo() throws IOException {
        int numCPUs = 0;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(CPUINFO_PATH));

            String line = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(PROC_WORD)) {
                    numCPUs++;
                }
            }

        } catch (FileNotFoundException e1) {
            throw e1;

        } catch (IOException e2) {
            throw e2;

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e3) {
                    throw e3;
                }
            }
        }

        return numCPUs;
    }
}
