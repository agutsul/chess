package com.agutsul.chess.piece.impl;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

interface PieceProxy<PIECE extends Piece<?>>
        extends Piece<Color> {

    PIECE getOrigin();
}