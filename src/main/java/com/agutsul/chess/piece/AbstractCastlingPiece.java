package com.agutsul.chess.piece;

import static java.util.stream.Collectors.toSet;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCastlingAction;
import com.agutsul.chess.action.PieceCastlingAction.CastlingMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.state.AbstractPieceState;
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
        ((CastlingablePieceState) state).castling(this, position);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void dispose() {
        super.dispose();

        this.state = (AbstractPieceState<AbstractPiece<Color>>) DISPOSED_STATE;
    }

    static final class ActiveCastlingablePieceState<PIECE extends Piece<Color> & Castlingable & Movable & Capturable>
            extends ActivePieceState<PIECE>
            implements CastlingablePieceState<PIECE> {

        private static final Logger LOGGER = getLogger(ActiveCastlingablePieceState.class);

        ActiveCastlingablePieceState(Board board,
                                     Rule<Piece<Color>, Collection<Action<?>>> actionRule,
                                     Rule<Piece<Color>, Collection<Impact<?>>> impactRule) {
            super(board, actionRule, impactRule);
        }

        @Override
        public void castling(PIECE piece, Position position) {
            LOGGER.info("Castling '{}' to '{}'", piece, position);

            PieceCastlingAction<?,?,?> castlingAction =
                    board.getActions(piece, PieceCastlingAction.class).stream()
                        .findFirst()
                        .get();

            var possiblePositions = Stream.of(castlingAction)
                    .map(action -> {
                        var targetAction = action.getTarget();
                        var sourceAction = action.getSource();

                        return List.of(targetAction.getTarget(), sourceAction.getTarget());
                    })
                    .flatMap(Collection::stream)
                    .collect(toSet());

            if (!possiblePositions.contains(position)) {
                throw new IllegalActionException(
                        String.format("%s invalid castling to %s", piece, position)
                );
            }

            doCastling(castlingAction.getSource());
            doCastling(castlingAction.getTarget());
        }

        private void doCastling(CastlingMoveAction<?,?> action) {
            LOGGER.info("Castling '{}' to '{}'", action.getSource(), action.getPosition());

            var piece = (AbstractPiece<?>) action.getSource();
            piece.doMove(action.getPosition());
        }
    }

    static final class DisposedCastlingablePieceState<PIECE extends AbstractPiece<Color> & Castlingable>
            extends DisposedPieceState<PIECE>
            implements CastlingablePieceState<PIECE> {

        private static final Logger LOGGER = getLogger(DisposedCastlingablePieceState.class);

        @Override
        public void castling(PIECE piece, Position position) {
            LOGGER.info("Castling '{}' to '{}'", piece, position);
            // do nothing
        }
    }
}