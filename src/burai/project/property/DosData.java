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

public class DosData implements DosInterface, Comparable<DosData> {

    private static final long INIT_TIME_STAMP = 0L;

    private File file;

    private long timeStamp;

    private boolean preLoading;

    private Object preLoadingLock;

    private DosType type;

    private boolean spinPolarized;

    private int atomIndex;

    private String atomName;

    private List<Point> points;

    public DosData(File file) {
        if (file == null) {
            throw new IllegalArgumentException("file is null.");
        }

        this.file = file;
        this.timeStamp = INIT_TIME_STAMP;
        this.preLoading = false;
        this.preLoadingLock = new Object();

        this.type = null;
        this.spinPolarized = false;
        this.atomIndex = -1;
        this.atomName = null;
        this.points = new ArrayList<Point>();

        this.setupAtomData(file.getName());
        this.reload();
    }

    @Override
    public synchronized DosType getType() {
        synchronized (this.preLoadingLock) {
            while (this.preLoading) {
                try {
                    this.preLoadingLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return this.type;
    }

    @Override
    public synchronized boolean isSpinPolarized() {
        synchronized (this.preLoadingLock) {
            while (this.preLoading) {
                try {
                    this.preLoadingLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return this.spinPolarized;
    }

    @Override
    public synchronized int getAtomIndex() {
        synchronized (this.preLoadingLock) {
            while (this.preLoading) {
                try {
                    this.preLoadingLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return this.atomIndex;
    }

    @Override
    public synchronized String getAtomName() {
        synchronized (this.preLoadingLock) {
            while (this.preLoading) {
                try {
                    this.preLoadingLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return this.atomName;
    }

    @Override
    public synchronized int numPoints() {
        synchronized (this.preLoadingLock) {
            while (this.preLoading) {
                try {
                    this.preLoadingLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return this.points.size();
    }

    @Override
    public synchronized double getEnergy(int i) {
        synchronized (this.preLoadingLock) {
            while (this.preLoading) {
                try {
                    this.preLoadingLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (i < 0 || i >= this.points.size()) {
            throw new IndexOutOfBoundsException("incorrect index of points: " + i + ".");
        }

        return this.points.get(i).energy;
    }

    @Override
    public synchronized double getDosUp(int i) {
        synchronized (this.preLoadingLock) {
            while (this.preLoading) {
                try {
                    this.preLoadingLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (i < 0 || i >= this.points.size()) {
            throw new IndexOutOfBoundsException("incorrect index of points: " + i + ".");
        }

        return this.points.get(i).dosUp;
    }

    @Override
    public synchronized double getDosDown(int i) {
        synchronized (this.preLoadingLock) {
            while (this.preLoading) {
                try {
                    this.preLoadingLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

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

    public boolean reload() {
        if (!this.reloadTimeStamp()) {
            return false;
        }

        Thread thread = new Thread(() -> {
            try {
                this.reloadKernel();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        synchronized (this.preLoadingLock) {
            this.preLoading = true;
        }

        thread.start();

        synchronized (this.preLoadingLock) {
            while (this.preLoading) {
                try {
                    this.preLoadingLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    private synchronized void reloadKernel() throws IOException {

        synchronized (this.preLoadingLock) {
            this.preLoading = false;
            this.preLoadingLock.notifyAll();
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

    @Override
    public synchronized int compareTo(DosData dosData) {
        synchronized (this.preLoadingLock) {
            while (this.preLoading) {
                try {
                    this.preLoadingLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (dosData == null) {
            return -1;
        }

        boolean sameType = false;
        if (this.type == null) {
            sameType = (this.type == dosData.type);
        } else {
            sameType = (this.type.equals(dosData.type));
        }

        if (!sameType) {
            if (DosType.TOTAL.equals(this.type)) {
                return -1;
            } else if (DosType.TOTAL.equals(dosData.type)) {
                return 1;
            }
        }

        if (this.atomIndex != dosData.atomIndex) {
            return (this.atomIndex < dosData.atomIndex) ? -1 : 1;
        }

        int momentum1 = this.type == null ? Integer.MAX_VALUE : this.type.getMomentum();
        int momentum2 = dosData.type == null ? Integer.MAX_VALUE : dosData.type.getMomentum();
        if (momentum1 != momentum2) {
            return (momentum1 < momentum2) ? -1 : 1;
        }

        if (this.spinPolarized != dosData.spinPolarized) {
            if (!this.spinPolarized) {
                return -1;
            } else if (!dosData.spinPolarized) {
                return 1;
            }
        }

        if (this.atomName != null) {
            return this.atomName.compareTo(dosData.atomName);
        } else if (this.atomName != dosData.atomName) {
            return -1;
        }

        return 0;
    }

    @Override
    public String toString() {
        return this.file.toString();
    }

    @Override
    public int hashCode() {
        return this.file.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        return this.file.equals(((DosData) obj).file);
    }
}
