/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.explorer.body;

import java.io.File;
import java.util.List;

import burai.matapi.MaterialsAPIQueue;
import burai.project.Project;

public class SearchedQueue extends FileQueue {

    private MaterialsAPIQueue matApiQueue;

    private List<Project> shownProjects;

    protected SearchedQueue(MaterialsAPIQueue matApiQueue, List<Project> shownProjects) {
        if (matApiQueue == null) {
            throw new IllegalArgumentException("matApiQueue is null.");
        }

        this.matApiQueue = matApiQueue;
        this.shownProjects = shownProjects;
    }

    @Override
    public FileElement pollFileElement() {
        File file = this.matApiQueue.pollCIFFile();

        String path = null;
        if (file != null) {
            path = file.getPath();
        }

        return this.createFileElement(path);
    }

    @Override
    public FileElement peekFileElement() {
        File file = this.matApiQueue.peekCIFFile();

        String path = null;
        if (file != null) {
            path = file.getPath();
        }

        return this.createFileElement(path);
    }

    private FileElement createFileElement(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        FileElement fileElement = new FileElement(path);

        if (this.shownProjects != null && (!this.shownProjects.isEmpty())) {
            for (Project project : this.shownProjects) {
                if (project != null && project.isRelatedFile(path)) {
                    fileElement.setProject(project);
                    break;
                }
            }
        }

        return fileElement;
    }

    @Override
    public void addFileElement(FileElement fileElement) {
        throw new RuntimeException("cannot call SearchedQueue#addFileElement.");
    }

    @Override
    public boolean hasFileElements() {
        return this.matApiQueue.hasCIFFiles();
    }

    @Override
    public void stopFileElements() {
        this.matApiQueue.stopCIFFiles();
    }
}
