package com.agutsul.chess.action.event;

import com.agutsul.chess.action.memento.ActionMemento;
import com.agutsul.chess.event.Event;

public class ActionPerformedEvent
        implements Event {

    private final ActionMemento actionMemento;

    public ActionPerformedEvent(ActionMemento actionMemento) {
        this.actionMemento = actionMemento;
    }

    public ActionMemento getActionMemento() {
        return actionMemento;
    }
}