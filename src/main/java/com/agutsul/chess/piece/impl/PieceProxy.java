package com.agutsul.chess.piece.impl;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

interface PieceProxy<COLOR extends Color,PIECE extends Piece<COLOR>>
        extends Piece<COLOR> {

    PIECE getOrigin();
}