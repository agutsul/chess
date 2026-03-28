package com.agutsul.chess.piece;

import com.agutsul.chess.color.Color;

public interface PieceProxy<COLOR extends Color,PIECE extends Piece<COLOR>>
        extends Piece<COLOR> {

    PIECE getOrigin();
}