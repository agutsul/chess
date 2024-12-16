package com.agutsul.chess.activity.action.event;

import com.agutsul.chess.activity.action.Action;

public class ActionCancellingEvent
        extends AbstractProcessingActionEvent {

    public ActionCancellingEvent(Action<?> action) {
        super(action);
    }
}