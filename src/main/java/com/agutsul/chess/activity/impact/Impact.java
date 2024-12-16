package com.agutsul.chess.activity.impact;

import com.agutsul.chess.activity.Activity;
import com.agutsul.chess.position.Positionable;

public interface Impact<SOURCE>
        extends Positionable, Activity<SOURCE> {

    enum Type implements Activity.Type {
        CONTROL,
        PROTECT,
        MONITOR,
        PIN,
        CHECK
    }
}