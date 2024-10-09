package com.agutsul.chess.piece.algo;

import com.agutsul.chess.Color;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.EnPassantable;
import com.agutsul.chess.piece.Piece;

public interface EnPassantPieceAlgo<COLOR extends Color,
                                    PIECE extends Piece<COLOR> & Capturable & EnPassantable,
                                    POSITION>
        extends CapturePieceAlgo<COLOR, PIECE, POSITION> {

}