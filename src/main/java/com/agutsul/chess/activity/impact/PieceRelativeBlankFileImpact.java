package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Fileable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;

public final class PieceRelativeBlankFileImpact<COLOR extends Color,
                                                PIECE extends Piece<COLOR> & Capturable & Movable & Lineable & Fileable>
        extends AbstractPieceBlankFileImpact<COLOR,PIECE> {

    public PieceRelativeBlankFileImpact(PIECE piece, Line line) {
        super(Mode.RELATIVE, piece, line);
    }

    @Override
    protected Integer calculateValue() {
        return getSource().getDirection();
    }
}