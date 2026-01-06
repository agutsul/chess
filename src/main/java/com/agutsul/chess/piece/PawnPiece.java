package com.agutsul.chess.piece;

import com.agutsul.chess.Backwardable;
import com.agutsul.chess.Blockadable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Disposable;
import com.agutsul.chess.EnPassantable;
import com.agutsul.chess.Isolatable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.Promotable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.Restorable;
import com.agutsul.chess.color.Color;

public interface PawnPiece<COLOR extends Color>
        extends Piece<COLOR>, Movable, Capturable,
                Promotable, Protectable, EnPassantable,
                Disposable, Restorable, Pinnable,
                Blockadable, Isolatable, Backwardable {

    int BIG_STEP_MOVE = 2;
}