package com.agutsul.chess.activity.action;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Positionable;
import com.agutsul.chess.activity.AbstractSourceActivity;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public abstract class AbstractPromoteAction<COLOR extends Color,
                                            PIECE extends Piece<COLOR> & Movable & Capturable>
        extends AbstractSourceActivity<AbstractTargetActivity<PIECE,?>>
        implements Action<AbstractTargetActivity<PIECE,?>> {

    AbstractPromoteAction(AbstractTargetActivity<PIECE,?> source) {
        super(Action.Type.PROMOTE, source);
    }

    @Override
    public final String getCode() {
        return String.format("%s?", getSource());
    }

    @Override
    public final Position getPosition() {
        return ((Positionable) getSource()).getPosition();
    }

    @Override
    public final String toString() {
        return getCode();
    }
}