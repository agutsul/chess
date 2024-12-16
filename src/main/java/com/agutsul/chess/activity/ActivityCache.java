package com.agutsul.chess.activity;

import java.util.Collection;

public interface ActivityCache<TYPE extends Enum<TYPE> & Activity.Type,
                               ACTIVITY extends Activity<?>> {

    boolean isEmpty();
    void clear();

    void put(TYPE type, ACTIVITY activity);
    void put(TYPE type, Collection<ACTIVITY> activities);
    void putAll(Collection<ACTIVITY> activities);

    Collection<ACTIVITY> get(TYPE type);
    Collection<ACTIVITY> getAll();
}