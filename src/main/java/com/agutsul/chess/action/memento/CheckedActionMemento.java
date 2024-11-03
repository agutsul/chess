package com.agutsul.chess.action.memento;

public class CheckedActionMemento<SOURCE,TARGET>
        extends ActionMementoProxy<SOURCE, TARGET> {

    public CheckedActionMemento(ActionMemento<SOURCE, TARGET> origin) {
        super(origin);
    }
}