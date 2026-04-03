package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Promotable;
import com.agutsul.chess.activity.AbstractSourceActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public interface PiecePromoteImpact<COLOR extends Color,
                                    PIECE extends Piece<COLOR> & Promotable>
        extends Impact<AbstractSourceActivity<Impact.Type,PIECE>> {

    PIECE getPiece();
    Piece.Type getPieceType();
}