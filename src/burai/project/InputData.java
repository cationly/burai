/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.project;

import java.io.File;
import java.io.IOException;

import burai.input.QEInput;

public class InputData {

    private String fileName;

    private QEInput qeInput;

    private InputGenerator inputGenerator;

    public InputData(String fileName) {
        String fileName2 = fileName == null ? null : fileName.trim();
        if (fileName2 == null || fileName2.isEmpty()) {
            throw new IllegalArgumentException("file name is empty.");
        }

        this.fileName = fileName2;
        this.qeInput = null;
        this.inputGenerator = null;
    }

    public String getFileName() {
        return this.fileName;
    }

    public QEInput getQEInput() {
        if (this.qeInput == null) {
            try {
                this.generateQEInput(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return this.qeInput;
    }

    public void setQEInput(QEInput qeInput) {
        this.qeInput = qeInput;
    }

    public void setInputGenerator(InputGenerator inputGenerator) {
        this.inputGenerator = inputGenerator;
    }

    private void generateQEInput(File file) throws IOException {
        if (this.inputGenerator != null) {
            this.qeInput = this.inputGenerator.generate(file);
        }
    }

    public void resolveQEInput() {
        if (this.qeInput == null) {
            try {
                this.generateQEInput(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (this.qeInput != null) {
            this.qeInput.reload();
        }
    }

    public void requestQEInput(Project project) {
        if (this.qeInput != null) {
            return;
        }

        try {
            this.loadQEInput(project);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadQEInput(Project project) throws IOException {
        if (project == null) {
            return;
        }

        String directoryPath = project.getDirectoryPath();
        if (directoryPath == null || directoryPath.trim().isEmpty()) {
            throw new IOException("directoryPath is empty.");
        }

        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            throw new IOException("no such directory: " + directory);
        }

        File file = new File(directory, this.fileName);
        if (!file.exists()) {
            throw new IOException("no such file: " + file);
        }
        if (!file.canRead()) {
            throw new IOException("cannot read file: " + file);
        }

        this.generateQEInput(file);
    }
}
