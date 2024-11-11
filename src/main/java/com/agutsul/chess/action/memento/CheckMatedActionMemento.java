package com.agutsul.chess.action.memento;

public class CheckMatedActionMemento<SOURCE,TARGET>
        extends AbstractCheckActionMemento<SOURCE,TARGET> {

    public CheckMatedActionMemento(ActionMemento<SOURCE,TARGET> origin) {
        super(origin, true, true);
    }
}