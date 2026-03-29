package com.agutsul.chess.piece.algo;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Promotable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public interface PromotePieceAlgo<COLOR extends Color,
                                  PIECE extends Piece<COLOR> & Movable & Capturable & Promotable>
        extends MovePieceAlgo<COLOR,PIECE,Position>,
                CapturePieceAlgo<COLOR,PIECE,Position> {

}