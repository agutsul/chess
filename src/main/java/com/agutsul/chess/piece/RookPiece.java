package com.agutsul.chess.piece;

import com.agutsul.chess.Color;

public interface RookPiece<COLOR extends Color>
        extends Piece<COLOR>, Movable, Capturable, Castlingable, Demotable, Disposable, Restorable {

}