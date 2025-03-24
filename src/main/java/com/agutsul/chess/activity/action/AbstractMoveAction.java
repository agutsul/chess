package com.agutsul.chess.activity.action;

import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public abstract class AbstractMoveAction<COLOR extends Color,
                                         PIECE extends Piece<COLOR> & Movable>
        extends AbstractTargetActivity<Action.Type,PIECE,Position>
        implements Action<PIECE> {

    AbstractMoveAction(PIECE source, Position target) {
        super(Action.Type.MOVE, source, target);
    }

    @Override
    public final String getCode() {
        return String.format("%s->%s", getPiece(), getPosition());
    }

    @Override
    @SuppressWarnings("unchecked")
    public final PIECE getPiece() {
        return getSource();
    }

    @Override
    public final Position getPosition() {
        return getTarget();
    }

    @Override
    public final String toString() {
        return getCode();
    }
}