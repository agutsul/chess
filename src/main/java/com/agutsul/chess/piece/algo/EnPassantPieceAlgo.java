package com.agutsul.chess.piece.algo;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.EnPassantable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public interface EnPassantPieceAlgo<COLOR extends Color,
                                    PIECE extends Piece<COLOR> & Capturable & EnPassantable,
                                    RESULT extends Calculatable>
        extends CapturePieceAlgo<COLOR,PIECE,RESULT> {

}