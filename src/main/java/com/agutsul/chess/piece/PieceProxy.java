package com.agutsul.chess.piece;

import com.agutsul.chess.color.Color;

public interface PieceProxy<PIECE extends Piece<?>>
        extends Piece<Color> {

}