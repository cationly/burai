/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.icon;

import java.io.File;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import burai.project.Project;
import burai.run.RunningManager;
import burai.run.RunningNode;

public abstract class QEFXIcon {

    private File correspondingFile;

    private StringProperty caption;

    private StringProperty subCaption;

    public static QEFXIcon getInstance(File file) {
        return getInstance(file == null ? null : file.getPath());
    }

    public static QEFXIcon getInstance(File file, Project refProject) {
        return getInstance(file == null ? null : file.getPath(), refProject);
    }

    public static QEFXIcon getInstance(File file, RunningNode refRunningNode) {
        return getInstance(file == null ? null : file.getPath(), refRunningNode);
    }

    public static QEFXIcon getInstance(String path) {
        return getInstance(path, (Project) null);
    }

    public static QEFXIcon getInstance(String path, Project refProject) {
        RunningNode queuedNode = null;
        if (path != null && (!path.trim().isEmpty())) {
            queuedNode = RunningManager.getInstance().getNode(path);
        }

        Project queuedProject = null;
        if (queuedNode != null) {
            queuedProject = queuedNode.getProject();
        }

        if (refProject == null || refProject.isSameAs(queuedProject)) {
            return getInstance(path, null, queuedNode);
        } else {
            return getInstance(path, refProject, null);
        }
    }

    public static QEFXIcon getInstance(String path, RunningNode refRunningNode) {
        return getInstance(path, null, refRunningNode);
    }

    private static QEFXIcon getInstance(String path, Project refProject, RunningNode refRunningNode) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }

        try {
            File file = new File(path.trim());
            if (file.exists()) {
                // file exists.

                QEFXIcon icon = null;
                if (Project.isProjectFile(file.getPath())) {
                    // file is involved with project.
                    Project project = Project.getInstance(file.getPath());
                    RunningNode runningNode = null;

                    if (refProject != null) {
                        // has referenced project.
                        project.setNetProject(refProject);

                    } else if (refRunningNode != null) {
                        // has referenced RunningNode.
                        runningNode = refRunningNode;
                        Project runningProject = runningNode.getProject();
                        if (runningProject != null) {
                            project.setNetProject(runningProject);
                        }
                    }

                    if (runningNode == null) {
                        icon = new QEFXProjectIcon(project);
                    } else {
                        icon = new QEFXRunningIcon(project, runningNode);
                    }

                } else if (file.isDirectory()) {
                    // file is directory (but not with project).
                    icon = new QEFXFolderIcon(file.getPath());

                } else {
                    String lowerPath = file.getPath().trim().toLowerCase();
                    if (lowerPath.endsWith(".upf")) {
                        // file is UPF.
                        icon = new QEFXUPFIcon(file.getPath());

                    } else if (lowerPath.endsWith(".html")) {
                        // file is HTML.
                        icon = new QEFXWebIcon(file);
                    }
                }

                if (icon != null) {
                    icon.correspondingFile = file;
                    return icon;
                }

            } else {
                // file does not exist, to be website.
                return new QEFXWebIcon(path);
            }

        } catch (Exception e) {
            return null;
        }

        return null;
    }

    public QEFXIcon() {
        this.correspondingFile = null;
        this.caption = null;
        this.subCaption = null;
    }

    public File getCorrespondingFile() {
        return this.correspondingFile;
    }

    public abstract void detach();

    public abstract Node getFigure(double size);

    protected abstract String initCaption();

    public StringProperty captionProperty() {
        if (this.caption == null) {
            String initStr = this.initCaption();
            if (initStr != null) {
                this.caption = new SimpleStringProperty(initStr);
            } else {
                this.caption = new SimpleStringProperty();
            }
        }

        return this.caption;
    }

    public String getCaption() {
        return this.captionProperty().get();
    }

    protected abstract String initSubCaption();

    public StringProperty subCaptionProperty() {
        if (this.subCaption == null) {
            String initStr = this.initSubCaption();
            if (initStr != null) {
                this.subCaption = new SimpleStringProperty(initStr);
            } else {
                this.subCaption = new SimpleStringProperty();
            }
        }

        return this.subCaption;
    }

    public String getSubCaption() {
        return this.subCaptionProperty().get();
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
