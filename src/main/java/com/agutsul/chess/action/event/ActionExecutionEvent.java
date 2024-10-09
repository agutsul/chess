package com.agutsul.chess.action.event;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.event.Event;

public class ActionExecutionEvent
        implements Event {

    private final Action<?> action;

    public ActionExecutionEvent(Action<?> action) {
        this.action = action;
    }

    public Action<?> getAction() {
        return action;
    }
}