package com.agutsul.chess.activity.cache;

import java.util.Collection;
import java.util.Map;

import com.agutsul.chess.activity.Activity;

public interface ActivityMap<KEY extends Enum<KEY> & Activity.Type,
                             VALUE extends Activity<KEY,?>>
        extends Map<KEY,Collection<VALUE>> {

    void put(KEY key, VALUE value);
}