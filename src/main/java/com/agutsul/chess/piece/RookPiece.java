package com.agutsul.chess.piece;

import com.agutsul.chess.color.Color;

public interface RookPiece<COLOR extends Color>
        extends Piece<COLOR>, Movable, Capturable, Castlingable,
                Demotable, Disposable, Restorable, Captured,
                Pinnable, Protectable {

}