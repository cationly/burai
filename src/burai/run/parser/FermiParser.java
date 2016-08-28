/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.run.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import burai.project.property.ProjectEnergies;
import burai.project.property.ProjectProperty;

public class FermiParser extends LogParser {

    private ProjectEnergies fermiEnergies;

    public FermiParser(ProjectProperty property) {
        super(property);

        this.fermiEnergies = this.property.getFermiEnergies();
    }

    @Override
    public synchronized void parse(File file) throws IOException {
        if (this.fermiEnergies != null) {
            this.fermiEnergies.clearEnergies();
        }

        try {
            this.parseKernel(file);

        } catch (IOException e) {
            if (this.fermiEnergies != null) {
                this.fermiEnergies.clearEnergies();
            }
            throw e;

        } finally {
            this.property.saveFermiEnergies();
        }
    }

    private void parseKernel(File file) throws IOException {

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));

            String line = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                String strFermi = null;
                if (line.startsWith("the Fermi energy")) {
                    String[] subLines = line.split("\\s+");
                    if (subLines != null && subLines.length > 4) {
                        strFermi = subLines[4];
                    }
                }

                if (strFermi != null) {
                    try {
                        double fermi = Double.parseDouble(strFermi);
                        if (this.fermiEnergies != null) {
                            this.fermiEnergies.addEnergy(fermi);
                        }
                    } catch (NumberFormatException e) {
                        // NOP
                    }
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
    }
}
