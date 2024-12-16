package com.agutsul.chess.action;

import com.agutsul.chess.Executable;
import com.agutsul.chess.activity.Activity;
import com.agutsul.chess.position.Positionable;

public interface Action<SOURCE>
        extends Executable, Positionable, Activity<SOURCE> {

    enum Type implements Activity.Type {
        CAPTURE,
        MOVE,
        CASTLING,
        PROMOTE,
        EN_PASSANT
    }

    String getCode();
}