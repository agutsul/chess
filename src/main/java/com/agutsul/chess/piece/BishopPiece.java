package com.agutsul.chess.piece;

import com.agutsul.chess.Color;

public interface BishopPiece<COLOR extends Color>
        extends Piece<COLOR>, Movable, Capturable, Demotable, Disposable, Restorable {

}