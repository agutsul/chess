package com.agutsul.chess.game.phase;

import com.agutsul.chess.color.Color;

public final class MiddleGamePhase
        extends AbstractGamePhase {

    public MiddleGamePhase(Color color) {
        super(Type.MIDDLEGAME, color);
    }
}