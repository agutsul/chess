package com.agutsul.chess.piece;

import com.agutsul.chess.color.Color;

public interface BishopPiece<COLOR extends Color>
        extends Piece<COLOR>, Movable, Capturable, Demotable, Disposable, Restorable, Captured {

}