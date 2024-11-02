package com.agutsul.chess.action.memento;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Memento;

public interface ActionMemento<SOURCE,TARGET>
        extends Memento {

    Color getColor();
    Action.Type getActionType();
    SOURCE getSource();
    TARGET getTarget();
}