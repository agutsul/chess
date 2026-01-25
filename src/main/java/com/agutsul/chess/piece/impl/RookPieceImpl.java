package com.agutsul.chess.piece.impl;

import static com.agutsul.chess.activity.action.Action.isCapture;
import static com.agutsul.chess.activity.action.Action.isCastling;
import static com.agutsul.chess.activity.action.Action.isMove;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

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

        this(board, position,
                new PieceContext<>(Piece.Type.ROOK, color, unicode, direction),
                Castling.of(position)
        );
    }

    private RookPieceImpl(Board board, Position position,
                          PieceContext<COLOR> context, Castling castling) {

        super(board, position, context,
                new RookPieceActionRule<>(board),
                new RookPieceImpactRule<>(board),
                nonNull(castling) ? List.of(castling.side()) : emptyList()
        );
    }

    @Override
    public Collection<Action<?>> getActions() {
        // Castling action for rook should have less priority
        // (because by default castling is initiated by the king)
        // and as result be the last in the result collection.
        // Action order influences action auto-detection used by PerformActionCommand.
        return Stream.of(super.getActions())
                .flatMap(Collection::stream)
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

            var isPriority = (isCapture(action1) && isMove(action2))
                    || (isCapture(action1) && isCastling(action2))
                    || (isMove(action1) && isCastling(action2));

            return isPriority ? -1 : 1;
        }
    }

    static final class DisposedCastlingablePieceState<PIECE extends RookPiece<?>>
            extends AbstractCastlingablePieceState<PIECE>
            implements DisposedPieceState<PIECE> {

        private static final Logger LOGGER = getLogger(DisposedCastlingablePieceState.class);

        private final DisposedPieceState<PIECE> disposedState;

        DisposedCastlingablePieceState(Instant instant) {
            this(new DisposedPieceStateImpl<>(instant));
        }

        private <DPS extends AbstractPieceState<PIECE> & DisposedPieceState<PIECE>>
                DisposedCastlingablePieceState(DPS pieceState) {

            super(pieceState);
            this.disposedState = pieceState;
        }

        @Override
        public void castling(PIECE piece, Position position) {
            LOGGER.warn("Castling by disabled '{}' to '{}'", piece, position);
            // do nothing
        }

        @Override
        public Optional<Instant> getDisposedAt() {
            return this.disposedState.getDisposedAt();
        }
    }
}