package com.agutsul.chess.action;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Movable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public abstract class AbstractPromoteAction<COLOR extends Color,
                                            PIECE extends Piece<COLOR> & Movable & Capturable>
        extends AbstractSourceAction<AbstractTargetAction<PIECE,?>> {

    AbstractPromoteAction(AbstractTargetAction<PIECE,?> source) {
        super(Type.PROMOTE, source);
    }

    @Override
    public String getCode() {
        return String.format("%s?", getSource());
    }

    @Override
    public Position getPosition() {
        return getSource().getPosition();
    }
}