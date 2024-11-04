package com.agutsul.chess.action.memento;

import com.agutsul.chess.piece.Checkable;

public class CheckedActionMemento<SOURCE,TARGET>
        extends ActionMementoProxy<SOURCE,TARGET>
        implements Checkable {

    public CheckedActionMemento(ActionMemento<SOURCE,TARGET> origin) {
        super(origin);
    }

    @Override
    public boolean isChecked() {
        return true;
    }

    @Override
    public boolean isCheckMated() {
        return false;
    }
}