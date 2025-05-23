package com.agutsul.chess.piece;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Demotable;
import com.agutsul.chess.Disposable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.Restorable;
import com.agutsul.chess.color.Color;

public interface BishopPiece<COLOR extends Color>
        extends Piece<COLOR>, Movable, Capturable,
                Demotable, Disposable, Restorable,
                Pinnable, Protectable {

}