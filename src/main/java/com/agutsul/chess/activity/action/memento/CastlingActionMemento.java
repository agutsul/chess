package com.agutsul.chess.activity.action.memento;

import static java.time.LocalDateTime.now;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

final class CastlingActionMemento
        extends AbstractActionMemento<ActionMemento<String,String>,ActionMemento<String,String>> {

    private final Castlingable.Side side;

    CastlingActionMemento(Castlingable.Side side,
                          Action.Type actionType,
                          ActionMemento<String,String> kingMemento,
                          ActionMemento<String,String> rookMemento) {

        super(now(), actionType, kingMemento, rookMemento);
        this.side = side;
    }

    @Override
    public String getCode() {
        return getSide().name();
    }

    public Castlingable.Side getSide() {
        return this.side;
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