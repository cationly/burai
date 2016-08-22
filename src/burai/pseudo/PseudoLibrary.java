/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.pseudo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import burai.com.env.Environments;
import burai.com.life.Life;
import burai.com.parallel.Parallel;

import com.google.gson.Gson;

public class PseudoLibrary {

    private static final double SMALL_ZVAL = 1.0e-8;
    private static final double SMALL_ECUT = 1.0e-8;
    private static final double DEFAULT_ECUT_WFC = 25.0;
    private static final double DEFAULT_ECUT_RHO = 225.0;
    private static final int DEFAULT_NUM_WFC = 1000;
    private static final int DEFAULT_NUM_PRJ = 1000;

    private static final long SLEEPING_TIME = 2500L;

    private static final int NUM_LOADING_THREADS =
            Math.min(Math.max(1, (int) (0.5 * Environments.getNumCUPs())), Environments.getNumCUPs() - 1);

    private static PseudoLibrary instance = null;

    public static PseudoLibrary getInstance() {
        if (instance == null) {
            instance = new PseudoLibrary();
        }

        return instance;
    }

    private boolean alive;

    private boolean loaded;

    private Object loadedLock;

    private Map<File, PseudoPotential> pseudoPots;

    private PseudoLibrary() {
        this.alive = true;
        this.loaded = false;
        this.loadedLock = new Object();
        this.pseudoPots = null;

        this.runReloadingThread();

        Life.getInstance().addOnDead(() -> this.stop());
    }

    private synchronized boolean isAlive() {
        return this.alive;
    }

    private boolean isLoaded() {
        if (this.loadedLock == null) {
            return this.loaded;
        }

        synchronized (this.loadedLock) {
            return this.loaded;
        }
    }

    private void setLoaded() {
        if (this.loadedLock == null) {
            this.loaded = true;

        } else {
            synchronized (this.loadedLock) {
                this.loaded = true;
                this.loadedLock.notifyAll();
            }
        }
    }

