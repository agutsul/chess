package com.agutsul.chess.activity.impact;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.position.Position;

public final class PositionAbsoluteHoleImpact
        extends AbstractPositionHoleImpact {

    public PositionAbsoluteHoleImpact(Color color, Position position) {
        super(Mode.ABSOLUTE, color, position);
    }
}