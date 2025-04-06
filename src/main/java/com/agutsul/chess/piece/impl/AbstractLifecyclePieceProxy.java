package com.agutsul.chess.piece.impl;

import java.time.Instant;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Disposable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.Restorable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

abstract class AbstractLifecyclePieceProxy<COLOR extends Color,
                                           PIECE extends Piece<COLOR>
                                                    & Movable & Capturable & Protectable
                                                    & Restorable & Disposable>
        extends AbstractPieceProxy<COLOR,PIECE>
        implements Restorable, Disposable {

    AbstractLifecyclePieceProxy(PIECE origin) {
        super(origin);
    }

    @Override
    public void dispose(Instant instant) {
        this.origin.dispose(instant);
    }

    @Override
    public void restore() {
        this.origin.restore();
    }
}