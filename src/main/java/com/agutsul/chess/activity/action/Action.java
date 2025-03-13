package com.agutsul.chess.activity.action;

import com.agutsul.chess.Executable;
import com.agutsul.chess.Positionable;
import com.agutsul.chess.activity.Activity;

public interface Action<SOURCE>
        extends Executable, Positionable, Activity<Action.Type,SOURCE> {

    enum Type implements Activity.Type {
        CAPTURE,
        MOVE,
        CASTLING,
        PROMOTE,
        EN_PASSANT
    }

    String getCode();
}