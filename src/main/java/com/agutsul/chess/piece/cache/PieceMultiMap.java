package com.agutsul.chess.piece.cache;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import com.agutsul.chess.piece.Piece;

final class PieceMultiMap implements PieceMap {

    private final MultiValuedMap<String,Collection<Piece<?>>> map;

    PieceMultiMap() {
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
    public Collection<Piece<?>> get(Object key) {
        return Stream.of(this.map.get(String.valueOf(key)))
                .flatMap(Collection::stream)
                .flatMap(Collection::stream)
                .toList();
    }

    @Override
    public Collection<Piece<?>> put(String key, Collection<Piece<?>> value) {
        this.map.put(key, value);
        return value;
    }

    @Override
    public Collection<Piece<?>> remove(Object key) {
        return Stream.of(this.map.remove(key))
                .flatMap(Collection::stream)
                .flatMap(Collection::stream)
                .toList();
    }

    @Override
    public void putAll(Map<? extends String, ? extends Collection<Piece<?>>> map) {
        this.map.putAll(map);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.map.keySet();
    }

    @Override
    public Collection<Collection<Piece<?>>> values() {
        return this.map.values();
    }

    @Override
    public Set<Entry<String, Collection<Piece<?>>>> entrySet() {
        return this.map.entries().stream().collect(toSet());
    }
}