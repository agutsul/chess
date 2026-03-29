package com.agutsul.chess.piece.algo;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.EnPassantable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public interface EnPassantPieceAlgo<COLOR extends Color,
                                    PIECE extends Piece<COLOR> & Capturable & EnPassantable>
        extends CapturePieceAlgo<COLOR,PIECE,Position> {

}