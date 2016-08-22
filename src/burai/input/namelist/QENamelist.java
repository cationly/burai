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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import burai.com.str.SmartSplitter;

public class QENamelist {

    private String listName;

    private List<QEValue> qeValues;

    private Map<QEValue, List<QEValueListener>> listenerMap;

    private List<QEValue> protectedValues;

    private List<QEValue> deletingValues;

    private List<QEValue> bindingValues;

    public QENamelist(String listName) {
        if (listName == null || listName.isEmpty()) {
            throw new IllegalArgumentException("name of namelist is null or empty.");
        }

        this.listName = listName.trim().toUpperCase();
        this.qeValues = new ArrayList<QEValue>();
        this.listenerMap = null;
        this.protectedValues = null;
        this.deletingValues = null;
        this.bindingValues = null;
    }

    public void addListener(String name, QEValueListener listener) {
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        this.addListener(QEValueBase.getInstance(name), listener);
    }

    public void addListener(QEValue qeValue, QEValueListener listener) {
        if (qeValue == null) {
            return;
        }

        if (listener == null) {
            return;
        }

        if (this.listenerMap == null) {
            this.listenerMap = new HashMap<QEValue, List<QEValueListener>>();
        }

        List<QEValueListener> listenerList = this.listenerMap.get(qeValue);
        if (listenerList == null) {
            listenerList = new ArrayList<QEValueListener>();
            this.listenerMap.put(qeValue, listenerList);
        }

        listenerList.add(listener);
    }

    public void removeListener(String name, QEValueListener listener) {
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        this.removeListener(QEValueBase.getInstance(name), listener);
    }

    public void removeListener(QEValue qeValue, QEValueListener listener) {
        if (qeValue == null) {
            return;
        }

        if (listener == null) {
            return;
        }

        if (this.listenerMap == null) {
            return;
        }

        List<QEValueListener> listenerList = this.listenerMap.get(qeValue);
        if (listenerList == null) {
            return;
        }

        listenerList.remove(listener);
    }

    public void addProtectedValue(String name) {
        if (this.protectedValues == null) {
            this.protectedValues = new ArrayList<QEValue>();
        }

        this.protectedValues.add(QEValueBase.getInstance(name));
    }

    public void addDeletingValue(String name) {
        if (this.deletingValues == null) {
            this.deletingValues = new ArrayList<QEValue>();
        }

        this.deletingValues.add(QEValueBase.getInstance(name));
    }

    public void addBindingValue(String name) {
        if (this.bindingValues == null) {
            this.bindingValues = new ArrayList<QEValue>();
        }

        this.bindingValues.add(QEValueBase.getInstance(name));
    }

    public String getName() {
        return this.listName;
    }

    public int numValues() {
        return this.qeValues.size();
    }

    public QEValue getValue(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        int index = this.qeValues.indexOf(QEValueBase.getInstance(name));
        if (index > -1) {
            return this.qeValues.get(index);
        }

        return null;
    }

    public QEValueBuffer getValueBuffer(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        QEValueBuffer qeValueBuffer = null;

        QEValue qeValue = this.getValue(name);
        if (qeValue != null) {
            qeValueBuffer = new QEValueBuffer(qeValue, this);

        } else {
            qeValueBuffer = new QEValueBuffer(QEValueBase.getInstance(name), this);
            qeValueBuffer.removeValue();
        }

        this.addListener(name, qeValueBuffer);
        return qeValueBuffer;
    }

    public boolean removeValue(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        return this.removeValue(QEValueBase.getInstance(name));
    }

    public boolean removeValue(QEValue qeValue) {
        if (qeValue == null) {
            return false;
        }

        boolean status = this.qeValues.remove(qeValue);

        if (status && this.listenerMap != null) {
            List<QEValueListener> listenerList = this.listenerMap.get(qeValue);
            if (listenerList != null) {
                for (QEValueListener listener : listenerList) {
                    if (listener != null) {
                        listener.onValueChanged(null);
                    }
                }
            }
        }

        return status;
    }

    public boolean setValue(QEValue qeValue) {
        if (qeValue == null) {
            return false;
        }

        QEValue qeValue2 = qeValue;
        if (qeValue2 instanceof QEValueBuffer) {
            qeValue2 = ((QEValueBuffer) qeValue2).getValue();
            if (qeValue2 == null) {
                return false;
            }
        }

        if (this.qeValues.contains(qeValue2)) {
            this.qeValues.remove(qeValue2);
        }

        boolean status = this.qeValues.add(qeValue2);

        if (status && this.listenerMap != null) {
            List<QEValueListener> listenerList = this.listenerMap.get(qeValue2);
            if (listenerList != null) {
                for (QEValueListener listener : listenerList) {
                    if (listener != null) {
                        listener.onValueChanged(qeValue2);
                    }
                }
            }
        }

        return status;
    }

    public boolean setValue(String line) {
        if (line == null || line.isEmpty()) {
            return false;
        }

        String[] strList = SmartSplitter.split(line.trim(), new char[] { '=', ':', ' ', ',' });
        if (strList == null || strList.length < 2) {
            return false;
        }

        String name = strList[0] == null ? "" : strList[0].trim();
        if (name.isEmpty()) {
            return false;
        }

        String value = strList[1] == null ? "" : strList[1].trim();
        if (value.isEmpty()) {
            return false;
        }

        QEValue qeValue = QEValueBase.getInstance(name, value);
        return this.setValue(qeValue);
    }

