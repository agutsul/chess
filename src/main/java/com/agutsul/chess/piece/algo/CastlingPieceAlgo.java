package com.agutsul.chess.piece.algo;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public interface CastlingPieceAlgo<COLOR  extends Color,
                                   PIECE  extends Piece<COLOR> & Movable & Castlingable,
                                   RESULT extends Calculatable>
        extends MovePieceAlgo<COLOR,PIECE,RESULT> {

}