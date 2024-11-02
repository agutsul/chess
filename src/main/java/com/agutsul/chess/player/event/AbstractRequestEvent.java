package com.agutsul.chess.player.event;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Event;

public abstract class AbstractRequestEvent
        implements Event {

    private final Color color;

    AbstractRequestEvent(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}