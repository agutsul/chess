package com.agutsul.chess.activity.action.memento;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Memento;
import com.agutsul.chess.piece.Piece;

public interface ActionMemento<SOURCE,TARGET>
        extends Memento {

    // template method
    default String getCode() {
        return null;
    }

    Color getColor();
    Action.Type getActionType();
    Piece.Type getPieceType();
    SOURCE getSource();
    TARGET getTarget();
}