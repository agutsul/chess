package com.agutsul.chess.activity;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

public class ActivityMultiMap<KEY extends Enum<KEY> & Activity.Type,
                              VALUE extends Activity<?>>
        implements ActivityMap<KEY,VALUE> {

    private final MultiValuedMap<KEY,VALUE> map;

    public ActivityMultiMap() {
        this.map = new ArrayListValuedHashMap<>();
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<VALUE> get(Object key) {
        return this.map.get((KEY) key);
    }

    @Override
    public Collection<VALUE> remove(Object key) {
        return this.map.remove(key);
    }

    @Override
    public void put(KEY key, VALUE value) {
        this.map.put(key, value);
    }

    @Override
    public Collection<VALUE> put(KEY key, Collection<VALUE> value) {
        this.map.putAll(key, value);
        return value;
    }

    @Override
    public void putAll(Map<? extends KEY,? extends Collection<VALUE>> map) {
        if (MapUtils.isEmpty(map)) {
            return;
        }

        for (var entry : map.entrySet()) {
            var values = (Collection<VALUE>) entry.getValue();
            this.map.putAll(entry.getKey(), values);
        }
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Set<KEY> keySet() {
        return this.map.keySet();
    }

    @Override
    public Collection<Collection<VALUE>> values() {
        return this.map.asMap().values();
    }

    @Override
    public Set<Entry<KEY, Collection<VALUE>>> entrySet() {
        return this.map.asMap().entrySet();
    }
}