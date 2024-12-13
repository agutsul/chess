package com.agutsul.chess.piece;

import static java.time.Instant.now;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceEnPassantAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.pawn.PawnPieceActionRule;
import com.agutsul.chess.piece.pawn.PawnPieceImpactRule;
import com.agutsul.chess.piece.state.EnPassantablePieceState;
import com.agutsul.chess.piece.state.PieceState;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.Rule;

final class PawnPieceImpl<COLOR extends Color>
        extends AbstractPiece<COLOR>
        implements PawnPiece<COLOR> {

    private static final Logger LOGGER = getLogger(PawnPieceImpl.class);

    private static final PieceState<?,?> DISPOSED_STATE = new PawnDisposedPieceState<>();

    PawnPieceImpl(Board board, COLOR color, String unicode, Position position,
                  int direction, int promotionLine, int initialLine) {

        super(board, Piece.Type.PAWN, color, unicode, position, direction,
                new PawnActivePieceState<>(board,
                        new PawnPieceActionRule(board, direction, initialLine, promotionLine),
                        new PawnPieceImpactRule(board, direction)
                )
        );
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public void dispose() {
        LOGGER.info("Dispose origin pawn '{}'", this);

        super.dispose();
        this.currentState = (PieceState<COLOR,Piece<COLOR>>) DISPOSED_STATE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void enpassant(PawnPiece<?> piece, Position position) {
        var state = (EnPassantablePieceState<?,?>) getState();
        ((EnPassantablePieceState<COLOR,PawnPiece<COLOR>>) state).enpassant(this, piece, position);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void unenpassant(PawnPiece<?> piece) {
        var state = (EnPassantablePieceState<?,?>) getState();
        ((EnPassantablePieceState<COLOR,PawnPiece<COLOR>>) state).unenpassant(this, piece);
    }

    static abstract class AbstractEnPassantablePieceState<COLOR extends Color,
                                                          PIECE extends PawnPiece<COLOR>>
            extends AbstractPieceStateProxy<COLOR,PIECE>
            implements EnPassantablePieceState<COLOR,PIECE> {

        private static final Logger LOGGER = getLogger(AbstractEnPassantablePieceState.class);

        AbstractEnPassantablePieceState(AbstractPieceState<COLOR,PIECE> originState) {
            super(originState);
        }

        @Override
        public void unenpassant(PIECE piece, PawnPiece<?> targetPiece) {
            LOGGER.info("Undo en-passante '{}' by '{}'", targetPiece, piece);
            ((AbstractPiece<?>) piece).cancelCapture(targetPiece);
        }
    }

    static final class PawnActivePieceState<COLOR extends Color,
                                            PIECE extends PawnPiece<COLOR>>
            extends AbstractEnPassantablePieceState<COLOR,PIECE> {

        private static final Logger LOGGER = getLogger(PawnActivePieceState.class);

        private final Board board;

        PawnActivePieceState(Board board,
                             Rule<Piece<?>, Collection<Action<?>>> actionRule,
                             Rule<Piece<?>, Collection<Impact<?>>> impactRule) {

            super(new ActivePieceState<>(board, actionRule, impactRule));
            this.board = board;
        }

        @Override
        public void enpassant(PIECE piece, PawnPiece<?> targetPiece, Position targetPosition) {
            LOGGER.info("En-passante '{}' by '{}'", targetPiece, piece);

            var actions = board.getActions(piece, PieceEnPassantAction.class);
            var isValid = actions.stream()
                    .map(action -> (PieceEnPassantAction<?,?,?,?>) action)
                    .anyMatch(action -> Objects.equals(action.getTarget(), targetPiece)
                                            && Objects.equals(action.getPosition(), targetPosition));

            if (!isValid) {
                throw new IllegalActionException(
                        String.format("%s invalid en passant of %s", piece, targetPosition)
                );
            }

            // save captured timestamp
            targetPiece.setCapturedAt(now());

            // remove target pawn from board
            targetPiece.dispose();

            // move this piece to target position
            ((AbstractPiece<?>) piece).doMove(targetPosition);
        }
    }

    static final class PawnDisposedPieceState<COLOR extends Color,
                                              PIECE extends PawnPiece<COLOR>>
            extends AbstractEnPassantablePieceState<COLOR,PIECE> {

        private static final Logger LOGGER = getLogger(PawnDisposedPieceState.class);

        PawnDisposedPieceState() {
            super(new DisposedPieceState<>());
        }

        @Override
        public void enpassant(PIECE piece, PawnPiece<?> targetPiece, Position targetPosition) {
            LOGGER.warn("En-passante by disabled '{}' by '{}'", targetPiece, this);
            // do nothing
        }
    }
}