    public QEValue[] listQEValues() {
        return this.qeValues.toArray(new QEValue[this.qeValues.size()]);
    }

    public void clearValueBuffers() {
        if (this.listenerMap == null) {
            return;
        }

        Set<QEValue> keyQeValues = this.listenerMap.keySet();
        if (keyQeValues == null) {
            return;
        }

        for (QEValue keyQeValue : keyQeValues) {
            List<QEValueListener> listeners = this.listenerMap.get(keyQeValue);
            if (listeners == null || listeners.isEmpty()) {
                continue;
            }

            QEValueListener[] listenerArray = new QEValueListener[listeners.size()];
            listenerArray = listeners.toArray(listenerArray);
            for (QEValueListener listener : listenerArray) {
                if (listener instanceof QEValueBuffer) {
                    listeners.remove(listener);
                }
            }
        }
    }

    private boolean isStartOfNamelist(String line) {
        if (line == null) {
            return false;
        }

        String line2 = line.trim().toUpperCase();
        if (line2 == null || line2.isEmpty()) {
            return false;
        }

        if (line2.equals("&" + this.listName)) {
            return true;
        }

        return false;
    }

    private boolean isEndOfNamelist(String line) {
        if (line == null) {
            return false;
        }

        String line2 = line.trim().toUpperCase();
        if (line2 == null || line2.isEmpty()) {
            return false;
        }

        if (line2.equals("/")) {
            return true;
        }

        if (line2.equals("&END")) {
            return true;
        }

        return false;
    }

    public boolean read(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("lines is null or empty.");
        }

        boolean inReading = false;
        boolean hasNamelist = false;

        for (String line : lines) {
            if (!inReading) {
                if (this.isStartOfNamelist(line)) {
                    inReading = true;
                    hasNamelist = true;
                }

            } else {
                if (this.isEndOfNamelist(line)) {
                    break;
                } else {
                    this.setValue(line);
                }
            }
        }

        return hasNamelist;
    }

    public void copyTo(QENamelist namelist) {
        this.copyTo(namelist, true);
    }

    public void copyTo(QENamelist namelist, boolean protect) {
        if (namelist == null) {
            throw new IllegalArgumentException("namelist is null.");
        }

        if (!this.getName().equals(namelist.getName())) {
            throw new IllegalArgumentException("namelist is incorrect.");
        }

        for (QEValue qeValue : this.qeValues) {
            if (qeValue == null) {
                continue;
            }
            if (protect && namelist.protectedValues != null && namelist.protectedValues.contains(qeValue)) {
                // NOP
            } else {
                namelist.setValue(qeValue);
            }
        }

        if (this.deletingValues != null) {
            for (QEValue qeValue : this.deletingValues) {
                if (qeValue == null) {
                    continue;
                }
                if (protect && namelist.protectedValues != null && namelist.protectedValues.contains(qeValue)) {
                    // NOP
                } else {
                    namelist.removeValue(qeValue);
                }
            }
        }

        if (this.bindingValues != null) {
            for (QEValue qeValue : this.bindingValues) {
                if (qeValue == null) {
                    continue;
                }
                if (!this.qeValues.contains(qeValue)) {
                    if (protect && namelist.protectedValues != null && namelist.protectedValues.contains(qeValue)) {
                        // NOP
                    } else {
                        namelist.removeValue(qeValue);
                    }
                }
            }
        }
    }

    public void clear() {
        //this.qeValues.clear();
        QEValue[] qeValueList = this.qeValues.toArray(new QEValue[this.qeValues.size()]);
        for (QEValue qeValue : qeValueList) {
            this.removeValue(qeValue);
        }
    }

    @Override
    public String toString() {
        String str = "&" + this.listName + System.lineSeparator();

        int maxLength = 0;
        for (QEValue qeValue : this.qeValues) {
            String name = null;
            if (qeValue != null) {
                name = qeValue.getName();
            }
            if (name != null && !name.startsWith("!")) {
                maxLength = Math.max(name.length(), maxLength);
            }
        }

        QEValue[] qeValueArray = this.qeValues.toArray(new QEValue[this.qeValues.size()]);
        Arrays.<QEValue> sort(qeValueArray, (qeValue1, qeValue2) -> {
            String name1 = qeValue1.getName();
            String name2 = qeValue2.getName();
            return name1.compareTo(name2);
        });

        for (QEValue qeValue : qeValueArray) {
            if (qeValue != null) {
                String name = qeValue.getName();
                if (name != null && !name.startsWith("!")) {
                    str = str + "    " + qeValue.toString(maxLength) + System.lineSeparator();
                }
            }
        }

        str = str + "/" + System.lineSeparator();
        return str;
    }

    @Override
    public int hashCode() {
        return this.listName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof QENamelist)) {
            return false;
        }

        QENamelist other = (QENamelist) obj;
        return this.listName.equals(other.listName);
    }
}
