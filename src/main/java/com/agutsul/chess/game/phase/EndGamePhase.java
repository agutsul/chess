package com.agutsul.chess.game.phase;

import com.agutsul.chess.color.Color;

public final class EndGamePhase
        extends AbstractGamePhase {

    public EndGamePhase(Color color) {
        super(Type.ENDGAME, color);
    }
}