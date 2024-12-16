package com.agutsul.chess.activity.action.memento;

public class CheckMatedActionMemento<SOURCE,TARGET>
        extends AbstractCheckActionMemento<SOURCE,TARGET> {

    public CheckMatedActionMemento(ActionMemento<SOURCE,TARGET> origin) {
        super(origin, true, true);
    }
}