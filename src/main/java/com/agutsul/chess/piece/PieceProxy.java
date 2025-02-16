package com.agutsul.chess.piece;

import com.agutsul.chess.color.Color;

interface PieceProxy<PIECE extends Piece<?>>
        extends Piece<Color> {

}