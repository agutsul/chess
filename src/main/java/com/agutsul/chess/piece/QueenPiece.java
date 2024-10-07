package com.agutsul.chess.piece;

import com.agutsul.chess.Color;

public interface QueenPiece<COLOR extends Color>
        extends Piece<COLOR>, Movable, Capturable, Disposable {

}