package com.agutsul.chess.activity.action.memento;

public class CheckedActionMemento<SOURCE,TARGET>
        extends AbstractCheckActionMemento<SOURCE,TARGET> {

    public CheckedActionMemento(ActionMemento<SOURCE,TARGET> origin) {
        super(origin, true, false);
    }
}