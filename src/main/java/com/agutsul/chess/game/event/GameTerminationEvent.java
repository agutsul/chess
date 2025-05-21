package com.agutsul.chess.game.event;

import com.agutsul.chess.game.Termination;

public interface GameTerminationEvent
        extends Termination {

    enum Type {
        EXIT,
        DRAW,
        DEFEAT,
        WIN,
        TIMEOUT
    }

    Type getType();
}