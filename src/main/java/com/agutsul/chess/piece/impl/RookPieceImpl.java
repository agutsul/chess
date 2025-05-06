package com.agutsul.chess.piece.impl;

import static com.agutsul.chess.activity.action.Action.isCapture;
import static com.agutsul.chess.activity.action.Action.isCastling;
import static com.agutsul.chess.activity.action.Action.isMove;
import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.rook.RookPieceActionRule;
import com.agutsul.chess.piece.rook.RookPieceImpactRule;
import com.agutsul.chess.piece.state.DisposedPieceState;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.action.AbstractCastlingActionRule.Castling;

final class RookPieceImpl<COLOR extends Color>
        extends AbstractCastlingPiece<COLOR>
        implements RookPiece<COLOR> {

    private static final Comparator<Action<?>> ROOK_COMPARATOR = new RookActionComparator();

    RookPieceImpl(Board board, COLOR color, String unicode,
                  Position position, int direction) {

        this(board, color, unicode, position, direction, Castling.of(position));
    }

    private RookPieceImpl(Board board, COLOR color, String unicode,
                          Position position, int direction, Castling castling) {

        super(board, Piece.Type.ROOK, color, unicode, position, direction,
                new RookPieceActionRule<>(board),
                new RookPieceImpactRule<>(board),
                castling != null ? List.of(castling.side()) : emptyList()
        );
    }

    @Override
    public Collection<Action<?>> getActions() {
        // Castling action for rook should have less priority
        // (because by default castling is initiated by the king)
        // and as result be the last in the result collection.
        // Action order influences action auto-detection used by PerformActionCommand.
        return super.getActions().stream()
                .sorted(ROOK_COMPARATOR)
                .toList();
    }

    @Override
    DisposedPieceState<?> createDisposedPieceState(Instant instant) {
        return new DisposedCastlingablePieceState<>(instant);
    }

    static final class RookActionComparator
            implements Comparator<Action<?>>, Serializable {

        private static final long serialVersionUID = 1L;

        @Override
        public int compare(Action<?> action1, Action<?> action2) {
            // expected order: capture, move, castling
            if (Objects.equals(action1.getType(), action2.getType())) {
                return 0;
            }

            var isHigherPriority = (isCapture(action1) && isMove(action2))
                    || (isCapture(action1) && isCastling(action2))
                    || (isMove(action1) && isCastling(action2));

            return isHigherPriority ? -1 : 1;
        }
    }

    static final class DisposedCastlingablePieceState<PIECE extends RookPiece<?>>
            extends AbstractCastlingablePieceState<PIECE>
            implements DisposedPieceState<PIECE> {

        private static final Logger LOGGER = getLogger(DisposedCastlingablePieceState.class);

        DisposedCastlingablePieceState(Instant instant) {
            super(new DisposedPieceStateImpl<>(instant));
        }

        @Override
        public void castling(PIECE piece, Position position) {
            LOGGER.warn("Castling by disabled '{}' to '{}'", piece, position);
            // do nothing
        }

        @Override
        public Instant getDisposedAt() {
            return ((DisposedPieceState<?>) this.origin).getDisposedAt();
        }
    }
}