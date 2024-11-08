package com.agutsul.chess.action.memento;

import static java.time.LocalDateTime.now;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

final class CastlingActionMemento
        extends AbstractActionMemento<ActionMemento<String,String>,ActionMemento<String,String>> {

    private final String code;

    CastlingActionMemento(String code,
                          Action.Type actionType,
                          ActionMemento<String,String> kingMemento,
                          ActionMemento<String,String> rookMemento) {

        super(now(), actionType, kingMemento, rookMemento);
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public Color getColor() {
        return getSource().getColor();
    }

    @Override
    public Piece.Type getPieceType() {
        return getSource().getPieceType();
    }

    @Override
    public String toString() {
        return String.format("%s(%s %s)", getActionType(), getSource(), getTarget());
    }
}