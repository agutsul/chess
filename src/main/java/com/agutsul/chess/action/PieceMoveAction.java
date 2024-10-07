package com.agutsul.chess.action;

import com.agutsul.chess.Color;
import com.agutsul.chess.piece.Movable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceMoveAction<COLOR extends Color,
                             PIECE extends Piece<COLOR> & Movable>
        extends AbstractTargetAction<PIECE, Position> {

    public PieceMoveAction(PIECE piece, Position position) {
        this(Action.Type.MOVE, piece, position);
    }

    PieceMoveAction(Type type, PIECE piece, Position position) {
        super(type, piece, position);
    }

    @Override
    public String getCode() {
        return String.format("%s->%s", getSource(), getPosition());
    }

    @Override
    public void execute() {
        getSource().move(getPosition());
    }

    @Override
    public Position getPosition() {
        return getTarget();
    }
}