/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input.namelist;

import java.util.ArrayList;
import java.util.List;

public class QEValueBuffer implements QEValue, QEValueListener {

    private String name;

    private QEValue qeValue;

    private QENamelist namelist;

    private List<QEValueListener> listeners;

    public QEValueBuffer(QEValue qeValue, QENamelist namelist) {
        if (qeValue == null) {
            throw new IllegalArgumentException("qeValue is null.");
        }

        if (namelist == null) {
            throw new IllegalArgumentException("namelist is null.");
        }

        this.name = qeValue.getName();
        this.qeValue = qeValue;
        this.namelist = namelist;
        this.listeners = null;
    }

    public void addListener(QEValueListener listener) {
        if (listener == null) {
            return;
        }

        if (this.listeners == null) {
            this.listeners = new ArrayList<QEValueListener>();
        }

        this.listeners.add(listener);
    }

    public void runAllListeners() {
        if (this.listeners != null) {
            for (QEValueListener listener : listeners) {
                if (listener != null) {
                    listener.onValueChanged(this.qeValue);
                }
            }
        }
    }

    public void delete() {
        this.namelist.removeListener(this.name, this);
    }

    public boolean hasValue() {
        return this.qeValue != null;
    }

    public QEValue getValue() {
        return this.qeValue;
    }

    public void removeValue() {
        if (this.qeValue == null) {
            return;
        }

        this.qeValue = null;
        this.namelist.removeValue(QEValueBase.getInstance(this.name));

        if (this.listeners != null) {
            for (QEValueListener listener : listeners) {
                if (listener != null) {
                    listener.onValueChanged(null);
                }
            }
        }
    }

    public void setValue(int i) {
        this.setValue(QEValueBase.getInstance(this.name, i));
    }

    public void setValue(double r) {
        this.setValue(QEValueBase.getInstance(name, r));
    }

    public void setValue(boolean l) {
        this.setValue(QEValueBase.getInstance(name, l));
    }

    public void setValue(String c) {
        this.setValue(QEValueBase.getInstance(name, c));
    }

    public void setValue(QEValue qeValue) {
        if (qeValue == null) {
            return;
        }

        if (this.qeValue == qeValue) {
            return;
        }

        if (!this.name.equals(qeValue.getName())) {
            return;
        }

        this.qeValue = qeValue;
        this.namelist.setValue(this.qeValue);

        if (this.listeners != null) {
            for (QEValueListener listener : listeners) {
                if (listener != null) {
                    listener.onValueChanged(this.qeValue);
                }
            }
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getIntegerValue() {
        if (this.qeValue == null) {
            return 0;
        }

        return this.qeValue.getIntegerValue();
    }

    @Override
    public double getRealValue() {
        if (this.qeValue == null) {
            return 0.0;
        }

        return this.qeValue.getRealValue();
    }

    @Override
    public boolean getLogicalValue() {
        if (this.qeValue == null) {
            return false;
        }

        return this.qeValue.getLogicalValue();
    }

    @Override
    public String getCharacterValue() {
        if (this.qeValue == null) {
            return "";
        }

        return this.qeValue.getCharacterValue();
    }

    @Override
    public void onValueChanged(QEValue qeValue) {
        if (this.qeValue == qeValue) {
            return;
        }

        if (qeValue == null) {
            this.qeValue = null;

        } else if (this.name.equals(qeValue.getName())) {
            this.qeValue = qeValue;

        } else {
            return;
        }

        if (this.listeners != null) {
            for (QEValueListener listener : listeners) {
                if (listener != null) {
                    listener.onValueChanged(this.qeValue);
                }
            }
        }
    }

    @Override
    public String toString(int length) {
        if (this.qeValue != null) {
            return this.qeValue.toString(length);
        }

        return "!" + this.getName() + " is not defined.";
    }

    @Override
    public String toString() {
        if (this.qeValue != null) {
            return this.qeValue.toString();
        }

        return "!" + this.getName() + " is not defined.";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((namelist == null) ? 0 : namelist.hashCode());
        result = prime * result + ((qeValue == null) ? 0 : qeValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
