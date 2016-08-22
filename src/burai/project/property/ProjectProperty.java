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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import com.google.gson.Gson;

public class ProjectProperty {

    private static final String FILE_NAME_STATUS = ".burai.status";
    private static final String FILE_NAME_SCF = ".burai.elec";
    private static final String FILE_NAME_OPT = ".burai.opt";
    private static final String FILE_NAME_MD = ".burai.md";

    public static boolean hasStatus(String directoryPath) {
        if (directoryPath == null || directoryPath.isEmpty()) {
            return false;
        }

        try {
            File file = new File(directoryPath, FILE_NAME_STATUS);
            if (file.isFile()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    private String directoryPath;

    private ProjectStatus status;

    private ProjectEnergies scfEnergies;

    private ProjectGeometryList optList;

    private ProjectGeometryList mdList;

    public ProjectProperty(String directoryPath) {
        if (directoryPath == null || directoryPath.isEmpty()) {
            throw new IllegalArgumentException("directoryPath is empty.");
        }

        this.directoryPath = directoryPath;

        this.status = null;
        this.scfEnergies = null;
        this.optList = null;
        this.mdList = null;
    }

    public synchronized void copyProperty(ProjectProperty property) {
        if (property == null) {
            return;
        }

        this.status = property.getStatus();
        this.scfEnergies = property.getScfEnergies();
        this.optList = property.getOptList();
        this.mdList = property.getMdList();
    }

    public void saveProperty() {
        this.saveStatus();
        this.saveScfEnergies();
        this.saveOptList();
        this.saveMdList();
    }

    public synchronized ProjectStatus getStatus() {
        if (this.status == null) {
            this.createStatus();
        }

        return this.status;
    }

    public synchronized ProjectEnergies getScfEnergies() {
        if (this.scfEnergies == null) {
            this.createScfEnergies();
        }

        return this.scfEnergies;
    }

    public synchronized ProjectGeometryList getOptList() {
        if (this.optList == null) {
            this.createOptList();
        }

        return this.optList;
    }

    public synchronized ProjectGeometryList getMdList() {
        if (this.mdList == null) {
            this.createMdList();
        }

        return this.mdList;
    }

    private void createStatus() {
        try {
            this.status = this.<ProjectStatus> readFile(FILE_NAME_STATUS, ProjectStatus.class);
        } catch (IOException e) {
            this.status = null;
        }

        if (this.status == null) {
            this.status = new ProjectStatus();
        }
    }

    private void createScfEnergies() {
        try {
            this.scfEnergies = this.<ProjectEnergies> readFile(FILE_NAME_SCF, ProjectEnergies.class);
        } catch (IOException e) {
            this.scfEnergies = null;
        }

        if (this.scfEnergies == null) {
            this.scfEnergies = new ProjectEnergies();
        }
    }

    private void createOptList() {
        try {
            this.optList = this.<ProjectGeometryList> readFile(FILE_NAME_OPT, ProjectGeometryList.class);
        } catch (IOException e) {
            this.optList = null;
        }

        if (this.optList == null) {
            this.optList = new ProjectGeometryList();
        }
    }

    private void createMdList() {
        try {
            this.mdList = this.<ProjectGeometryList> readFile(FILE_NAME_MD, ProjectGeometryList.class);
        } catch (IOException e) {
            this.mdList = null;
        }

        if (this.mdList == null) {
            this.mdList = new ProjectGeometryList();
        }
    }

    public synchronized void saveStatus() {
        if (this.status == null) {
            this.createStatus();
        }

        try {
            this.<ProjectStatus> writeFile(FILE_NAME_STATUS, this.status);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void saveScfEnergies() {
        try {
            this.<ProjectEnergies> writeFile(FILE_NAME_SCF, this.scfEnergies);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void saveOptList() {
        try {
            this.<ProjectGeometryList> writeFile(FILE_NAME_OPT, this.optList);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void saveMdList() {
        try {
            this.<ProjectGeometryList> writeFile(FILE_NAME_MD, this.mdList);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T> T readFile(String fileName, Class<T> classT) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        T objT = null;
        Reader reader = null;

        try {
            File file = new File(this.directoryPath, fileName);
            if (!file.isFile()) {
                return null;
            }

            reader = new BufferedReader(new FileReader(file));

            Gson gson = new Gson();
            objT = gson.<T> fromJson(reader, classT);

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

        return objT;
    }

    private <T> void writeFile(String fileName, T objT) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        if (objT == null) {
            return;
        }

        Writer writer = null;

        try {
            File file = new File(this.directoryPath, fileName);
            writer = new BufferedWriter(new FileWriter(file));

            Gson gson = new Gson();
            gson.toJson(objT, writer);

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
}
