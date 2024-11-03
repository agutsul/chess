package com.agutsul.chess.mock;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.Action.Type;
import com.agutsul.chess.action.memento.ActionMemento;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public class ActionMementoMock<SOURCE,TARGET>
        implements ActionMemento<SOURCE,TARGET> {

    private final Color color;
    private final Action.Type actionType;
    private final Piece.Type pieceType;
    private final SOURCE source;
    private final TARGET target;

    public ActionMementoMock(Color color,
                             Action.Type actionType,
                             Piece.Type pieceType,
                             SOURCE source,
                             TARGET target) {
        this.color = color;
        this.actionType = actionType;
        this.pieceType = pieceType;
        this.source = source;
        this.target = target;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public Type getActionType() {
        return this.actionType;
    }

    @Override
    public Piece.Type getPieceType() {
        return this.pieceType;
    }

    @Override
    public SOURCE getSource() {
        return this.source;
    }

    @Override
    public TARGET getTarget() {
        return this.target;
    }
}