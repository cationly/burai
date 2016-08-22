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
import java.util.ArrayList;
import java.util.List;

import burai.atoms.model.Cell;
import burai.atoms.reader.AtomsReader;
import burai.input.QEInput;
import burai.project.property.ProjectProperty;

public abstract class Project {

    public static final int INPUT_MODE_GEOMETRY = 0;
    public static final int INPUT_MODE_SCF = 1;
    public static final int INPUT_MODE_OPTIMIZ = 2;
    public static final int INPUT_MODE_MD = 3;
    public static final int INPUT_MODE_DOS = 4;
    public static final int INPUT_MODE_BAND = 5;
    public static final int INPUT_MODE_NEB = 6;
    public static final int INPUT_MODE_TDDFT = 7;
    public static final int INPUT_MODE_PHONON = 8;

    public static boolean isProjectDirectory(String path) {
        if (path == null) {
            return false;
        }

        String path2 = path.trim();
        if (path2.isEmpty()) {
            return false;
        }

        return ProjectProperty.hasStatus(path2);
    }

    public static boolean isProjectFile(String path) {
        if (path == null) {
            return false;
        }

        String path2 = path.trim();
        if (path2.isEmpty()) {
            return false;
        }

        File file = new File(path);

        if (file.isDirectory()) {
            if (Project.isProjectDirectory(file.getPath())) {
                return true;
            }

        } else {
            if (AtomsReader.isToBeInstance(file.getPath())) {
                return true;
            }
        }

        return false;
    }

    public static Project getInstance(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path is empty.");

        }

