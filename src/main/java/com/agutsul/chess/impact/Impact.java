package com.agutsul.chess.impact;

import com.agutsul.chess.position.Positionable;

public interface Impact<SOURCE>
        extends Positionable {

    enum Type {
        CONTROL,
        PROTECT,
        MONITOR,
        PIN,
        CHECK
    }

    SOURCE getSource();
    Type getType();
    String getCode();
}