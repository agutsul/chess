package com.agutsul.chess.piece.impl;

import static com.agutsul.chess.activity.action.Action.isCastling;
import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.king.KingPieceActionRule;
import com.agutsul.chess.piece.king.KingPieceImpactRule;
import com.agutsul.chess.piece.state.ActivePieceState;
import com.agutsul.chess.piece.state.CastlingablePieceState;
import com.agutsul.chess.piece.state.CheckMatedPieceState;
import com.agutsul.chess.piece.state.CheckedPieceState;
import com.agutsul.chess.piece.state.DisposedPieceState;
import com.agutsul.chess.piece.state.PieceState;
import com.agutsul.chess.position.Position;

final class KingPieceImpl<COLOR extends Color>
        extends AbstractCastlingPiece<COLOR>
        implements KingPiece<COLOR> {

    private static final Logger LOGGER = getLogger(KingPieceImpl.class);

    private static final String DISPOSE_ERROR_MESSAGE = "Unable to dispose KING piece";
    private static final String RESTORE_ERROR_MESSAGE = "Unable to restore KING piece";

    private final CheckedPieceState<? extends KingPiece<?>> checkedPieceState;
    private final CheckMatedPieceState<? extends KingPiece<?>> checkMatedPieceState;

    KingPieceImpl(Board board, COLOR color, String unicode,
                  Position position, int direction) {

        super(board, Piece.Type.KING, color, unicode, position, direction,
                new KingPieceActionRule<>(board),
                new KingPieceImpactRule<>(board),
                List.of(Side.values())
        );

        this.checkedPieceState = new KingCheckedPieceState<>(getState());
        this.checkMatedPieceState = new KingCheckMatedPieceState<>(getState());
    }

    @Override
    public void setChecked(boolean isChecked) {
        LOGGER.info("Set {} king checked='{}' state", getColor(), isChecked);
        setState(isChecked
                ? (PieceState<?>) this.checkedPieceState
                : (PieceState<?>) this.activeState
        );
    }

    @Override
    public void setCheckMated(boolean isCheckMated) {
        LOGGER.info("Set {} king checkMated='{}' state", getColor(), isCheckMated);
        setState(isCheckMated
                ? (PieceState<?>) this.checkMatedPieceState
                : (PieceState<?>) this.checkedPieceState
        );
    }

    @Override
    public boolean isChecked() {
        return this.currentState instanceof CheckedPieceState<?>;
    }

    @Override
    public boolean isCheckMated() {
        return this.currentState instanceof CheckMatedPieceState<?>;
    }

    // prevent prohibited operations

    @Override
    public void dispose(Instant instant) {
        throw new UnsupportedOperationException(
                String.format("%s at '%s'", DISPOSE_ERROR_MESSAGE, instant)
        );
    }

    @Override
    public void restore() {
        throw new UnsupportedOperationException(RESTORE_ERROR_MESSAGE);
    }

    @Override
    DisposedPieceState<?> createDisposedPieceState(Instant instant) {
        throw new UnsupportedOperationException(
                String.format("%s at '%s'", DISPOSE_ERROR_MESSAGE, instant)
        );
    }

    static class KingCheckedPieceState<PIECE extends KingPiece<?>>
            extends AbstractPieceStateProxy<PIECE>
            implements CheckedPieceState<PIECE>,
                       CastlingablePieceState<PIECE>,
                       ActivePieceState<PIECE> {

        @SuppressWarnings("unchecked")
        KingCheckedPieceState(PieceState<? extends Piece<?>> origin) {
            super((AbstractCastlingablePieceState<PIECE>) origin);
        }

        @Override
        public Collection<Action<?>> calculateActions(PIECE piece) {
            var actions = new HashSet<Action<?>>();
            actions.addAll(calculateActions(piece, Action.Type.MOVE));
            actions.addAll(calculateActions(piece, Action.Type.CAPTURE));
            return actions;
        }

        @Override
        public Collection<Action<?>> calculateActions(PIECE piece, Action.Type actionType) {
            return isCastling(actionType)
                    ? emptyList()
                    : super.calculateActions(piece, actionType);
        }

        @Override
        public void castling(PIECE piece, Position position) {
            throw new IllegalActionException("Unable to perform castling for checked king");
        }

        @Override
        public void uncastling(PIECE piece, Position position) {
            ((AbstractCastlingablePieceState<PIECE>) this.origin).uncastling(piece, position);
        }
    }

    static final class KingCheckMatedPieceState<PIECE extends KingPiece<?>>
            extends KingCheckedPieceState<PIECE>
            implements CheckMatedPieceState<PIECE> {

        KingCheckMatedPieceState(PieceState<? extends Piece<?>> origin) {
            super(origin);
        }

        @Override
        public Collection<Action<?>> calculateActions(PIECE piece) {
            return emptyList();
        }

        @Override
        public Collection<Action<?>> calculateActions(PIECE piece, Action.Type actionType) {
            return emptyList();
        }

        @Override
        public Collection<Impact<?>> calculateImpacts(PIECE piece) {
            return emptyList();
        }

        @Override
        public Collection<Impact<?>> calculateImpacts(PIECE piece, Impact.Type impactType) {
            return emptyList();
        }

        @Override
        public void castling(PIECE piece, Position position) {
            throw new IllegalActionException("Unable to perform castling for check mated king");
        }

        @Override
        public void move(PIECE piece, Position position) {
            throw new IllegalActionException("Unable to perform move for check mated king");
        }

        @Override
        public void capture(PIECE piece, Piece<?> targetPiece) {
            throw new IllegalActionException("Unable to perform capture for check mated king");
        }
    }
}