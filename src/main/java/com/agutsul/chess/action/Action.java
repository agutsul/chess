package com.agutsul.chess.action;

import com.agutsul.chess.Executable;
import com.agutsul.chess.position.Positionable;

public interface Action<SOURCE>
        extends Executable, Positionable {

    enum Type {
        CAPTURE,
        MOVE,
        CASTLING,
        PROMOTE,
        EN_PASSANT
    }

    SOURCE getSource();
    Type getType();
    String getCode();

}