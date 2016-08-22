/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer.operation;

import java.util.ArrayList;
import java.util.List;

import javafx.event.Event;
import javafx.event.EventHandler;

public abstract class ViewerEventHandler<T extends Event> implements EventHandler<T> {

    protected ViewerEventManager manager;

    protected List<ViewerEventKernel<T>> kernels;

    public ViewerEventHandler() {
        this.manager = null;
        this.kernels = new ArrayList<ViewerEventKernel<T>>();
    }

    public ViewerEventHandler(ViewerEventManager manager) {
        this();

        if (manager == null) {
            throw new IllegalArgumentException("manager is null.");
        }

        this.manager = manager;
    }

    @Override
    public void handle(T event) {
        if (event == null) {
            return;
        }

        for (ViewerEventKernel<T> kernel : this.kernels) {
            if (kernel.isToPerform(this.manager)) {
                kernel.perform(this.manager, event);
                break;
            }
        }
    }

    protected void addKernel(ViewerEventKernel<T> kernel) {
        if (kernel == null) {
            return;
        }

        this.kernels.add(kernel);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        @SuppressWarnings("unchecked")
        ViewerEventHandler<T> handler = (ViewerEventHandler<T>) obj;
        return this.manager == handler.manager;
    }
}
