package com.agutsul.chess.activity.cache;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.activity.Activity;

public class ActivityCacheImpl<TYPE extends Enum<TYPE> & Activity.Type,
                               ACTIVITY extends Activity<TYPE,?>>
        implements ActivityCache<TYPE,ACTIVITY> {

    private final ActivityMap<TYPE,ACTIVITY> cache;

    public ActivityCacheImpl() {
        this.cache = new ActivityMultiMap<>();
    }

    @Override
    public void putAll(Collection<ACTIVITY> activities) {
        Stream.ofNullable(activities)
            .flatMap(Collection::stream)
            .filter(Objects::nonNull)
            .forEach(activity -> put(activity.getType(), activity));
    }

    @Override
    public final void put(TYPE type, Collection<ACTIVITY> activities) {
        this.cache.put(type, activities);
    }

    @Override
    public final void put(TYPE type, ACTIVITY activity) {
        this.cache.put(type, activity);
    }

    @Override
    public final Collection<ACTIVITY> getAll() {
        return Stream.of(this.cache.values())
                .flatMap(Collection::stream)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .distinct()
                .collect(toList());
    }

    @Override
    public final Collection<ACTIVITY> get(TYPE type) {
        return this.cache.getOrDefault(type, emptyList());
    }

    @Override
    public final boolean isEmpty() {
        return this.cache.isEmpty();
    }

    @Override
    public final void clear() {
        this.cache.clear();
    }
}