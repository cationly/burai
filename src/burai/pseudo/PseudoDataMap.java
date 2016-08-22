/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.pseudo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PseudoDataMap implements Map<String, PseudoData> {

    private Map<String, PseudoData> pseudos;

    public PseudoDataMap() {
        this.pseudos = null;
    }

    private Map<String, PseudoData> getMap() {
        if (this.pseudos == null) {
            this.pseudos = new HashMap<String, PseudoData>();
        }

        return this.pseudos;
    }

    @Override
    public int size() {
        return this.getMap().size();
    }

    @Override
    public boolean isEmpty() {
        return this.getMap().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.getMap().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.getMap().containsValue(value);
    }

    @Override
    public PseudoData get(Object key) {
        return this.getMap().get(key);
    }

    @Override
    public PseudoData put(String key, PseudoData value) {
        return this.getMap().put(key, value);
    }

    @Override
    public PseudoData remove(Object key) {
        return this.getMap().remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends PseudoData> m) {
        this.getMap().putAll(m);
    }

    @Override
    public void clear() {
        this.getMap().clear();
    }

    @Override
    public Set<String> keySet() {
        return this.getMap().keySet();
    }

    @Override
    public Collection<PseudoData> values() {
        return this.getMap().values();
    }

    @Override
    public Set<java.util.Map.Entry<String, PseudoData>> entrySet() {
        return this.getMap().entrySet();
    }
}
