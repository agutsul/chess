package com.agutsul.chess.action.memento;

import com.agutsul.chess.journal.Memento;
import com.agutsul.chess.position.Position;

public final class ActionMemento implements Memento {

    private final String source;
    private final String target;

    public ActionMemento(Position source, Position target) {
        this(String.valueOf(source), String.valueOf(target));
    }

    public ActionMemento(String source, String target) {
        this.source = source;
        this.target = target;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return String.format("%s %s", source, target);
    }
}