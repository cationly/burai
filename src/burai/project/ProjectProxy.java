/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.project;

import java.io.IOException;

import burai.atoms.model.Cell;
import burai.input.QEInput;
import burai.project.property.ProjectProperty;

public class ProjectProxy extends Project {

    private Project netProject;

    protected ProjectProxy(String rootFilePath, String directoryPath) {
        super(rootFilePath, directoryPath);

        this.netProject = null;

        this.addOnInputModeChanged(inputMode -> {
            if (this.netProject != null) {
                this.netProject.setInputMode(this.getInputMode());
            }
        });

        this.addOnFilePathChanged(filePath -> {
            if (this.netProject != null) {
                if (this.getRootFilePath() != null) {
                    this.netProject.setRootFilePath(this.getRootFilePath());
                }

                if (this.getDirectoryPath() != null) {
                    this.netProject.setDirectoryPath(this.getDirectoryPath());
                }
            }
        });
    }

    private Project getNetProject() {
        if (this.netProject == null) {
            try {
                this.netProject = new ProjectBody(this.getRootFilePath(), this.getDirectoryPath());
                this.netProject.setInputMode(this.getInputMode());

            } catch (IOException e) {
                this.netProject = null;
            }
        }

        return this.netProject;
    }

    @Override
    public void setNetProject(Project project) {
        if (project == null) {
            this.netProject = null;
            return;
        }

        if (this.getRootFilePath() != null) {
            if (!this.getRootFilePath().equals(project.getRootFilePath())) {
                throw new IllegalArgumentException("project is incorrect at rootFilePath.");
            }
        } else {
            if (this.getRootFilePath() != project.getRootFilePath()) {
                throw new IllegalArgumentException("project is incorrect at rootFilePath.");
            }
        }

        if (this.getDirectoryPath() != null) {
            if (!this.getDirectoryPath().equals(project.getDirectoryPath())) {
                throw new IllegalArgumentException("project is incorrect at directoryPath.");
            }
        } else {
            if (this.getDirectoryPath() != project.getDirectoryPath()) {
                throw new IllegalArgumentException("project is incorrect at directoryPath.");
            }
        }

        if (this.getInputMode() != project.getInputMode()) {
            this.setInputMode(project.getInputMode());
        }

        this.netProject = project;
    }

    @Override
    public boolean isValid() {
        return this.getNetProject() != null;
    }

    @Override
    public boolean isSameAs(Project project) {
        if (project == null) {
            return false;
        }

        Project project2 = project;
        while (project2 != null && (project2 instanceof ProjectProxy)) {
            project2 = ((ProjectProxy) project2).netProject;
        }

        if (this.netProject != null) {
            return this.netProject.isSameAs(project2);
        } else {
            return false;
        }
    }

    @Override
    public ProjectProperty getProperty() {
        return this.getNetProject() == null ? null : this.netProject.getProperty();
    }

    @Override
    public String getPrefixName() {
        return this.getNetProject() == null ? null : this.netProject.getPrefixName();
    }

    @Override
    public String getInpFileName() {
        return this.getNetProject() == null ? null : this.netProject.getInpFileName();
    }

    @Override
    public String getLogFileName(int i) {
        return this.getNetProject() == null ? null : this.netProject.getLogFileName(i);
    }

    @Override
    public String getErrFileName(int i) {
        return this.getNetProject() == null ? null : this.netProject.getErrFileName(i);
    }

    @Override
    public String getExitFileName() {
        return this.getNetProject() == null ? null : this.netProject.getExitFileName();
    }

    @Override
    public QEInput getQEInputGeometry() {
        return this.getNetProject() == null ? null : this.netProject.getQEInputGeometry();
    }

    @Override
    public QEInput getQEInputScf() {
        return this.getNetProject() == null ? null : this.netProject.getQEInputScf();
    }

    @Override
    public QEInput getQEInputOptimiz() {
        return this.getNetProject() == null ? null : this.netProject.getQEInputOptimiz();
    }

    @Override
    public QEInput getQEInputMd() {
        return this.getNetProject() == null ? null : this.netProject.getQEInputMd();
    }

    @Override
    public QEInput getQEInputDos() {
        return this.getNetProject() == null ? null : this.netProject.getQEInputDos();
    }

    @Override
    public QEInput getQEInputBand() {
        return this.getNetProject() == null ? null : this.netProject.getQEInputBand();
    }

    @Override
    public Cell getCell() {
        return this.getNetProject() == null ? null : this.netProject.getCell();
    }

    @Override
    protected void loadQEInputs() {
        if (this.getNetProject() != null) {
            this.netProject.loadQEInputs();
        }
    }

    @Override
    public void resolveQEInputs() {
        if (this.getNetProject() != null) {
            this.netProject.resolveQEInputs();
        }
    }

    @Override
    public void markQEInputs() {
        if (this.getNetProject() != null) {
            this.netProject.markQEInputs();
        }
    }

    @Override
    public boolean isQEInputChanged() {
        if (this.getNetProject() != null) {
            return this.netProject.isQEInputChanged();
        }

        return false;
    }

    @Override
    public void saveQEInputs(String directoryPath) {
        this.loadQEInputs();

        if (directoryPath != null && (!directoryPath.trim().isEmpty())) {
            this.setDirectoryPath(directoryPath);
        }

        if (this.getNetProject() != null) {
            this.netProject.saveQEInputs(directoryPath);
        }
    }
}
