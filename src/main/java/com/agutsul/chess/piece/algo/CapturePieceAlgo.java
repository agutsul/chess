package com.agutsul.chess.piece.algo;

import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public interface CapturePieceAlgo<COLOR extends Color,
                                  PIECE extends Piece<COLOR> & Capturable,
                                  RESULT extends Calculatable>
        extends Algo<PIECE,Collection<RESULT>> {

}