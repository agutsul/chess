package com.agutsul.chess.piece.impl;

import static com.agutsul.chess.activity.action.Action.isCastling;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCastlingAction;
import com.agutsul.chess.activity.action.PieceCastlingAction.CastlingMoveAction;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.event.SetCastlingableSideEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.event.CompositeEventObserver;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.state.ActivePieceState;
import com.agutsul.chess.piece.state.CastlingablePieceState;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.Rule;

abstract class AbstractCastlingPiece<COLOR extends Color>
        extends AbstractPiece<COLOR>
        implements Castlingable {

    private static final Logger LOGGER = getLogger(AbstractCastlingPiece.class);

    private final Map<Castlingable.Side,Boolean> sides;

    AbstractCastlingPiece(Board board, Piece.Type type, COLOR color,
                          String unicode, Position position, int direction,
                          Rule<Piece<?>,Collection<Action<?>>> actionRule,
                          Rule<Piece<?>,Collection<Impact<?>>> impactRule,
                          Collection<Side> sides) {

        super(board, type, color, unicode, position, direction,
                new ActiveCastlingablePieceState<>(board, actionRule, impactRule)
        );

        // by default enable castling
        this.sides = sides.stream().collect(toMap(identity(), entry -> true));
    }

    @Override
    public Collection<Action<?>> getActions() {
        return filterEnabledActions(super.getActions());
    }

    @Override
    public Collection<Action<?>> getActions(Action.Type actionType) {
        return filterEnabledActions(super.getActions(actionType));
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void castling(Position position) {
        LOGGER.info("'{}' castling to '{}'", this, position);

        var state = (CastlingablePieceState<?>) getState();
        ((CastlingablePieceState<AbstractCastlingPiece<COLOR>>) state).castling(this, position);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void uncastling(Position position) {
        LOGGER.info("'{}' undo castling to '{}'", this, position);

        var state = (CastlingablePieceState<?>) getState();
        ((CastlingablePieceState<AbstractCastlingPiece<COLOR>>) state).uncastling(this, position);
    }

    @Override
    final Observer createObserver() {
        return new CompositeEventObserver(
                new ClearPieceActivitiesObserver(),
                new CopyPieceVisitedPositionsObserver(),
                new SetCastlingableSideObserver()
        );
    }

    private void set(Castlingable.Side side, Boolean enabled) {
        if (this.sides.containsKey(side)) {
            this.sides.put(side, enabled);
        }
    }

    private Collection<Action<?>> filterEnabledActions(Collection<Action<?>> actions) {
        var filteredActions = actions.stream()
                .filter(action -> {
                    if (!isCastling(action)) {
                        // return all non-castling related actions
                        return true;
                    }

                    var castlingAction = (PieceCastlingAction<?,?,?>) action;
                    // filter castling actions to return ones for enabled sides only
                    return isTrue(sides.get(castlingAction.getSide()));
                })
                .toList();

        return filteredActions;
    }

    private void doCastling(Position position) {
        super.doMove(position);
    }

    private void cancelCastling(Position position) {
        super.cancelMove(position);
    }

    private final class SetCastlingableSideObserver
            extends AbstractEventObserver<SetCastlingableSideEvent> {

        @Override
        protected void process(SetCastlingableSideEvent event) {
            if (Objects.equals(getColor(), event.getColor())) {
                set(event.getSide(), event.isEnabled());
            }
        }
    }

    static abstract class AbstractCastlingablePieceState<PIECE extends Piece<?> & Movable & Capturable & Castlingable>
            extends AbstractPieceStateProxy<PIECE>
            implements CastlingablePieceState<PIECE> {

        private static final Logger LOGGER = getLogger(AbstractCastlingablePieceState.class);

        AbstractCastlingablePieceState(AbstractPieceState<PIECE> originState) {
            super(originState);
        }

        @Override
        public void uncastling(PIECE piece, Position position) {
            LOGGER.info("Undo castling '{}' to '{}'", piece, position);

            if (piece instanceof AbstractCastlingPiece) {
                cancelCastling((AbstractCastlingPiece<?>) piece, position);
            } else {
                cancelCastling((PieceProxy<?,?>) piece, position);
            }
        }

        private static void cancelCastling(PieceProxy<?,?> proxy, Position position) {
            cancelCastling((AbstractCastlingPiece<?>) proxy.getOrigin(), position);
        }

        private static void cancelCastling(AbstractCastlingPiece<?> piece, Position position) {
            piece.cancelCastling(position);
        }
    }

    static final class ActiveCastlingablePieceState<PIECE extends Piece<?> & Movable & Capturable & Castlingable>
            extends AbstractCastlingablePieceState<PIECE>
            implements ActivePieceState<PIECE> {

        private static final Logger LOGGER = getLogger(ActiveCastlingablePieceState.class);

        private final Board board;

        ActiveCastlingablePieceState(Board board,
                                     Rule<Piece<?>,Collection<Action<?>>> actionRule,
                                     Rule<Piece<?>,Collection<Impact<?>>> impactRule) {

            super(new ActivePieceStateImpl<>(board, actionRule, impactRule));
            this.board = board;
        }

        @Override
        public void castling(PIECE piece, Position position) {
            LOGGER.info("Castling '{}' to '{}'", piece, position);

            var possibleActions = board.getActions(piece, Action.Type.CASTLING);
            var possibleAction = possibleActions.stream()
                    .map(action -> (PieceCastlingAction<?,?,?>) action)
                    .filter(action -> isValidAction(action, position))
                    .findFirst();

            if (possibleAction.isEmpty()) {
                throw new IllegalActionException(
                        String.format("%s invalid castling to %s", piece, position)
                );
            }

            var castlingAction = possibleAction.get();

            doCastling(castlingAction.getSource());
            doCastling(castlingAction.getTarget());
        }

        private static boolean isValidAction(PieceCastlingAction<?,?,?> action,
                                             Position position) {

            var possiblePositions = Stream.of(action.getTarget(), action.getSource())
                    .map(CastlingMoveAction::getTarget)
                    .collect(toSet());

            return possiblePositions.contains(position);
        }

        @SuppressWarnings("unchecked")
        private void doCastling(CastlingMoveAction<?,?> action) {
            var piece = action.getSource();
            var position = action.getPosition();

            LOGGER.info("Castling '{}' to '{}'", piece, position);
            doCastling((PIECE) piece, position);
        }

        private void doCastling(PIECE piece, Position position) {
            if (piece instanceof AbstractCastlingPiece) {
                doCastling((AbstractCastlingPiece<?>) piece, position);
            } else {
                doCastling((PieceProxy<?,?>) piece, position);
            }
        }

        @SuppressWarnings("unchecked")
        private void doCastling(PieceProxy<?,?> proxy, Position position) {
            doCastling((PIECE) proxy.getOrigin(), position);
        }

        private void doCastling(AbstractCastlingPiece<?> piece, Position position) {
            piece.doCastling(position);
        }
    }
}