package com.agutsul.chess.piece.impl;

import org.slf4j.Logger;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Demotable;
import com.agutsul.chess.Disposable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.Restorable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.Piece;

abstract class AbstractDemotablePieceProxy<PIECE extends Piece<?>
                                                    & Movable & Capturable & Protectable
                                                    & Restorable & Disposable & Pinnable
                                                    & Demotable>
        extends AbstractPinnablePieceProxy<PIECE>
        implements Demotable {

    AbstractDemotablePieceProxy(Logger logger, Board board, PIECE origin) {
        super(logger, board, origin);
    }

    @Override
    public final void demote() {
        this.origin.demote();
    }
}