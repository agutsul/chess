package com.agutsul.chess.piece.algo;

import java.util.Collection;

import com.agutsul.chess.Movable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public interface MovePieceAlgo<COLOR extends Color,
                               PIECE extends Piece<COLOR> & Movable,
                               POSITION>
        extends Algo<PIECE,Collection<POSITION>> {

}