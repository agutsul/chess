package com.agutsul.chess.action;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Movable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public abstract class AbstractMoveAction<COLOR extends Color,
                                         PIECE extends Piece<COLOR> & Movable>
        extends AbstractTargetAction<PIECE, Position> {

    AbstractMoveAction(PIECE source, Position target) {
        super(Type.MOVE, source, target);
    }

    @Override
    public String getCode() {
        return String.format("%s->%s", getSource(), getPosition());
    }

    @Override
    public Position getPosition() {
        return getTarget();
    }
}