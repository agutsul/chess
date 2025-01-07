package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Positionable;
import com.agutsul.chess.activity.Activity;

public interface Impact<SOURCE>
        extends Positionable, Activity<SOURCE> {

    enum Type implements Activity.Type {
        CONTROL,
        PROTECT,
        MONITOR,
        BLOCK,
        PIN,
        CHECK
    }
}