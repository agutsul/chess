package com.agutsul.chess.activity.action.event;

import com.agutsul.chess.activity.action.memento.ActionMemento;

public class ActionPerformedEvent
        extends AbstractProccessedActionEvent {

    private final ActionMemento<?,?> actionMemento;

    public ActionPerformedEvent(ActionMemento<?,?> actionMemento) {
        this.actionMemento = actionMemento;
    }

    public ActionMemento<?,?> getActionMemento() {
        return actionMemento;
    }
}