package com.agutsul.chess.piece.impl;

import org.slf4j.Logger;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.state.CapturablePieceState;
import com.agutsul.chess.piece.state.MovablePieceState;
import com.agutsul.chess.piece.state.PieceState;
import com.agutsul.chess.position.Position;

abstract class AbstractPieceState<PIECE extends Piece<?> & Movable & Capturable>
        implements PieceState<PIECE>,
                   MovablePieceState<PIECE>,
                   CapturablePieceState<PIECE> {

    protected final Logger logger;
    private final PieceState.Type type;

    AbstractPieceState(Logger logger, PieceState.Type type) {
        this.logger = logger;
        this.type = type;
    }

    @Override
    public final void unmove(PIECE piece, Position position) {
        logger.info("Undo move '{}' to '{}'", piece, position);
        ((AbstractPiece<?>) piece).cancelMove(position);
    }

    @Override
    public final void uncapture(PIECE piece, Piece<?> targetPiece) {
        logger.info("Undo capture '{}' by '{}'", targetPiece, piece);
        ((AbstractPiece<?>) piece).cancelCapture(targetPiece);
    }

    @Override
    public final PieceState.Type getType() {
        return this.type;
    }

    @Override
    public final String toString() {
        return this.type.name();
    }
}