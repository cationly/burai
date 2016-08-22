/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.matapi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import burai.com.env.Environments;
import burai.com.file.FileTools;

public class MaterialsAPILoader {

    private static final String[] DIRECTORY_IDS = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };

    private static int directoryID = initDirectoryID();

    private static int initDirectoryID() {
        int iMin = 0;
        long timeMin = 0L;

        for (int i = 0; i < DIRECTORY_IDS.length; i++) {
            String id = DIRECTORY_IDS[i];
            String dirPath = Environments.getMaterialsAPIPath(id);
            dirPath = dirPath == null ? null : dirPath.trim();
            if (dirPath == null || dirPath.isEmpty()) {
                continue;
            }

            try {
                File file = new File(dirPath);
                long time = file.lastModified();
                if (i == 0 || time < timeMin) {
                    iMin = i;
                    timeMin = time;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return iMin;
    }

    private synchronized static String getCurrentDirectoryPath() {
        String dirPath = Environments.getMaterialsAPIPath(DIRECTORY_IDS[directoryID]);
        dirPath = dirPath == null ? null : dirPath.trim();
        if (dirPath == null || dirPath.isEmpty()) {
            return null;
        }

        directoryID++;
        if (directoryID >= DIRECTORY_IDS.length) {
            directoryID = 0;
        }

        return dirPath;
    }

    private boolean alive;

    private boolean finished;

    private boolean cleaningDir;

    private File directory;

    private List<File> cifFiles;

    private MaterialsAPI matAPI;

    public MaterialsAPILoader(String formula) {
        if (formula == null || formula.trim().isEmpty()) {
            throw new IllegalArgumentException("formula is empty.");
        }

        MaterialsAPIHolder.getInstance().deleteLoader();
        MaterialsAPIHolder.getInstance().setLoader(this);

        this.alive = true;

        this.finished = false;

        this.cleaningDir = true;

        this.setupDirectory();
        if (this.directory == null) {
            throw new RuntimeException("directory is null.");
        }

        this.cleanDirectory();

        this.cifFiles = new ArrayList<File>();

        this.matAPI = new MaterialsAPI(formula);

        this.loadCIFFiles();
    }

    public MaterialsAPIQueue getQueue() {
        return new MaterialsAPIQueue(this);
    }

    public File getDirectory() {
        return this.directory;
    }

    public String getFormula() {
        return this.matAPI.getFormula();
    }

    public int numMaterialIDs() {
        return this.matAPI.numMaterialIDs();
    }

    public synchronized int numCIFFiles() {
        return this.cifFiles.size();
    }

    protected int numCIFFilesNosync() {
        return this.cifFiles.size();
    }

    public synchronized File getCIFFile(int index) throws IndexOutOfBoundsException {
        return this.cifFiles.get(index);
    }

    protected File getCIFFileNosync(int index) throws IndexOutOfBoundsException {
        return this.cifFiles.get(index);
    }

    protected synchronized void setToBeDead() {
        this.alive = false;
    }

    protected synchronized boolean isAlive() {
        return this.alive;
    }

    private synchronized void setToBeFinished() {
        this.finished = true;
        this.notifyAll();
    }

    protected synchronized boolean isFinished() {
        return this.finished;
    }

    protected boolean isFinishedNosync() {
        return this.finished;
    }

    private void setupDirectory() {
        String dirPath = getCurrentDirectoryPath();
        if (dirPath == null || dirPath.isEmpty()) {
            this.directory = null;
            return;
        }

        this.directory = new File(dirPath);
    }

    private void cleanDirectory() {
        Thread thread = new Thread(() -> {
            try {
                File[] files = this.directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        FileTools.deleteAllFiles(file, false);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            synchronized (this) {
                this.cleaningDir = false;
                this.notifyAll();
            }
        });

        thread.start();
    }

    private void loadCIFFiles() {
        Thread thread = new Thread(() -> {
            this.preloadMaterialsData();
            this.loadMaterialsData();
        });

        thread.start();
    }

    private void preloadMaterialsData() {
        int numIDs = this.matAPI.numMaterialIDs();
        for (int i = 0; i < numIDs; i++) {
            synchronized (this) {
                if (!this.cleaningDir) {
                    break;
                }
            }

            this.matAPI.getMaterialData(i);
        }

        synchronized (this) {
            while (this.cleaningDir) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadMaterialsData() {
        int numIDs = this.matAPI.numMaterialIDs();
        for (int i = 0; i < numIDs; i++) {
            String matID = this.matAPI.getMaterialID(i);
            matID = matID == null ? null : matID.trim();
            if (matID == null || matID.isEmpty()) {
                continue;
            }

            MaterialData matData = this.matAPI.getMaterialData(i);
            if (matData == null) {
                continue;
            }

            String cifData = matData.getCIF();
            if (cifData == null || cifData.trim().isEmpty()) {
                continue;
            }

            File cifFile = new File(this.directory, matID + ".cif");

            if (!this.isAlive()) {
                break;
            }

            try {
                this.writeCIFFile(cifData, cifFile);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            if (!this.isAlive()) {
                break;
            }

            synchronized (this) {
                this.cifFiles.add(cifFile);
                this.notifyAll();
            }
        }

        this.setToBeFinished();
    }

    private void writeCIFFile(String cifData, File cifFile) throws IOException {
        if (cifData == null) {
            return;
        }

        if (cifFile == null) {
            return;
        }

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(cifFile)));
            writer.println(cifData);

        } catch (IOException e) {
            throw e;

        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
