package com.agutsul.chess.activity.action.event;

import com.agutsul.chess.color.Color;

public class ActionCancelledEvent
        extends AbstractProccessedActionEvent {

    private final Color color;

    public ActionCancelledEvent(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}