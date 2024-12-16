package com.agutsul.chess.activity.action.event;

import com.agutsul.chess.activity.action.Action;

public class ActionExecutionEvent
        extends AbstractProcessingActionEvent {

    public ActionExecutionEvent(Action<?> action) {
        super(action);
    }
}