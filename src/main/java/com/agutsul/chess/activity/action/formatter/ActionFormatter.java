package com.agutsul.chess.activity.action.formatter;

import com.agutsul.chess.activity.action.memento.ActionMemento;

public interface ActionFormatter {
    String formatMemento(ActionMemento<?,?> memento);
}