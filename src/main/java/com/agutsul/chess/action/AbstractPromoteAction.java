package com.agutsul.chess.action;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractSourceActivity;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.Positionable;

public abstract class AbstractPromoteAction<COLOR extends Color,
                                            PIECE extends Piece<COLOR> & Movable & Capturable>
        extends AbstractSourceActivity<AbstractTargetActivity<PIECE,?>>
        implements Action<AbstractTargetActivity<PIECE,?>> {

    AbstractPromoteAction(AbstractTargetActivity<PIECE,?> source) {
        super(Action.Type.PROMOTE, source);
    }

    @Override
    public String getCode() {
        return String.format("%s?", getSource());
    }

    @Override
    public Position getPosition() {
        return ((Positionable) getSource()).getPosition();
    }

    @Override
    public String toString() {
        return getCode();
    }
}