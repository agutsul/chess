package com.agutsul.chess.action.event;

import com.agutsul.chess.action.Action;

public class ActionCancellingEvent
        extends AbstractProcessingActionEvent {

    public ActionCancellingEvent(Action<?> action) {
        super(action);
    }
}