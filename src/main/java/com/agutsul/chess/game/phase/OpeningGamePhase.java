package com.agutsul.chess.game.phase;

import com.agutsul.chess.color.Color;

public final class OpeningGamePhase
        extends AbstractGamePhase {

    public OpeningGamePhase(Color color) {
        super(Type.OPENING, color);
    }
}