package com.agutsul.chess.action.event;

import com.agutsul.chess.action.memento.ActionMemento;

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