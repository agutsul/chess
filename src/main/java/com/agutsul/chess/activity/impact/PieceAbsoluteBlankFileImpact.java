package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Fileable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;

public final class PieceAbsoluteBlankFileImpact<COLOR extends Color,
                                                PIECE extends Piece<COLOR> & Capturable & Movable & Lineable & Fileable>
        extends AbstractPieceBlankFileImpact<COLOR,PIECE> {

    public PieceAbsoluteBlankFileImpact(PIECE piece, Line line) {
        super(Mode.ABSOLUTE, piece, line);
    }
}