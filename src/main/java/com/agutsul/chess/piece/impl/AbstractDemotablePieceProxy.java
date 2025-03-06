package com.agutsul.chess.piece.impl;

import org.slf4j.Logger;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Demotable;
import com.agutsul.chess.Disposable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.Restorable;
import com.agutsul.chess.piece.Piece;

abstract class AbstractDemotablePieceProxy<PIECE extends Piece<?>
                                                    & Movable & Capturable & Protectable
                                                    & Restorable & Disposable & Demotable>
        extends AbstractLifecyclePieceProxy<PIECE>
        implements Demotable {

    protected final Logger logger;

    AbstractDemotablePieceProxy(Logger logger, PIECE origin) {
        super(origin);
        this.logger = logger;
    }

    @Override
    public final void demote() {
        logger.info("Demote piece '{}'", this);
        this.origin.demote();
    }
}