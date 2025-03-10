package com.agutsul.chess.piece.impl;

import static java.time.Instant.now;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceEnPassantAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.activity.action.function.ActionFilter;
import com.agutsul.chess.activity.cache.ActivityCacheImpl;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.event.ResetPawnMoveActionEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Event;
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
                        new PawnPieceImpactRule<>(board, direction, promotionLine)
                )
        );
    }

    private PawnPieceImpl(Board board, COLOR color, String unicode, Position position,
                          int direction, PieceState<? extends PawnPiece<COLOR>> state) {

        super(board, Piece.Type.PAWN, color, unicode, position, direction,
                (AbstractPieceState<? extends Piece<COLOR>>) state,
                new PawnActionCache(),
                new ActivityCacheImpl<Impact.Type,Impact<?>>()
        );
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
        var blocks = getImpacts(Impact.Type.BLOCK);
        return !blocks.isEmpty();
    }

    @Override
    DisposedPieceState<?> createDisposedPieceState(Instant instant) {
        return new DisposedEnPassantablePieceState<>(instant);
    }

    @Override
    void process(Event event) {
        // give a chance for parent processor to handle event
        super.process(event);

        if (event instanceof ResetPawnMoveActionEvent) {
            process((ResetPawnMoveActionEvent) event);
        }
    }

    private void process(ResetPawnMoveActionEvent event) {
        if (Objects.equals(this, event.getPawnPiece())) {
            // cancel move to position
            cancelMove(getPosition());
            // move piece back to source position
            doMove(event.getPosition());
        }
    }

    @SuppressWarnings("unchecked")
    private static <A extends Action<?>> Collection<Action<?>> filter(Collection<Action<?>> actions,
                                                                      Class<A> actionClass) {

        var filter = new ActionFilter<>(actionClass);
        var filtered = (Collection<Action<?>>) filter.apply(actions);

        return filtered;
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

            var actions = board.getActions(piece, Action.Type.EN_PASSANT);
            var isValid = actions.stream()
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
                return calculateMoveActions(piece);
            case CAPTURE:
                return calculateCaptureActions(piece);
            default:
                return super.calculateActions(piece, actionType);
            }
        }

        private Collection<Action<?>> calculateMoveActions(PIECE piece) {
            var actions = this.actionRule.evaluate(
                    piece,
                    Action.Type.MOVE,
                    Action.Type.PROMOTE
            );

            return filter(actions, PieceMoveAction.class);
        }

        private Collection<Action<?>> calculateCaptureActions(PIECE piece) {
            var calculatedActions = this.actionRule.evaluate(
                    piece,
                    Action.Type.CAPTURE,
                    Action.Type.PROMOTE
            );

            var actions = new HashSet<Action<?>>();

            actions.addAll(filter(calculatedActions, PieceCaptureAction.class));
            actions.addAll(super.calculateActions(piece, Action.Type.EN_PASSANT));

            return actions;
        }
    }

    static final class DisposedEnPassantablePieceState<PIECE extends PawnPiece<?>>
            extends AbstractEnPassantablePieceState<PIECE>
            implements DisposedPieceState<PIECE> {

        private static final Logger LOGGER = getLogger(DisposedEnPassantablePieceState.class);

        DisposedEnPassantablePieceState(Instant instant) {
            super(new DisposedPieceStateImpl<>(instant));
        }

        @Override
        public void enpassant(PIECE piece, PawnPiece<?> targetPiece, Position targetPosition) {
            LOGGER.warn("En-passante by disabled '{}' by '{}'", targetPiece, this);
            // do nothing
        }

        @Override
        public Instant getDisposedAt() {
            return ((DisposedPieceState<?>) this.origin).getDisposedAt();
        }
    }

    static final class PawnActionCache
            extends ActivityCacheImpl<Action.Type,Action<?>> {

        @Override
        public void putAll(Collection<Action<?>> actions) {
            super.put(Action.Type.CAPTURE,    filter(actions, PieceCaptureAction.class));
            super.put(Action.Type.EN_PASSANT, filter(actions, PieceEnPassantAction.class));
            super.put(Action.Type.MOVE,       filter(actions, PieceMoveAction.class));
            super.put(Action.Type.PROMOTE,    filter(actions, PiecePromoteAction.class));
        }
    }
}