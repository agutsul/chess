package com.agutsul.chess.rule.checkmate;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

final class KingMoveEvaluator<COLOR extends Color,
                              KING extends KingPiece<COLOR>>
        implements CheckMateEvaluator<COLOR, KING> {

    private static final Logger LOGGER = getLogger(KingMoveEvaluator.class);

    private final Board board;

    KingMoveEvaluator(Board board) {
        this.board = board;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Boolean evaluate(KING king) {
        LOGGER.info("Evaluate king '{}' escape ability", king);

        var attackerColor = king.getColor().invert();
        var actions = board.getActions((Piece<Color>) king);

        for (var action : actions) {
            if (Action.Type.MOVE.equals(action.getType())) {
                var targetPosition = action.getPosition();
                if (!board.isAttacked(targetPosition, attackerColor)
                        && !board.isMonitored(targetPosition, attackerColor)) {
                    return true;
                }
            }
        }

        return false;
    }
}