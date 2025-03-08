package com.agutsul.chess.board.event;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Event;

public final class SetCastlingableSideEvent
        implements Event {

    private final Color color;
    private final Castlingable.Side side;
    private final boolean enabled;

    public SetCastlingableSideEvent(Color color, Castlingable.Side side, boolean enabled) {
        this.color = color;
        this.side = side;
        this.enabled = enabled;
    }

    public Color getColor() {
        return color;
    }

    public Castlingable.Side getSide() {
        return side;
    }

    public boolean isEnabled() {
        return enabled;
    }
}