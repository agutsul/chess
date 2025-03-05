package com.agutsul.chess.piece.impl;

import java.time.Instant;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Disposable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.Restorable;
import com.agutsul.chess.piece.Piece;

abstract class AbstractLifecyclePieceProxy<PIECE extends Piece<?>
                                                    & Movable & Capturable & Protectable
                                                    & Restorable & Disposable>
        extends AbstractPieceProxy<PIECE>
        implements Restorable, Disposable {

    AbstractLifecyclePieceProxy(PIECE origin) {
        super(origin);
    }

    @Override
    public void dispose(Instant instant) {
        origin.dispose(instant);
    }

    @Override
    public void restore() {
        origin.restore();
    }
}