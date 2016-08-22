/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.model.property;

import java.util.ArrayList;
import java.util.List;

public class ModelProperty {

    private Object property;

    private List<ModelPropertyListener> listeners;

    public ModelProperty() {
        this.property = null;
        this.listeners = null;
    }

    public Object getProperty() {
        return this.property;
    }

    public void setProperty(Object property) {
        this.property = property;

        if (this.listeners != null) {
            for (ModelPropertyListener listener : this.listeners) {
                if (listener != null) {
                    listener.onPropertyChanged(this.property);
                }
            }
        }
    }

    public void addListener(ModelPropertyListener listener) {
        if (listener == null) {
            return;
        }

        if (this.listeners == null) {
            this.listeners = new ArrayList<ModelPropertyListener>();
        }

        this.listeners.add(listener);
    }

    public void removeListener(ModelPropertyListener listener) {
        if (listener == null) {
            return;
        }

        if (this.listeners == null) {
            return;
        }

        this.listeners.remove(listener);
    }
}
