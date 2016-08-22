/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.run;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import burai.com.life.Life;
import burai.project.Project;

public class RunningManager implements Runnable {

    private static RunningManager instance = null;

    public static RunningManager getInstance() {
        if (instance == null) {
            instance = new RunningManager();
        }

        return instance;
    }

    private boolean alive;

    private RunningNode currentNode;

    private Queue<RunningNode> nodes;

    private List<RunningManagerListener> listeners;

    private RunningManager() {
        this.alive = true;

        this.currentNode = null;
        this.nodes = new LinkedList<RunningNode>();

        this.listeners = null;

        Thread thread = new Thread(this);
        thread.start();

        Life.getInstance().addOnDead(() -> this.stop());
    }

    private synchronized boolean isAlive() {
        return this.alive;
    }

    public synchronized void stop() {
        this.alive = false;

        if (this.currentNode != null) {
            this.currentNode.stop();
        }

        this.notifyAll();
    }

    public synchronized boolean addNode(RunningNode node) {
        if (node != null) {
            boolean status = this.nodes.offer(node);

            if (status) {
                node.setStatus(RunningStatus.QUEUED);

                this.notifyAll();

                if (this.listeners != null) {
                    for (RunningManagerListener listener : this.listeners) {
                        if (listener != null) {
                            listener.onNodeAdded(node);
                        }
                    }
                }
            }

            return status;
        }

        return false;
    }

    public synchronized boolean removeNode(RunningNode node) {
        if (node != null) {
            if (node == this.currentNode) {
                this.currentNode.stop();
                return true;
            }

            boolean status = this.nodes.remove(node);

            if (status) {
                node.setStatus(RunningStatus.IDLE);

                if (this.listeners != null) {
                    for (RunningManagerListener listener : this.listeners) {
                        if (listener != null) {
                            listener.onNodeRemoved(node);
                        }
                    }
                }
            }

            return status;
        }

        return false;
    }

    public synchronized void addListener(RunningManagerListener listener) {
        if (listener == null) {
            return;
        }

        if (this.listeners == null) {
            this.listeners = new ArrayList<RunningManagerListener>();
        }

        this.listeners.add(listener);
    }

    public synchronized void removeListener(RunningManagerListener listener) {
        if (listener == null) {
            return;
        }

        if (this.listeners == null || this.listeners.isEmpty()) {
            return;
        }

        this.listeners.remove(listener);
    }

    @Override
    public void run() {
        while (this.isAlive()) {

            synchronized (this) {
                while (this.alive) {
                    this.currentNode = this.nodes.poll();
                    if (this.currentNode != null) {
                        break;
                    }

                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (this.isAlive() && this.currentNode != null) {
                this.currentNode.setStatus(RunningStatus.RUNNING);

                this.currentNode.run();

                this.currentNode.setStatus(RunningStatus.DONE);

                synchronized (this) {
                    if (this.listeners != null) {
                        for (RunningManagerListener listener : this.listeners) {
                            if (listener != null) {
                                listener.onNodeRemoved(this.currentNode);
                            }
                        }
                    }

                    this.currentNode = null;
                }
            }
        }
    }

    public synchronized RunningQueue getQueue() {
        Queue<RunningNode> nodes2 = new LinkedList<RunningNode>();
        if (this.currentNode != null) {
            nodes2.add(this.currentNode);
        }
        if (!this.nodes.isEmpty()) {
            nodes2.addAll(this.nodes);
        }

        RunningQueue queue = new RunningQueue(this, nodes2);

        if (this.listeners == null) {
            this.listeners = new ArrayList<RunningManagerListener>();
        }

        this.listeners.add(queue);

        return queue;
    }

    public synchronized RunningNode getNode(String path) {
        Project currentProject = this.currentNode == null ? null : this.currentNode.getProject();
        if (currentProject != null && currentProject.isRelatedFile(path)) {
            return this.currentNode;
        }

        for (RunningNode node : this.nodes) {
            Project project = node == null ? null : node.getProject();
            if (project != null && project.isRelatedFile(path)) {
                return node;
            }
        }

        return null;
    }

    public synchronized RunningNode getNode(Project project) {
        Project currentProject = this.currentNode == null ? null : this.currentNode.getProject();
        if (currentProject != null && currentProject.isSameAs(project)) {
            return this.currentNode;
        }

        for (RunningNode node : this.nodes) {
            Project project2 = node == null ? null : node.getProject();
            if (project2 != null && project2.isSameAs(project)) {
                return node;
            }
        }

        return null;
    }

    public synchronized boolean isEmpty() {
        return this.currentNode == null && this.nodes.isEmpty();
    }
}
