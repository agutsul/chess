package com.agutsul.chess.activity;

import java.util.Collection;
import java.util.Map;

public interface ActivityMap<KEY extends Enum<KEY> & Activity.Type,
                             VALUE extends Activity<?>>
        extends Map<KEY,Collection<VALUE>> {

    void put(KEY key, VALUE value);
}