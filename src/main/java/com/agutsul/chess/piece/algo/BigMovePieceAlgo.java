package com.agutsul.chess.piece.algo;

import java.util.Collection;

import com.agutsul.chess.BigMovable;
import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public interface BigMovePieceAlgo<COLOR extends Color,
                                  PIECE extends Piece<COLOR> & Movable & BigMovable,
                                  RESULT extends Calculatable>
        extends Algo<PIECE,Collection<RESULT>> {

}