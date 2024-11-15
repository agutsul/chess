package com.agutsul.chess.piece.algo;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Movable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.Promotable;

public interface PromotePieceAlgo<COLOR extends Color,
                                  PIECE extends Piece<COLOR> & Movable & Capturable & Promotable,
                                  POSITION>
    extends MovePieceAlgo<COLOR,PIECE,POSITION>,
            CapturePieceAlgo<COLOR,PIECE,POSITION> {

}