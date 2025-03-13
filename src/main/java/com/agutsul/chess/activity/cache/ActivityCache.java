package com.agutsul.chess.activity.cache;

import java.util.Collection;

import com.agutsul.chess.activity.Activity;

public interface ActivityCache<TYPE extends Enum<TYPE> & Activity.Type,
                               ACTIVITY extends Activity<TYPE,?>> {

    boolean isEmpty();
    void clear();

    void put(TYPE type, ACTIVITY activity);
    void put(TYPE type, Collection<ACTIVITY> activities);
    void putAll(Collection<ACTIVITY> activities);

    Collection<ACTIVITY> get(TYPE type);
    Collection<ACTIVITY> getAll();
}