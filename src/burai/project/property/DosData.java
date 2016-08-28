/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.project.property;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DosData {

    private static final long INIT_TIME_STAMP = 0L;

    private File file;

    private long timeStamp;

    private DosType type;

    private boolean spinPolarized;

    private int atomIndex;

    private String atomName;

    private List<Point> points;

    public DosData(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("file is null.");
        }

        this.file = file;
        this.timeStamp = INIT_TIME_STAMP;

        this.type = null;
        this.spinPolarized = false;
        this.atomIndex = -1;
        this.atomName = null;
        this.points = new ArrayList<Point>();

        this.setupAtomData(file.getName());
        this.reload();
    }

    public DosType getType() {
        return this.type;
    }

    public boolean isSpinPolarized() {
        return this.spinPolarized;
    }

    public int getAtomIndex() {
        return this.atomIndex;
    }

    public String getAtomName() {
        return this.atomName;
    }

    public int numPoints() {
        return this.points.size();
    }

    public double getEnergy(int i) {
        if (i < 0 || i >= this.points.size()) {
            throw new IndexOutOfBoundsException("incorrect index of points: " + i + ".");
        }

        return this.points.get(i).energy;
    }

    public double getDosUp(int i) {
        if (i < 0 || i >= this.points.size()) {
            throw new IndexOutOfBoundsException("incorrect index of points: " + i + ".");
        }

        return this.points.get(i).dosUp;
    }

    public double getDosDown(int i) {
        if (i < 0 || i >= this.points.size()) {
            throw new IndexOutOfBoundsException("incorrect index of points: " + i + ".");
        }

        return this.points.get(i).dosDown;
    }

    private static class Point {

        public double energy;

        public double dosUp;

        public double dosDown;

        public Point(double energy, double dosUp, double dosDown) {
            this.energy = energy;
            this.dosUp = dosUp;
            this.dosDown = dosDown;
        }
    }

    private void setupAtomData(String name) {
        if (name == null || name.isEmpty()) {
            return;
        }

        String[] subName = name.trim().split("\\.");
        if (subName == null || subName.length < 1) {
            return;
        }

        String extName = subName[subName.length - 1];
        if (extName == null || extName.isEmpty()) {
            return;
        }

        if (!extName.startsWith("pdos")) {
            return;
        }

        String[] words = extName.split("[_#]+");
        if (words == null || words.length < 3) {
            return;
        }

        String word = words[2];
        if (word == null || word.isEmpty()) {
            return;
        }

        word = word.replace('(', ' ');
        word = word.replace(')', ' ');
        String[] tokens = word.trim().split("\\s+");
        if (tokens == null || tokens.length < 2) {
            return;
        }

        try {
            this.atomIndex = Integer.parseInt(tokens[0]);
            this.atomName = tokens[1];

        } catch (Exception e) {
            this.atomIndex = -1;
            this.atomName = null;
        }
    }

    private boolean reloadTimeStamp() {
        long timeStamp2 = INIT_TIME_STAMP;

        try {
            if (this.file.exists()) {
                timeStamp2 = this.file.lastModified();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (this.timeStamp != INIT_TIME_STAMP && this.timeStamp == timeStamp2) {
            return false;
        }

        this.timeStamp = timeStamp2;
        return true;
    }

    public void reload() throws IOException {
        if (!this.reloadTimeStamp()) {
            return;
        }

        BufferedReader reader = null;

        try {
            String line = null;
            reader = new BufferedReader(new FileReader(this.file));

            // read header
            String header = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                boolean startData = false;
                String[] subLines = line.split("[\\s,]+");
                if (subLines != null && subLines.length > 0) {
                    try {
                        Double.parseDouble(subLines[0]);
                        startData = true;
                    } catch (Exception e) {
                        startData = false;
                    }
                }

                if (startData) {
                    break;
                }

                if (header == null) {
                    header = line;
                } else {
                    header = header + " " + line;
                }
            }

            this.type = DosType.TOTAL;
            this.spinPolarized = false;

            String[] subHeaders = header == null ? null : header.split("[\\s,]+");
            if (subHeaders != null) {
                for (String subHeader : subHeaders) {
                    if (subHeader == null) {
                        continue;
                    }
                    if (subHeader.startsWith("ldos")) {
                        this.type = null;
                    }
                    if (subHeader.startsWith("dosup") || subHeader.startsWith("ldosup")) {
                        this.spinPolarized = true;
                    }
                }
            }

            // read data
            if (this.type == null && line != null) {
                String[] subLines = line.split("[\\s,]+");
                int numSubs = subLines == null ? 0 : subLines.length;
                numSubs--;
                if (this.spinPolarized) {
                    numSubs /= 2;
                }
                numSubs--;

                switch (numSubs) {
                case 1:
                    this.type = DosType.PDOS_S;
                    break;
                case 3:
                    this.type = DosType.PDOS_P;
                    break;
                case 5:
                    this.type = DosType.PDOS_D;
                    break;
                case 7:
                    this.type = DosType.PDOS_F;
                    break;
                default:
                    this.type = DosType.TOTAL;
                    break;
                }
            }

            while (line != null) {
                if (line.isEmpty()) {
                    continue;
                }

                String[] subLines = line.split("[\\s,]+");
                try {
                    double energy = Double.parseDouble(subLines[0]);
                    double dosUp = Double.parseDouble(subLines[1]);
                    double dosDown = this.spinPolarized ? Double.parseDouble(subLines[2]) : 0.0;
                    this.points.add(new Point(energy, dosUp, dosDown));

                } catch (Exception e) {
                    // NOP
                }

                line = reader.readLine();
                if (line != null) {
                    line = line.trim();
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

                } catch (Exception e3) {
                    throw e3;
                }
            }
        }
    }
}