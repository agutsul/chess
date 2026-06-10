package com.agutsul.chess.game.phase;

import com.agutsul.chess.color.Color;

public interface GamePhase {

    enum Type {
        OPENING,
        MIDDLEGAME,
        ENDGAME
    }

    Type getType();
    Color getColor();

    // utilities

    static boolean isOpening(GamePhase gamePhase) {
        return isOpening(gamePhase.getType());
    }

    static boolean isOpening(GamePhase.Type type) {
        return GamePhase.Type.OPENING.equals(type);
    }

    static boolean isMiddleGame(GamePhase gamePhase) {
        return isMiddleGame(gamePhase.getType());
    }

    static boolean isMiddleGame(GamePhase.Type type) {
        return GamePhase.Type.MIDDLEGAME.equals(type);
    }

    static boolean isEndGame(GamePhase gamePhase) {
        return isEndGame(gamePhase.getType());
    }

    static boolean isEndGame(GamePhase.Type type) {
        return GamePhase.Type.ENDGAME.equals(type);
    }
}