package com.agutsul.chess.activity.impact;

import com.agutsul.chess.activity.AbstractSourceActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.position.Position;

abstract class AbstractPositionHoleImpact
        extends AbstractSourceActivity<Impact.Type,Position>
        implements PositionHoleImpact<Position> {

    private final Color color;
    private final Mode mode;

    AbstractPositionHoleImpact(Mode mode, Color color, Position position) {
        super(Impact.Type.HOLE, position);
        this.mode = mode;
        this.color = color;
    }

    @Override
    public final Color getColor() {
        return this.color;
    }

    @Override
    public final Mode getMode() {
        return this.mode;
    }

    @Override
    public final Position getPosition() {
        return getSource();
    }

    @Override
    public final String toString() {
        return String.format("%s:%s:[%s:%s]",
                getType(), getMode(), getColor(), getPosition()
        );
    }
}