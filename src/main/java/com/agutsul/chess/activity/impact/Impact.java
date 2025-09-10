package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Positionable;
import com.agutsul.chess.activity.Activity;

public interface Impact<SOURCE>
        extends Positionable, Activity<Impact.Type,SOURCE> {

    enum Type implements Activity.Type {
        CONTROL,
        PROTECT,
        MONITOR,
        STAGNANT,
        PIN,
        CHECK,
        ATTACK,
        FORK
    }
}