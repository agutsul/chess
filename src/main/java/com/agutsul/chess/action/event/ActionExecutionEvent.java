package com.agutsul.chess.action.event;

import com.agutsul.chess.action.Action;

public class ActionExecutionEvent
        extends AbstractActionEvent {

    public ActionExecutionEvent(Action<?> action) {
        super(action);
    }
}