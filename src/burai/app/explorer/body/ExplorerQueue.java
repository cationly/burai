/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.explorer.body;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import burai.project.Project;

public class ExplorerQueue extends FileQueue {

    private boolean alive;

    private LinkedList<FileElement> fileElements;

    protected ExplorerQueue(QEFXExplorerBody explorerBody, List<Project> shownProjects) throws IOException {
        if (explorerBody == null) {
            throw new IllegalArgumentException("explorerBody is null.");
        }

        this.alive = true;

        this.fileElements = new LinkedList<FileElement>();

        this.setupFileElements(explorerBody, shownProjects);
    }

    private void setupFileElements(QEFXExplorerBody explorerBody, List<Project> shownProjects) throws IOException {
        FileLister fileLister = new FileLister(explorerBody);
        fileLister.list(shownProjects);
        String[] fileNames = fileLister.getFileNames();
        Project[] fileProjects = fileLister.getFileProjects();

        boolean hasFileProjects = false;
        if (fileProjects != null && fileProjects.length >= fileNames.length) {
            hasFileProjects = true;
        }

        if (fileNames != null && fileNames.length > 0) {
            for (int i = 0; i < fileNames.length; i++) {
                String fileName = fileNames[i];
                Project fileProject = hasFileProjects ? fileProjects[i] : null;
                FileElement fileElement = new FileElement(fileName);
                fileElement.setProject(fileProject);
                this.fileElements.add(fileElement);
            }
        }
    }

    @Override
    public synchronized FileElement pollFileElement() {
        while (this.alive && this.fileElements.isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (this.alive) {
            return this.fileElements.poll();
        } else {
            return null;
        }
    }

    @Override
    public synchronized FileElement peekFileElement() {
        while (this.alive && this.fileElements.isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (this.alive) {
            return this.fileElements.peek();
        } else {
            return null;
        }
    }

    @Override
    public synchronized void addFileElement(FileElement fileElement) {
        if (fileElement == null) {
            return;
        }

        this.fileElements.offer(fileElement);

        this.notifyAll();
    }

    @Override
    public synchronized boolean hasFileElements() {
        return !this.fileElements.isEmpty();
    }

    @Override
    public synchronized void stopFileElements() {
        this.alive = false;

        this.notifyAll();
    }
}
