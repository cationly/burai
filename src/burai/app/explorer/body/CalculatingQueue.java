/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.explorer.body;

import javafx.application.Platform;
import burai.project.Project;
import burai.run.RunningManager;
import burai.run.RunningManagerListener;
import burai.run.RunningNode;
import burai.run.RunningQueue;

public class CalculatingQueue extends FileQueue implements RunningManagerListener {

    private static final long PRE_REMOVING_TIME = 2500L;

    private RunningQueue runningQueue;

    protected CalculatingQueue() {
        this.runningQueue = RunningManager.getInstance().getQueue();

        if (this.runningQueue != null) {
            this.runningQueue.setListener(this);
        }
    }

    @Override
    public FileElement pollFileElement() {
        RunningNode runningNode = null;
        if (this.runningQueue != null) {
            runningNode = this.runningQueue.pollNode();
        }

        return this.createFileElement(runningNode);
    }

    @Override
    public FileElement peekFileElement() {
        RunningNode runningNode = null;
        if (this.runningQueue != null) {
            runningNode = this.runningQueue.peekNode();
        }

        return this.createFileElement(runningNode);
    }

    private FileElement createFileElement(RunningNode runningNode) {
        if (runningNode == null) {
            return null;
        }

        Project project = runningNode.getProject();
        if (project == null) {
            return null;
        }

        String path = project.getRelatedFilePath();
        if (path == null || path.isEmpty()) {
            return null;
        }

        FileElement fileElement = new FileElement(path);
        fileElement.setProject(project);
        fileElement.setRunningNode(runningNode);

        return fileElement;
    }

    @Override
    public void addFileElement(FileElement fileElement) {
        throw new RuntimeException("cannot call CalculatingQueue#addFileElement.");
    }

    @Override
    public boolean hasFileElements() {
        if (this.runningQueue != null) {
            return this.runningQueue.hasNodes();
        }

        return false;
    }

    @Override
    public void stopFileElements() {
        if (this.runningQueue != null) {
            this.runningQueue.stopQueue();
        }
    }

    @Override
    public void onNodeAdded(RunningNode runningNode) {
        // NOP
    }

    @Override
    public void onNodeRemoved(RunningNode runningNode) {
        FileElement fileElement = this.createFileElement(runningNode);
        if (fileElement == null) {
            return;
        }

        Thread thread = new Thread(() -> {
            synchronized (this) {
                try {
                    this.wait(PRE_REMOVING_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            this.removeFileElementFX(fileElement);
        });

        thread.start();
    }

    private void removeFileElementFX(FileElement fileElement) {
        if (fileElement == null) {
            return;
        }

        Platform.runLater(() -> {
            this.actionOnFileElementDeleted(fileElement);
        });
    }
}