        File file = new File(path);
        if (file.isDirectory()) {
            return new ProjectProxy(null, path);
        } else {
            return new ProjectProxy(path, null);
        }
    }

    private int inputMode;

    private String rootFilePath;

    private String directoryPath;

    private List<InputModeChanged> onInputModeChangedList;

    private List<FilePathChanged> onFilePathChangedList;

    protected Project(String rootFilePath, String directoryPath) {
        String rootFilePath2 = rootFilePath == null ? null : rootFilePath.trim();
        String directoryPath2 = directoryPath == null ? null : directoryPath.trim();

        boolean rootFileEmpty = (rootFilePath2 == null || rootFilePath2.isEmpty());
        boolean directoryEmpty = (directoryPath2 == null || directoryPath2.isEmpty());

        if (rootFileEmpty && directoryEmpty) {
            throw new IllegalArgumentException("both rootFilePath and directoryPath are empty.");
        }

        this.inputMode = INPUT_MODE_GEOMETRY;

        this.rootFilePath = rootFilePath2;
        this.directoryPath = directoryPath2;

        this.onInputModeChangedList = null;
        this.onFilePathChangedList = null;
    }

    public int getInputMode() {
        return this.inputMode;
    }

    public void setInputMode(int inputMode) {
        this.inputMode = inputMode;

        if (this.onInputModeChangedList != null) {
            for (InputModeChanged onInputModeChanged : this.onInputModeChangedList) {
                if (onInputModeChanged != null) {
                    onInputModeChanged.onInputModeChanged(this.inputMode);
                }
            }
        }
    }

    public String getRootFilePath() {
        return this.rootFilePath;
    }

    public String getRootFileName() {
        if (this.rootFilePath == null) {
            return null;
        }

        File file = new File(this.rootFilePath);
        return file.getName();
    }

    protected void setRootFilePath(String rootFilePath) {
        if (rootFilePath == null) {
            return;
        }

        String rootFilePath2 = rootFilePath.trim();
        if (rootFilePath2.isEmpty()) {
            return;
        }

        this.rootFilePath = rootFilePath2;
        this.directoryPath = null;

        if (this.onFilePathChangedList != null) {
            for (FilePathChanged onFilePathChanged : this.onFilePathChangedList) {
                if (onFilePathChanged != null) {
                    onFilePathChanged.onFilePathChanged(this.rootFilePath);
                }
            }
        }
    }

    public String getDirectoryPath() {
        return this.directoryPath;
    }

    public String getDirectoryName() {
        if (this.directoryPath == null) {
            return null;
        }

        File file = new File(this.directoryPath);
        return file.getName();
    }

    protected void setDirectoryPath(String directoryPath) {
        if (directoryPath == null) {
            return;
        }

        String directoryPath2 = directoryPath.trim();
        if (directoryPath2.isEmpty()) {
            return;
        }

        this.rootFilePath = null;
        this.directoryPath = directoryPath2;

        if (this.onFilePathChangedList != null) {
            for (FilePathChanged onFilePathChanged : this.onFilePathChangedList) {
                if (onFilePathChanged != null) {
                    onFilePathChanged.onFilePathChanged(this.directoryPath);
                }
            }
        }
    }

    public void addOnInputModeChanged(InputModeChanged onInputModeChanged) {
        if (onInputModeChanged != null) {
            if (this.onInputModeChangedList == null) {
                this.onInputModeChangedList = new ArrayList<InputModeChanged>();
            }

            this.onInputModeChangedList.add(onInputModeChanged);
        }
    }

    public void removeOnInputModeChanged(InputModeChanged onInputModeChanged) {
        if (onInputModeChanged != null) {
            if (this.onInputModeChangedList != null) {
                this.onInputModeChangedList.remove(onInputModeChanged);
            }
        }
    }

    public void addOnFilePathChanged(FilePathChanged onFilePathChanged) {
        if (onFilePathChanged != null) {
            if (this.onFilePathChangedList == null) {
                this.onFilePathChangedList = new ArrayList<FilePathChanged>();
            }

            this.onFilePathChangedList.add(onFilePathChanged);
        }
    }

    public void removeOnFilePathChanged(FilePathChanged onFilePathChanged) {
        if (onFilePathChanged != null) {
            if (this.onFilePathChangedList != null) {
                this.onFilePathChangedList.remove(onFilePathChanged);
            }
        }
    }

    public String getRelatedFilePath() {
        if (this.rootFilePath != null) {
            return this.rootFilePath;
        }

        if (this.directoryPath != null) {
            return this.directoryPath;
        }

        return null;
    }

    public String getRelatedFileName() {
        String filePath = this.getRelatedFilePath();
        if (filePath == null) {
            return null;
        }

        File file = new File(filePath);
        return file.getName();
    }

    public void renameRelatedFile(String filePath) {
        if (filePath == null) {
            return;
        }

        String filePath2 = filePath.trim();
        if (filePath2.isEmpty()) {
            return;
        }

        if (this.rootFilePath != null) {
            this.setRootFilePath(filePath2);
        } else if (this.directoryPath != null) {
            this.setDirectoryPath(filePath2);
        }
    }

    public boolean isRelatedFile(String filePath) {
        if (filePath == null) {
            return false;
        }

        String filePath2 = filePath.trim();
        return filePath2.equals(this.rootFilePath) || filePath2.equals(this.directoryPath);
    }

    public boolean isRelatedFile(File file) {
        if (file == null) {
            return false;
        }

        return this.isRelatedFile(file.getPath());
    }

    public QEInput getQEInputCurrent() {
        switch (this.inputMode) {

        case INPUT_MODE_GEOMETRY:
            return this.getQEInputGeometry();

        case INPUT_MODE_SCF:
            return this.getQEInputScf();

        case INPUT_MODE_OPTIMIZ:
            return this.getQEInputOptimiz();

        case INPUT_MODE_MD:
            return this.getQEInputMd();

        case INPUT_MODE_DOS:
            return this.getQEInputDos();

        case INPUT_MODE_BAND:
            return this.getQEInputBand();

        default:
            return this.getQEInputGeometry();
        }
    }

    public abstract void setNetProject(Project project);

    public abstract boolean isValid();

    public abstract boolean isSameAs(Project project);

    public abstract ProjectProperty getProperty();

    public abstract String getPrefixName();

    public abstract String getInpFileName();

    public abstract String getLogFileName(int i);

    public abstract String getErrFileName(int i);

    public abstract String getExitFileName();

    public abstract QEInput getQEInputGeometry();

    public abstract QEInput getQEInputScf();

    public abstract QEInput getQEInputOptimiz();

    public abstract QEInput getQEInputMd();

    public abstract QEInput getQEInputDos();

    public abstract QEInput getQEInputBand();

    public abstract Cell getCell();

    protected abstract void loadQEInputs();

    public abstract void resolveQEInputs();

    public abstract void markQEInputs();

    public abstract boolean isQEInputChanged();

    public abstract void saveQEInputs(String directoryPath);

    public void saveQEInputs() {
        this.saveQEInputs(null);
    }

    @Override
    public String toString() {
        if (this.rootFilePath != null) {
            return this.getRootFileName();
        } else {
            return this.getDirectoryName();
        }
    }
}
