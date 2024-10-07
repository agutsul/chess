package com.agutsul.chess.piece.algo;

import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Piece;

public interface CapturePieceAlgo<COLOR extends Color,
                                  PIECE extends Piece<COLOR> & Capturable,
                                  POSITION>
        extends Algo<PIECE, Collection<POSITION>> {

}