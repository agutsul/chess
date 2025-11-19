package com.agutsul.chess.rule.checkmate;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;

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

        var availablePositions = Stream.of(board.getActions(king, Action.Type.MOVE))
                .flatMap(Collection::stream)
                .map(Action::getPosition)
                .filter(targetPosition -> !board.isAttacked(targetPosition,  attackerColor))
                .filter(targetPosition -> !board.isMonitored(targetPosition, attackerColor))
                .findFirst();

        return !availablePositions.isEmpty();
    }
}