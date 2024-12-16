package com.agutsul.chess.rule.checkmate;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashSet;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.position.Position;

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
        var moveActions = board.getActions(king, Action.Type.MOVE);

        var positions = new HashSet<Position>();
        for (var action : moveActions) {
            var targetPosition = action.getPosition();

            var isAttacked = board.isAttacked(targetPosition, attackerColor);
            var isMonitored = board.isMonitored(targetPosition, attackerColor);

            if (!isAttacked && !isMonitored) {
                positions.add(targetPosition);
            }
        }

        return !positions.isEmpty();
    }
}