package com.agutsul.chess.action.event;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.event.Event;

public abstract class AbstractActionEvent
        implements Event {

    private final Action<?> action;

    public AbstractActionEvent(Action<?> action) {
        this.action = action;
    }

    public Action<?> getAction() {
        return action;
    }
}