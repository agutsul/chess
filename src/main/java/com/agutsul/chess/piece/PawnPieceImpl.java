package com.agutsul.chess.piece;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceEnPassantAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.pawn.PawnPieceActionRule;
import com.agutsul.chess.piece.pawn.PawnPieceImpactRule;
import com.agutsul.chess.piece.state.AbstractPieceState;
import com.agutsul.chess.piece.state.EnPassantablePieceState;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.Rule;

final class PawnPieceImpl<COLOR extends Color>
        extends AbstractPiece<COLOR>
        implements PawnPiece<COLOR> {

    private static final Logger LOGGER = getLogger(PawnPieceImpl.class);

    private static final PawnDisposedPieceState<?> DISPOSED_STATE =
            new PawnDisposedPieceState<>();

    PawnPieceImpl(Board board, COLOR color, String unicode, Position position,
            int direction, int promotionLine, int initialLine) {

        super(board, Piece.Type.PAWN, color, unicode, position,
                new PawnActivePieceState<>(board,
                    new PawnPieceActionRule(board, direction, initialLine, promotionLine),
                    new PawnPieceImpactRule(board, direction)
                )
        );
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public void dispose() {
        super.dispose();
        this.state = (AbstractPieceState<AbstractPiece<Color>>) DISPOSED_STATE;
    }

    @Override
    public void promote(Position targetPosition, Type pieceType) {
        LOGGER.info("Promote origin pawn '{}' to '{}'", this, pieceType);

        if (!isActive()) {
            return;
        }

        // remove origin pawn from board as all the promotion-related logic is in proxy class
        dispose();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void enPassant(PawnPiece<?> targetPiece, Position targetPosition) {
        ((EnPassantablePieceState) state).enPassant(this, targetPiece, targetPosition);
    }

    static final class PawnActivePieceState<PIECE extends PawnPiece<Color>>
            extends ActivePieceState<PIECE>
            implements EnPassantablePieceState<PIECE> {

        private static final Logger LOGGER = getLogger(PawnActivePieceState.class);

        PawnActivePieceState(Board board,
                             Rule<Piece<Color>, Collection<Action<?>>> actionRule,
                             Rule<Piece<Color>, Collection<Impact<?>>> impactRule) {
            super(board, actionRule, impactRule);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void enPassant(PIECE piece, PawnPiece<Color> targetPiece, Position targetPosition) {
            LOGGER.info("En-passante '{}' by '{}'", targetPiece, this);

            var isValid = board.getActions(piece).stream()
                    .filter(action -> Action.Type.EN_PASSANT.equals(action.getType()))
                    .map(action -> (PieceEnPassantAction<?,?,?,?>) action)
                    .anyMatch(action -> Objects.equals(action.getTarget(), targetPiece)
                                && Objects.equals(action.getPosition(), targetPosition));

            if (!isValid) {
                throw new IllegalActionException(
                        String.format("%s invalid en passant of %s", piece, targetPosition)
                    );
            }

            // remove target pawn from board
            targetPiece.dispose();
            // move this piece to target position
            ((AbstractPiece<Color>) piece).doMove(targetPosition);
        }
    }

    static final class PawnDisposedPieceState<PIECE extends AbstractPiece<Color> & PawnPiece<Color>>
            extends DisposedPieceState<PIECE>
            implements EnPassantablePieceState<PIECE> {

        private static final Logger LOGGER = getLogger(PawnDisposedPieceState.class);

        @Override
        public void enPassant(PIECE piece, PawnPiece<Color> targetPiece, Position targetPosition) {
            LOGGER.info("En-passante '{}' by '{}'", targetPiece, this);
            // do nothing
        }
    }
}