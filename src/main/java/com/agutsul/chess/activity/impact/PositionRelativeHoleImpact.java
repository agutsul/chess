package com.agutsul.chess.activity.impact;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.position.Position;

public final class PositionRelativeHoleImpact
        extends AbstractPositionHoleImpact{

    public PositionRelativeHoleImpact(Color color, Position position) {
        super(Mode.RELATIVE, color, position);
    }
}