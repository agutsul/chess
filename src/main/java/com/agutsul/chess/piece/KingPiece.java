package com.agutsul.chess.piece;

import com.agutsul.chess.color.Color;

public interface KingPiece<COLOR extends Color>
        extends Piece<COLOR>, Movable, Capturable, Castlingable, Checkable {

}