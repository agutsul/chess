package com.agutsul.chess.action.memento;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

final class CastlingActionMemento
        implements ActionMemento<ActionMemento<String,String>,ActionMemento<String,String>> {

    private final String code;
    private final Action.Type actionType;
    private final ActionMemento<String,String> kingMemento;
    private final ActionMemento<String,String> rookMemento;

    CastlingActionMemento(String code,
                          Action.Type actionType,
                          ActionMemento<String,String> kingMemento,
                          ActionMemento<String,String> rookMemento) {
        this.code = code;
        this.actionType = actionType;
        this.kingMemento = kingMemento;
        this.rookMemento = rookMemento;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public Color getColor() {
        return kingMemento.getColor();
    }

    @Override
    public Action.Type getActionType() {
        return actionType;
    }

    @Override
    public Piece.Type getPieceType() {
        return getSource().getPieceType();
    }

    @Override
    public ActionMemento<String,String> getSource() {
        return kingMemento;
    }

    @Override
    public ActionMemento<String,String> getTarget() {
        return rookMemento;
    }

    @Override
    public String toString() {
        return String.format("%s(%s %s)", getActionType(), getSource(), getTarget());
    }
}