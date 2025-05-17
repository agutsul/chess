package com.agutsul.chess.game.event;

public interface GameTerminationEvent {
    enum Type {
        EXIT,
        DRAW,
        DEFEAT,
        WIN
    }

    Type getType();
}