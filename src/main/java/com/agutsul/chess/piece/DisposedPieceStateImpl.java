package com.agutsul.chess.piece;

import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.Collection;

import org.slf4j.Logger;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Disposable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.piece.state.DisposedPieceState;
import com.agutsul.chess.position.Position;

final class DisposedPieceStateImpl<PIECE extends Piece<?> & Movable & Capturable & Disposable>
        extends AbstractPieceState<PIECE>
        implements DisposedPieceState<PIECE> {

    private static final Logger LOGGER = getLogger(DisposedPieceStateImpl.class);

    private Instant disposedAt;

    DisposedPieceStateImpl(Instant disposedAt) {
        super(Type.INACTIVE);
        this.disposedAt = disposedAt;
    }

    @Override
    public Instant getDisposedAt() {
        return this.disposedAt;
    }

    @Override
    public Collection<Action<?>> calculateActions(PIECE piece) {
        LOGGER.warn("Calculate actions of disabled piece '{}'", piece);
        return emptyList();
    }

    @Override
    public Collection<Action<?>> calculateActions(PIECE piece, Action.Type actionType) {
        LOGGER.warn("Calculate actions({}) of disabled piece '{}'",
                actionType.name(),
                piece
        );

        return emptyList();
    }

    @Override
    public Collection<Impact<?>> calculateImpacts(PIECE piece) {
        LOGGER.warn("Calculate impacts of disabled piece '{}'", piece);
        return emptyList();
    }

    @Override
    public Collection<Impact<?>> calculateImpacts(PIECE piece, Impact.Type impactType) {
        LOGGER.warn("Calculate impacts({}) of disabled piece '{}'",
                impactType.name(),
                piece
        );

        return emptyList();
    }

    @Override
    public void move(PIECE piece, Position position) {
        LOGGER.warn("Move disabled piece '{}' to '{}'", piece, position);
        // do nothing because piece is disposed
    }

    @Override
    public void capture(PIECE piece, Piece<?> targetPiece) {
        LOGGER.warn("Capture by disabled piece '{}' of '{}'", targetPiece, piece);
        // do nothing because piece is disposed
    }
}