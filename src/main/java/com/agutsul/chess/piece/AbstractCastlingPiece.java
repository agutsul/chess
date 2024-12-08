package com.agutsul.chess.piece;

import static java.util.stream.Collectors.toSet;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCastlingAction;
import com.agutsul.chess.action.PieceCastlingAction.CastlingMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.state.CastlingablePieceState;
import com.agutsul.chess.piece.state.PieceState;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.Rule;

abstract class AbstractCastlingPiece<COLOR extends Color>
        extends AbstractPiece<COLOR>
        implements ICastlingable {

    private static final Logger LOGGER = getLogger(AbstractCastlingPiece.class);

    private static final PieceState<?,?> DISPOSED_STATE = new DisposedCastlingablePieceState<>();

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
        LOGGER.info("'{}' caslting to '{}'", this, position);

        var state = (CastlingablePieceState<?,?>) getState();
        ((CastlingablePieceState<COLOR,AbstractCastlingPiece<COLOR>>) state).castling(this, position);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void uncastling(Position position) {
        LOGGER.info("'{}' undo caslting to '{}'", this, position);

        var state = (CastlingablePieceState<?,?>) getState();
        ((CastlingablePieceState<COLOR,AbstractCastlingPiece<COLOR>>) state).uncastling(this, position);
    }

    @Override
    public final void doCastling(Position position) {
        super.doMove(position);
    }

    @Override
    public final void cancelCastling(Position position) {
        super.cancelMove(position);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void dispose() {
        super.dispose();

        this.currentState = (PieceState<COLOR,Piece<COLOR>>) DISPOSED_STATE;
    }

    static abstract class AbstractCastlingablePieceState<COLOR extends Color,
                                                         PIECE extends Piece<COLOR> & Movable & Capturable & Castlingable>
            extends AbstractPieceStateProxy<COLOR,PIECE>
            implements CastlingablePieceState<COLOR,PIECE> {

        private static final Logger LOGGER = getLogger(AbstractCastlingablePieceState.class);

        AbstractCastlingablePieceState(AbstractPieceState<COLOR,PIECE> originState) {
            super(originState);
        }

        @Override
        public void uncastling(PIECE piece, Position position) {
            LOGGER.info("Undo castling '{}' to '{}'", piece, position);
            ((ICastlingable) piece).cancelCastling(position);
        }
    }

    static final class ActiveCastlingablePieceState<COLOR extends Color,
                                                    PIECE extends Piece<COLOR> & Movable & Capturable & Castlingable>
            extends AbstractCastlingablePieceState<COLOR,PIECE> {

        private static final Logger LOGGER = getLogger(ActiveCastlingablePieceState.class);

        private final Board board;

        ActiveCastlingablePieceState(Board board,
                                     Rule<Piece<?>, Collection<Action<?>>> actionRule,
                                     Rule<Piece<?>, Collection<Impact<?>>> impactRule) {

            super(new ActivePieceState<>(board, actionRule, impactRule));
            this.board = board;
        }

        @Override
        public void castling(PIECE piece, Position position) {
            LOGGER.info("Castling '{}' to '{}'", piece, position);

            var actions = board.getActions(piece, PieceCastlingAction.class);
            var validAction = actions.stream()
                    .filter(action -> isValidCastling(action, position))
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

        private boolean isValidCastling(PieceCastlingAction<?,?,?> action,
                                        Position position) {

            var possiblePositions = Stream.of(action.getTarget(), action.getSource())
                    .map(CastlingMoveAction::getTarget)
                    .collect(toSet());

            return possiblePositions.contains(position);
        }

        private void doCastling(CastlingMoveAction<?,?> action) {
            var piece = action.getSource();

            LOGGER.info("Castling '{}' to '{}'", piece, action.getPosition());

            ((ICastlingable) piece).doCastling(action.getPosition());
        }
    }

    static final class DisposedCastlingablePieceState<COLOR extends Color,
                                                      PIECE extends Piece<COLOR> & Movable & Capturable & Castlingable>
            extends AbstractCastlingablePieceState<COLOR,PIECE> {

        private static final Logger LOGGER = getLogger(DisposedCastlingablePieceState.class);

        DisposedCastlingablePieceState() {
            super(new DisposedPieceState<>());
        }

        @Override
        public void castling(PIECE piece, Position position) {
            LOGGER.info("Castling '{}' to '{}'", piece, position);
            // do nothing
        }
    }
}