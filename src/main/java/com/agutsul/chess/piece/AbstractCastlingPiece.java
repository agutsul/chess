package com.agutsul.chess.piece;

import static java.util.stream.Collectors.toSet;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
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
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.piece.state.ActivePieceState;
import com.agutsul.chess.piece.state.CastlingablePieceState;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.Rule;

abstract class AbstractCastlingPiece<COLOR extends Color>
        extends AbstractPiece<COLOR>
        implements Castlingable {

    private static final Logger LOGGER = getLogger(AbstractCastlingPiece.class);

    AbstractCastlingPiece(Board board, Type type, COLOR color,
                          String unicode, Position position, int direction,
                          Rule<Piece<?>, Collection<Action<?>>> actionRule,
                          Rule<Piece<?>, Collection<Impact<?>>> impactRule) {

        super(board, type, color, unicode, position, direction,
                new ActiveCastlingablePieceState<>(board, actionRule, impactRule)
        );
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

    private void doCastling(Position position) {
        super.doMove(position);
    }

    private void cancelCastling(Position position) {
        super.cancelMove(position);
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

            if (piece instanceof PieceProxy) {
                cancelCastling((AbstractPieceProxy<?>) piece, position);
            } else {
                cancelCastling((AbstractCastlingPiece<?>) piece, position);
            }
        }

        private static void cancelCastling(AbstractPieceProxy<?> proxy, Position position) {
            cancelCastling((AbstractCastlingPiece<?>) proxy.origin, position);
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
                                     Rule<Piece<?>, Collection<Action<?>>> actionRule,
                                     Rule<Piece<?>, Collection<Impact<?>>> impactRule) {

            super(new ActivePieceStateImpl<>(board, actionRule, impactRule));
            this.board = board;
        }

        @Override
        public void castling(PIECE piece, Position position) {
            LOGGER.info("Castling '{}' to '{}'", piece, position);

            var actions = board.getActions(piece, Action.Type.CASTLING);
            var validAction = actions.stream()
                    .map(action -> (PieceCastlingAction<?,?,?>) action)
                    .filter(action -> isValidAction(action, position))
                    .findFirst();

            if (validAction.isEmpty()) {
                throw new IllegalActionException(
                        String.format("%s invalid castling to %s", piece, position)
                );
            }

            PieceCastlingAction<?,?,?> castlingAction = validAction.get();

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

        private static void doCastling(CastlingMoveAction<?,?> action) {
            var piece = action.getSource();
            var position = action.getPosition();

            LOGGER.info("Castling '{}' to '{}'", piece, position);

            if (piece instanceof PieceProxy) {
                doCastling((AbstractPieceProxy<?>) piece, position);
            } else {
                doCastling((AbstractCastlingPiece<?>) piece, position);
            }
        }

        private static void doCastling(AbstractPieceProxy<?> proxy, Position position) {
            doCastling((AbstractCastlingPiece<?>) proxy.origin, position);
        }

        private static void doCastling(AbstractCastlingPiece<?> piece, Position position) {
            piece.doCastling(position);
        }
    }
}