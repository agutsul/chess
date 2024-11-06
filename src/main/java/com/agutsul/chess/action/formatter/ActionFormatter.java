package com.agutsul.chess.action.formatter;

import com.agutsul.chess.action.memento.ActionMemento;

public interface ActionFormatter {
    String formatMemento(ActionMemento<?,?> memento);
}