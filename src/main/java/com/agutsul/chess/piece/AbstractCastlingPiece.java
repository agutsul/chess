package com.agutsul.chess.piece;

import static java.util.stream.Collectors.toSet;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCastlingAction;
import com.agutsul.chess.action.PieceCastlingAction.CastlingMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.state.CastlingablePieceState;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.Rule;

abstract class AbstractCastlingPiece<COLOR extends Color>
        extends AbstractPiece<COLOR>
        implements Castlingable {

    private static final Logger LOGGER = getLogger(AbstractCastlingPiece.class);

    private static final DisposedCastlingablePieceState<?> DISPOSED_STATE =
            new DisposedCastlingablePieceState<>();

    AbstractCastlingPiece(Board board, Type type,
            COLOR color, String unicode, Position position,
            Rule<Piece<Color>, Collection<Action<?>>> actionRule,
            Rule<Piece<Color>, Collection<Impact<?>>> impactRule) {

        super(board, type, color, unicode, position,
                new ActiveCastlingablePieceState<>(board, actionRule, impactRule)
        );
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final void castling(Position position) {
        LOGGER.info("'{}' caslting to '{}'", this, position);
        ((CastlingablePieceState) this.currentState).castling(this, position);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final void uncastling(Position position) {
        LOGGER.info("'{}' undo caslting to '{}'", this, position);
        ((CastlingablePieceState) this.currentState).uncastling(this, position);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void dispose() {
        super.dispose();
        this.currentState = (AbstractPieceState<AbstractPiece<Color>>) DISPOSED_STATE;
    }

    static abstract class AbstractCastlingablePieceState<PIECE extends AbstractPiece<Color> & Castlingable>
            extends AbstractPieceStateProxy<PIECE>
            implements CastlingablePieceState<PIECE> {

        private static final Logger LOGGER = getLogger(AbstractCastlingablePieceState.class);

        AbstractCastlingablePieceState(AbstractPieceState<PIECE> originState) {
            super(originState);
        }

        @Override
        public void uncastling(PIECE piece, Position position) {
            LOGGER.info("Undo castling '{}' to '{}'", piece, position);
            ((AbstractPiece<Color>) piece).cancelMove(position);
        }
    }

    static final class ActiveCastlingablePieceState<PIECE extends AbstractPiece<Color> & Castlingable>
            extends AbstractCastlingablePieceState<PIECE> {

        private static final Logger LOGGER = getLogger(ActiveCastlingablePieceState.class);

        private final Board board;

        ActiveCastlingablePieceState(Board board,
                                     Rule<Piece<Color>, Collection<Action<?>>> actionRule,
                                     Rule<Piece<Color>, Collection<Impact<?>>> impactRule) {

            super(new ActivePieceState<>(board, actionRule, impactRule));
            this.board = board;
        }

        @Override
        public void castling(PIECE piece, Position position) {
            LOGGER.info("Castling '{}' to '{}'", piece, position);

            var actions = board.getActions(piece, PieceCastlingAction.class);
            PieceCastlingAction<?,?,?> castlingAction = actions.iterator().next();

            validateCastling(castlingAction, piece, position);

            doCastling(castlingAction.getSource());
            doCastling(castlingAction.getTarget());
        }

        private void validateCastling(PieceCastlingAction<?,?,?> castlingAction,
                                      PIECE piece,
                                      Position position) {

            var actions = List.of(castlingAction.getTarget(), castlingAction.getSource());
            var possiblePositions = actions.stream()
                    .map(CastlingMoveAction::getTarget)
                    .collect(toSet());

            if (!possiblePositions.contains(position)) {
                throw new IllegalActionException(
                    String.format("%s invalid castling to %s", piece, position)
                );
            }
        }

        private void doCastling(CastlingMoveAction<?,?> action) {
            LOGGER.info("Castling '{}' to '{}'", action.getSource(), action.getPosition());

            var piece = (AbstractPiece<?>) action.getSource();
            piece.doMove(action.getPosition());
        }
    }

    static final class DisposedCastlingablePieceState<PIECE extends AbstractPiece<Color> & Castlingable>
            extends AbstractCastlingablePieceState<PIECE> {

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