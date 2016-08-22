/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.run;

import java.util.LinkedList;
import java.util.Queue;

public class RunningQueue implements RunningManagerListener {

    private boolean alive;

    private RunningManager manager;

    private RunningManagerListener listener;

    private Queue<RunningNode> bufferingNodes;

    protected RunningQueue(RunningManager manager, Queue<RunningNode> bufferingNodes) {
        if (manager == null) {
            throw new IllegalArgumentException("manager is null.");
        }

        this.alive = true;

        this.manager = manager;

        this.listener = null;

        if (bufferingNodes != null) {
            this.bufferingNodes = bufferingNodes;
        } else {
            this.bufferingNodes = new LinkedList<RunningNode>();
        }
    }

    public synchronized void stopQueue() {
        this.alive = false;
        this.manager.removeListener(this);
        this.notifyAll();
    }

    public synchronized RunningNode pollNode() {
        RunningNode node = null;

        while (this.alive) {
            node = this.bufferingNodes.poll();
            if (node != null) {
                break;
            }

            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return node;
    }

    public synchronized RunningNode peekNode() {
        RunningNode node = null;

        while (this.alive) {
            node = this.bufferingNodes.peek();
            if (node != null) {
                break;
            }

            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return node;
    }

    public synchronized boolean hasNodes() {
        RunningNode node = this.bufferingNodes.peek();
        return node != null;
    }

    public synchronized void setListener(RunningManagerListener listener) {
        this.listener = listener;
    }

    @Override
    public synchronized void onNodeAdded(RunningNode node) {
        if (node != null) {
            boolean status = this.bufferingNodes.add(node);
            if (status) {
                this.notifyAll();
            }
        }

        if (this.listener != null) {
            this.listener.onNodeAdded(node);
        }
    }

    @Override
    public synchronized void onNodeRemoved(RunningNode node) {
        if (node != null) {
            this.bufferingNodes.remove(node);
        }

        if (this.listener != null) {
            this.listener.onNodeRemoved(node);
        }
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
