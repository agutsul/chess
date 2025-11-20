package com.agutsul.chess.activity.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import com.agutsul.chess.activity.Activity;

final class ActivityMultiMap<KEY extends Enum<KEY> & Activity.Type,
                             VALUE extends Activity<KEY,?>>
        implements ActivityMap<KEY,VALUE> {

    private final MultiValuedMap<KEY,VALUE> map;

    ActivityMultiMap() {
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
        return new ArrayList<>(this.map.get((KEY) key));
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
        Stream.ofNullable(map)
            .map(Map::entrySet)
            .flatMap(Collection::stream)
            .filter(Objects::nonNull)
            .forEach(entry -> this.map.putAll(entry.getKey(), entry.getValue()));
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Set<KEY> keySet() {
        return new HashSet<>(this.map.keySet());
    }

    @Override
    public Collection<Collection<VALUE>> values() {
        return new ArrayList<>(this.map.asMap().values());
    }

    @Override
    public Set<Entry<KEY,Collection<VALUE>>> entrySet() {
        return new HashSet<>(this.map.asMap().entrySet());
    }
}