package com.agutsul.chess.piece;

import java.time.Instant;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Demotable;
import com.agutsul.chess.Disposable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.Restorable;

abstract class AbstractLifecyclePieceProxy<PIECE extends Piece<?>
                                                & Movable & Capturable & Protectable
                                                & Restorable & Disposable>
        extends AbstractPieceProxy<PIECE>
        implements Restorable, Disposable, Demotable {

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

    @Override
    public void demote() {
        ((Demotable) origin).demote();
    }
}