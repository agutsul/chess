package com.agutsul.chess.piece.impl;

import static com.agutsul.chess.activity.action.Action.isCapture;
import static com.agutsul.chess.activity.action.Action.isMove;
import static java.time.Instant.now;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceEnPassantAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.event.ResetPawnMoveActionEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.event.CompositeEventObserver;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.pawn.PawnPieceActionRule;
import com.agutsul.chess.piece.pawn.PawnPieceImpactRule;
import com.agutsul.chess.piece.state.ActivePieceState;
import com.agutsul.chess.piece.state.DisposedPieceState;
import com.agutsul.chess.piece.state.EnPassantablePieceState;
import com.agutsul.chess.piece.state.PieceState;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.Rule;

final class PawnPieceImpl<COLOR extends Color>
        extends AbstractPiece<COLOR>
        implements PawnPiece<COLOR> {

    PawnPieceImpl(Board board, COLOR color, String unicode, Position position,
                  int direction, int promotionLine, int initialLine) {

        this(board, color, unicode, position, direction,
                new ActiveEnPassantablePieceState<>(board,
                        new PawnPieceActionRule<>(board, direction, initialLine, promotionLine),
                        new PawnPieceImpactRule<>(board, direction, initialLine, promotionLine)
                )
        );
    }

    private PawnPieceImpl(Board board, COLOR color, String unicode, Position position,
                          int direction, PieceState<? extends PawnPiece<COLOR>> state) {

        super(board, Piece.Type.PAWN, color, unicode, position, direction,
                (AbstractPieceState<? extends Piece<COLOR>>) state
        );
    }

    @Override
    public Collection<Action<?>> getActions(Action.Type actionType) {
        if (isMove(actionType) || isCapture(actionType)) {
            var promoteActions = super.getActions(Action.Type.PROMOTE);
            if (!promoteActions.isEmpty()) {
                return Stream.of(promoteActions)
                        .flatMap(Collection::stream)
                        .map(action -> (PiecePromoteAction<?,?>) action)
                        .map(PiecePromoteAction::getSource)
                        .map(action -> (Action<?>) action)
                        .filter(action -> Objects.equals(actionType, action.getType()))
                        .collect(toList());
            }
        }

        return super.getActions(actionType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void enpassant(PawnPiece<?> piece, Position position) {
        var state = (EnPassantablePieceState<?>) getState();
        ((EnPassantablePieceState<PawnPiece<COLOR>>) state).enpassant(this, piece, position);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void unenpassant(PawnPiece<?> piece) {
        var state = (EnPassantablePieceState<?>) getState();
        ((EnPassantablePieceState<PawnPiece<COLOR>>) state).unenpassant(this, piece);
    }

    @Override
    public boolean isBlocked() {
        var blocks = getImpacts(Impact.Type.BLOCKADE);
        return !blocks.isEmpty();
    }

    @Override
    public boolean isIsolated() {
        var isolations = getImpacts(Impact.Type.ISOLATION);
        return !isolations.isEmpty();
    }

    @Override
    public boolean isBackwarded() {
        var backwards = getImpacts(Impact.Type.BACKWARD);
        return !backwards.isEmpty();
    }

    @Override
    public boolean isAccumulated() {
        var accumulations = getImpacts(Impact.Type.ACCUMULATION);
        return !accumulations.isEmpty();
    }

    @Override
    DisposedPieceState<?> createDisposedPieceState(Instant instant) {
        return new DisposedEnPassantablePieceState<>(instant);
    }

    @Override
    Observer createObserver() {
        return new CompositeEventObserver(
                new ClearPieceActivitiesObserver(),
                new CopyPieceVisitedPositionsObserver(),
                new ResetPawnMoveActionObserver()
        );
    }

    private final class ResetPawnMoveActionObserver
            extends AbstractEventObserver<ResetPawnMoveActionEvent> {

        @Override
        protected void process(ResetPawnMoveActionEvent event) {
            if (Objects.equals(PawnPieceImpl.this, event.getPawnPiece())) {
                // cancel move to position
                cancelMove(getPosition());
                // move piece back to source position
                doMove(event.getPosition());
            }
        }
    }

    static abstract class AbstractEnPassantablePieceState<PIECE extends PawnPiece<?>>
            extends AbstractPieceStateProxy<PIECE>
            implements EnPassantablePieceState<PIECE> {

        private static final Logger LOGGER = getLogger(AbstractEnPassantablePieceState.class);

        AbstractEnPassantablePieceState(AbstractPieceState<PIECE> originState) {
            super(originState);
        }

        @Override
        public void unenpassant(PIECE piece, PawnPiece<?> targetPiece) {
            LOGGER.info("Undo en-passante '{}' by '{}'", targetPiece, piece);
            ((AbstractPiece<?>) piece).cancelCapture(targetPiece);
        }
    }

    static final class ActiveEnPassantablePieceState<PIECE extends PawnPiece<?>>
            extends AbstractEnPassantablePieceState<PIECE>
            implements ActivePieceState<PIECE> {

        private static final Logger LOGGER = getLogger(ActiveEnPassantablePieceState.class);

        private final AbstractPieceRule<Action<?>,Action.Type> actionRule;
        private final Board board;

        @SuppressWarnings("unchecked")
        ActiveEnPassantablePieceState(Board board,
                                      Rule<Piece<?>, Collection<Action<?>>> actionRule,
                                      Rule<Piece<?>, Collection<Impact<?>>> impactRule) {

            super(new ActivePieceStateImpl<>(board, actionRule, impactRule));

            this.board = board;
            this.actionRule = (AbstractPieceRule<Action<?>,Action.Type>) actionRule;
        }

        @Override
        public void enpassant(PIECE piece, PawnPiece<?> targetPiece, Position targetPosition) {
            LOGGER.info("En-passante '{}' by '{}'", targetPiece, piece);

            var isValid = Stream.of(board.getActions(piece, Action.Type.EN_PASSANT))
                    .flatMap(Collection::stream)
                    .map(action -> (PieceEnPassantAction<?,?,?,?>) action)
                    .anyMatch(action -> Objects.equals(action.getTarget(), targetPiece)
                                            && Objects.equals(action.getPosition(), targetPosition));

            if (!isValid) {
                throw new IllegalActionException(
                        String.format("%s invalid en passant of %s", piece, targetPosition)
                );
            }

            // remove target pawn from board
            targetPiece.dispose(now());

            // move this piece to target position
            ((AbstractPiece<?>) piece).doMove(targetPosition);
        }

        @Override
        public Collection<Action<?>> calculateActions(PIECE piece, Action.Type actionType) {
            switch (actionType) {
            case MOVE:
                return this.actionRule.evaluate(piece, Action.Type.BIG_MOVE, Action.Type.MOVE);
            case CAPTURE:
                return this.actionRule.evaluate(piece, Action.Type.EN_PASSANT, Action.Type.CAPTURE);
            default:
                return super.calculateActions(piece, actionType);
            }
        }
    }

    static final class DisposedEnPassantablePieceState<PIECE extends PawnPiece<?>>
            extends AbstractEnPassantablePieceState<PIECE>
            implements DisposedPieceState<PIECE> {

        private static final Logger LOGGER = getLogger(DisposedEnPassantablePieceState.class);

        private final DisposedPieceState<PIECE> disposedState;

        DisposedEnPassantablePieceState(Instant instant) {
            this(new DisposedPieceStateImpl<>(instant));
        }

        private <DPS extends AbstractPieceState<PIECE> & DisposedPieceState<PIECE>>
                DisposedEnPassantablePieceState(DPS pieceState) {

            super(pieceState);
            this.disposedState = pieceState;
        }

        @Override
        public void enpassant(PIECE piece, PawnPiece<?> targetPiece, Position targetPosition) {
            LOGGER.warn("En-passante by disabled '{}' by '{}'", targetPiece, this);
            // do nothing
        }

        @Override
        public Optional<Instant> getDisposedAt() {
            return this.disposedState.getDisposedAt();
        }
    }
}