package com.agutsul.chess.activity.cache;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.activity.Activity;

public class ActivityCacheImpl<TYPE extends Enum<TYPE> & Activity.Type,
                               ACTIVITY extends Activity<?>>
        implements ActivityCache<TYPE,ACTIVITY> {

    private final ActivityMap<TYPE,ACTIVITY> cache;

    public ActivityCacheImpl() {
        this.cache = new ActivityMultiMap<>();
    }

    @Override
    public void clear() {
        this.cache.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void putAll(Collection<ACTIVITY> activities) {
        Stream.ofNullable(activities)
            .flatMap(Collection::stream)
            .forEach(activity -> put((TYPE) activity.getType(), activity));
    }

    @Override
    public void put(TYPE type, Collection<ACTIVITY> activities) {
        this.cache.put(type, activities);
    }

    @Override
    public void put(TYPE type, ACTIVITY activity) {
        this.cache.put(type, activity);
    }

    @Override
    public Collection<ACTIVITY> getAll() {
        return this.cache.values().stream()
                .flatMap(Collection::stream)
                .toList();
    }

    @Override
    public Collection<ACTIVITY> get(TYPE type) {
        return this.cache.get(type);
    }

    @Override
    public boolean isEmpty() {
        return this.cache.isEmpty();
    }
}