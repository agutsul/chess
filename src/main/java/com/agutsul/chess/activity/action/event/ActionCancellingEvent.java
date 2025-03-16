package com.agutsul.chess.activity.action.event;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.color.Color;

public class ActionCancellingEvent
        extends AbstractProcessingActionEvent {

    private final Color color;

    public ActionCancellingEvent(Color color, Action<?> action) {
        super(action);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}