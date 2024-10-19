package com.agutsul.chess.piece;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.piece.state.CapturablePieceState;
import com.agutsul.chess.piece.state.MovablePieceState;
import com.agutsul.chess.piece.state.PieceState;
import com.agutsul.chess.position.Position;

abstract class AbstractPieceState<PIECE extends Piece<Color> & Movable & Capturable>
        implements PieceState<PIECE>,
                   MovablePieceState<PIECE>,
                   CapturablePieceState<PIECE> {

    private static final Logger LOGGER = getLogger(AbstractPieceState.class);

    private final Type type;

    AbstractPieceState(Type type) {
        this.type = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void unmove(PIECE piece, Position position) {
        LOGGER.info("Undo move '{}' to '{}'", piece, position);
        ((AbstractPiece<Color>) piece).cancelMove(position);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void uncapture(PIECE piece, Piece<?> targetPiece) {
        LOGGER.info("Undo capture '{}' by '{}'", targetPiece, piece);
        ((AbstractPiece<Color>) piece).cancelCapture(targetPiece);
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.type.name();
    }
}