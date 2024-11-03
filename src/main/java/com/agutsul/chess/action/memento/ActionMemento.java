package com.agutsul.chess.action.memento;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Memento;
import com.agutsul.chess.piece.Piece;

public interface ActionMemento<SOURCE,TARGET>
        extends Memento {

    Color getColor();
    Action.Type getActionType();
    Piece.Type getPieceType();
    SOURCE getSource();
    TARGET getTarget();
}