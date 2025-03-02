package com.agutsul.chess.piece;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Checkable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.Settable;
import com.agutsul.chess.color.Color;

public interface KingPiece<COLOR extends Color>
        extends Piece<COLOR>, Movable, Capturable,
                Castlingable, Checkable, Protectable,
                Settable<Castlingable.Side,Boolean> {

    void setChecked(boolean checked);
    void setCheckMated(boolean checkMated);
}