    public void waitToLoad() {
        if (this.loadedLock == null) {
            return;
        }

        synchronized (this.loadedLock) {
            while (!this.loaded) {
                try {
                    this.loadedLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void runReloadingThread() {
        Thread thread = new Thread(() -> {
            while (this.isAlive()) {
                this.reload();

                if (!this.isAlive()) {
                    break;
                }

                synchronized (this) {
                    try {
                        this.wait(SLEEPING_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    public void touch() {
        // NOP
    }

    public synchronized void stop() {
        this.alive = false;
        this.notifyAll();
    }

    public synchronized void reload() {
        try {
            File pseudosDir = null;
            String pseudosPath = Environments.getPseudosPath();
            if (pseudosPath != null && (!pseudosPath.isEmpty())) {
                pseudosDir = new File(pseudosPath);
            }

            if (pseudosDir == null || (!pseudosDir.isDirectory())) {
                if (this.pseudoPots != null) {
                    this.pseudoPots.clear();
                }
                return;
            }

            try {
                this.readPseudoList();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            int numReloaded = 0;
            Map<File, PseudoPotential> pseudoPots2 = new HashMap<File, PseudoPotential>();

            File[] pseudosFiles = pseudosDir.listFiles();
            if (pseudosFiles != null && pseudosFiles.length > 0) {
                Parallel<File, Integer> parallel = new Parallel<File, Integer>(pseudosFiles);
                parallel.setNumThreads(NUM_LOADING_THREADS);
                parallel.setSumRule(Parallel.integerSumRule());

                numReloaded = parallel.forEach(pseudoFile -> {
                    if (pseudoFile == null || (!this.isUPF(pseudoFile))) {
                        return 0;
                    }

                    boolean reloaded = false;
                    PseudoPotential pseudoPot = null;

                    if (this.pseudoPots != null && this.pseudoPots.containsKey(pseudoFile)) {
                        pseudoPot = this.pseudoPots.get(pseudoFile);
                        if (pseudoPot != null) {
                            reloaded = pseudoPot.reload();
                        }

                    } else {
                        pseudoPot = new PseudoPotential(pseudoFile);
                        reloaded = true;
                    }

                    if (pseudoPot != null && pseudoPot.isAvairable()) {
                        synchronized (pseudoPots2) {
                            pseudoPots2.put(pseudoFile, pseudoPot);
                        }
                    }

                    return reloaded ? 1 : 0;
                });
            }

            int numPseudos1 = this.pseudoPots == null ? 0 : this.pseudoPots.size();
            int numPseudos2 = pseudoPots2.size();
            this.pseudoPots = pseudoPots2;

            if (numReloaded > 0 || numPseudos1 != numPseudos2) {
                try {
                    this.writePseudoList();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }

        } catch (Exception e3) {
            e3.printStackTrace();
            if (this.pseudoPots != null) {
                this.pseudoPots.clear();
            }

        } finally {
            this.setLoaded();
        }
    }

    private boolean isUPF(File file) {
        if (file == null) {
            return false;
        }

        String name = file.getName();
        if (name == null || name.isEmpty()) {
            return false;
        }

        int index = name.lastIndexOf('.');
        if (index < 0) {
            return false;
        }

        String ext = name.substring(index);
        return ".upf".equalsIgnoreCase(ext);
    }

    private void readPseudoList() throws IOException {
        if (this.pseudoPots != null) {
            return;
        }

        String pseudoPath = Environments.getPseudosPath();
        if (pseudoPath == null || pseudoPath.isEmpty()) {
            return;
        }

        String listPath = Environments.getPseudoListPath();
        if (listPath == null || listPath.isEmpty()) {
            return;
        }

        Reader reader = null;
        Map<String, PseudoData> pseudoMap = null;

        try {
            File file = new File(listPath);
            if (!file.isFile()) {
                return;
            }

            reader = new BufferedReader(new FileReader(file));

            Gson gson = new Gson();
            pseudoMap = gson.<PseudoDataMap> fromJson(reader, PseudoDataMap.class);

        } catch (FileNotFoundException e1) {
            throw e1;

        } catch (Exception e2) {
            throw new IOException(e2);

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e3) {
                    throw e3;
                }
            }
        }

        if (pseudoMap == null) {
            return;
        }

        Map<File, PseudoPotential> pseudoPots2 = new HashMap<File, PseudoPotential>();

        Set<Entry<String, PseudoData>> pseudoEntries = pseudoMap.entrySet();
        if (pseudoEntries != null) {
            for (Entry<String, PseudoData> pseudoEntry : pseudoEntries) {
                if (pseudoEntry == null) {
                    continue;
                }

                String pseudoName = pseudoEntry.getKey();
                File pseudoFile = pseudoName == null ? null : new File(pseudoPath, pseudoName);
                if (pseudoFile == null) {
                    continue;
                }

                PseudoData pseudoData = pseudoEntry.getValue();
                PseudoPotential pseudoPot = pseudoData == null ? null : new PseudoPotential(pseudoFile, pseudoData);
                if (pseudoPot == null || (!pseudoPot.isAvairable())) {
                    continue;
                }

                pseudoPots2.put(pseudoFile, pseudoPot);
            }
        }

        this.pseudoPots = pseudoPots2;
    }

    private void writePseudoList() throws IOException {
        if (this.pseudoPots == null) {
            return;
        }

        String listPath = Environments.getPseudoListPath();
        if (listPath == null || listPath.isEmpty()) {
            return;
        }

        Map<String, PseudoData> pseudoMap = new PseudoDataMap();

        Set<Entry<File, PseudoPotential>> pseudoEntries = this.pseudoPots.entrySet();
        if (pseudoEntries != null) {
            for (Entry<File, PseudoPotential> pseudoEntry : pseudoEntries) {
                if (pseudoEntry == null) {
                    continue;
                }

                File pseudoFile = pseudoEntry.getKey();
                String pseudoName = pseudoFile == null ? null : pseudoFile.getName();
                pseudoName = pseudoName == null ? null : pseudoName.trim();
                if (pseudoName == null || pseudoName.isEmpty()) {
                    continue;
                }

                PseudoPotential pseudoPot = pseudoEntry.getValue();
                PseudoData pseudoData = null;
                if (pseudoPot != null && pseudoPot.isAvairable()) {
                    pseudoData = pseudoPot.getData();
                }
                if (pseudoData == null) {
                    continue;
                }

                pseudoMap.put(pseudoName, pseudoData);
            }
        }

        Writer writer = null;

        try {
            File file = new File(listPath);
            writer = new BufferedWriter(new FileWriter(file));

            Gson gson = new Gson();
            gson.toJson(pseudoMap, writer);

        } catch (IOException e1) {
            throw e1;

        } catch (Exception e2) {
            throw new IOException(e2);

        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e3) {
                    throw e3;
                }
            }
        }
    }

    public PseudoPotential peekPseudoPotential(String pseudoName) {
        if (pseudoName == null || pseudoName.trim().isEmpty()) {
            return null;
        }

        String pseudosPath = Environments.getPseudosPath();
        if (pseudosPath == null || pseudosPath.isEmpty()) {
            return null;
        }

        File pseudoFile = new File(pseudosPath, pseudoName);
        return this.peekPseudoPotential(pseudoFile);
    }

    public synchronized PseudoPotential peekPseudoPotential(File pseudoFile) {
        if (pseudoFile == null) {
            return null;
        }

        if (this.pseudoPots != null && this.pseudoPots.containsKey(pseudoFile)) {
            return this.pseudoPots.get(pseudoFile);
        }

        return null;
    }

    public PseudoPotential getPseudoPotential(String element) {
        final int[] functionals = {
                PseudoData.FUNCTIONAL_PBE,
                PseudoData.FUNCTIONAL_PZ,
                PseudoData.FUNCTIONAL_UNKNOWN };

        final int[] pseudoTypes = {
                PseudoData.PSEUDO_TYPE_NC,
                PseudoData.PSEUDO_TYPE_US,
                PseudoData.PSEUDO_TYPE_PAW,
                PseudoData.PSEUDO_TYPE_UNKNOWN };

        for (int functional : functionals) {
            for (int pseudoType : pseudoTypes) {
                PseudoPotential[] pseudoPots = this.listPseudoPotentials(element, pseudoType, functional);
                if (pseudoPots != null) {
                    if (pseudoPots.length > 1) {
                        this.sortPseudoPotentials(pseudoPots);
                    }
                    if (pseudoPots.length > 0) {
                        return pseudoPots[0];
                    }
                }
            }
        }

        return null;
    }

    private void sortPseudoPotentials(PseudoPotential[] pseudoPots) {
        if (pseudoPots == null) {
            return;
        }

        Arrays.sort(pseudoPots, (pseudoPot1, pseudoPot2) -> {
            if (pseudoPot1 == null && pseudoPot2 == null) {
                return 0;
            } else if (pseudoPot1 != null && pseudoPot2 == null) {
                return -1;
            } else if (pseudoPot1 == null && pseudoPot2 != null) {
                return 1;
            }

            int rel1 = 0;
            int rel2 = 0;
            if (pseudoPot1.getData().hasRelativistic()) {
                rel1 = pseudoPot1.getData().getRelativistic();
            } else {
                rel1 = PseudoData.RELATIVISTIC_SCALAR;
            }
            if (pseudoPot2.getData().hasRelativistic()) {
                rel2 = pseudoPot2.getData().getRelativistic();
            } else {
                rel2 = PseudoData.RELATIVISTIC_SCALAR;
            }
            if (rel1 != rel2) {
                if (rel1 == PseudoData.RELATIVISTIC_SCALAR) {
                    return -1;
                } else if (rel2 == PseudoData.RELATIVISTIC_SCALAR) {
                    return 1;
                }
            }

            double zval1 = pseudoPot1.getData().getZValence();
            double zval2 = pseudoPot2.getData().getZValence();
            if (Math.abs(zval1 - zval2) > SMALL_ZVAL) {
                return (zval1 < zval2) ? -1 : 1;
            }

            double ewfc1 = pseudoPot1.getData().getWfcCutoff();
            double ewfc2 = pseudoPot2.getData().getWfcCutoff();
            ewfc1 = ewfc1 > SMALL_ECUT ? ewfc1 : DEFAULT_ECUT_WFC;
            ewfc2 = ewfc2 > SMALL_ECUT ? ewfc2 : DEFAULT_ECUT_WFC;
            if (Math.abs(ewfc1 - ewfc2) > SMALL_ECUT) {
                return (ewfc1 < ewfc2) ? -1 : 1;
            }

            double erho1 = pseudoPot1.getData().getRhoCutoff();
            double erho2 = pseudoPot2.getData().getRhoCutoff();
            erho1 = erho1 > SMALL_ECUT ? erho1 : DEFAULT_ECUT_RHO;
            erho2 = erho2 > SMALL_ECUT ? erho2 : DEFAULT_ECUT_RHO;
            if (Math.abs(erho1 - erho2) > SMALL_ECUT) {
                return (erho1 < erho2) ? -1 : 1;
            }

            int nwfc1 = pseudoPot1.getData().getNumberOfWfc();
            int nwfc2 = pseudoPot2.getData().getNumberOfWfc();
            nwfc1 = nwfc1 > 0 ? nwfc1 : DEFAULT_NUM_WFC;
            nwfc2 = nwfc2 > 0 ? nwfc2 : DEFAULT_NUM_WFC;
            if (nwfc1 < nwfc2) {
                return -1;
            } else if (nwfc1 > nwfc2) {
                return 1;
            }

            int nprj1 = pseudoPot1.getData().getNumberOfProj();
            int nprj2 = pseudoPot2.getData().getNumberOfProj();
            nprj1 = nprj1 > 0 ? nprj1 : DEFAULT_NUM_PRJ;
            nprj2 = nprj2 > 0 ? nprj2 : DEFAULT_NUM_PRJ;
            if (nprj1 < nprj2) {
                return -1;
            } else if (nprj1 > nprj2) {
                return 1;
            }

            return 0;
        });
    }

    public PseudoPotential[] listPseudoPotentials(String element) {
        return this.listPseudoPotentials(element,
                PseudoData.PSEUDO_TYPE_UNKNOWN, PseudoData.FUNCTIONAL_UNKNOWN);
    }

    public PseudoPotential[] listPseudoPotentials(String element, int pseudoType, int functional) {
        if (!this.isLoaded()) {
            return null;
        }

        if (element == null) {
            return null;
        }

        String elementTrim = element.trim();
        if (elementTrim == null || elementTrim.isEmpty()) {
            return null;
        }

        List<PseudoPotential> pseudoList = null;

        synchronized (this) {
            if (this.pseudoPots != null && (!this.pseudoPots.isEmpty())) {
                pseudoList = new ArrayList<PseudoPotential>();
                Collection<PseudoPotential> pseudoColl = this.pseudoPots.values();

                for (PseudoPotential pseudoPot : pseudoColl) {
                    String element2 = pseudoPot.getData().getElement();
                    if (element2 == null) {
                        continue;
                    }

                    String elementTrim2 = element2.trim();
                    if (elementTrim2 == null || elementTrim2.isEmpty()) {
                        continue;
                    }

                    if (elementTrim.equalsIgnoreCase(elementTrim2)) {
                        int pseudoType2 = pseudoPot.getData().getPseudoType();
                        boolean okPseudoType =
                                (pseudoType == PseudoData.PSEUDO_TYPE_UNKNOWN || pseudoType == pseudoType2);

                        int functional2 = pseudoPot.getData().getFunctional();
                        boolean okFunctional =
                                (functional == PseudoData.FUNCTIONAL_UNKNOWN || functional == functional2);

                        if (okPseudoType && okFunctional) {
                            pseudoList.add(pseudoPot);
                        }
                    }
                }
            }
        }

        if (pseudoList == null || pseudoList.isEmpty()) {
            return null;
        }

        if (pseudoList.size() > 1) {
            Collections.sort(pseudoList);
        }

        return pseudoList.toArray(new PseudoPotential[pseudoList.size()]);
    }
}
