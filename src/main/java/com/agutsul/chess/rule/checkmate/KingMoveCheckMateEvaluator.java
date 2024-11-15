package com.agutsul.chess.rule.checkmate;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

final class KingMoveCheckMateEvaluator
        implements CheckMateEvaluator {

    private static final Logger LOGGER = getLogger(KingMoveCheckMateEvaluator.class);

    private final Board board;

    KingMoveCheckMateEvaluator(Board board) {
        this.board = board;
    }

    @Override
    public Boolean evaluate(KingPiece<?> king) {
        LOGGER.info("Evaluate king '{}' escape ability", king);

        var attackerColor = king.getColor().invert();
        var moveActions = board.getActions((Piece<?>) king, PieceMoveAction.class);

        for (var action : moveActions) {
            var targetPosition = action.getPosition();
            if (!board.isAttacked(targetPosition, attackerColor)
                    && !board.isMonitored(targetPosition, attackerColor)) {
                return true;
            }
        }

        return false;
    }
}