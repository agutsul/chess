package com.agutsul.chess.piece;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.piece.state.CapturablePieceState;
import com.agutsul.chess.piece.state.MovablePieceState;
import com.agutsul.chess.piece.state.PieceState;
import com.agutsul.chess.position.Position;

abstract class AbstractPieceState<PIECE extends Piece<?> & Movable & Capturable>
        implements PieceState<PIECE>,
                   MovablePieceState<PIECE>,
                   CapturablePieceState<PIECE> {

    private static final Logger LOGGER = getLogger(AbstractPieceState.class);

    private final PieceState.Type type;

    AbstractPieceState(PieceState.Type type) {
        this.type = type;
    }

    @Override
    public final void unmove(PIECE piece, Position position) {
        LOGGER.info("Undo move '{}' to '{}'", piece, position);
        ((AbstractPiece<?>) piece).cancelMove(position);
    }

    @Override
    public final void uncapture(PIECE piece, Piece<?> targetPiece) {
        LOGGER.info("Undo capture '{}' by '{}'", targetPiece, piece);
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