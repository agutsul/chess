package com.agutsul.chess.rule.board;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Optional;
import java.util.concurrent.Callable;

import org.slf4j.Logger;

import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;

final class BoardStateEvaluationTask
        implements Callable<Optional<BoardState>> {

    private static final Logger LOGGER = getLogger(BoardStateEvaluationTask.class);

    private final BoardStateEvaluator<Optional<BoardState>> evaluator;
    private final Color color;

    BoardStateEvaluationTask(BoardStateEvaluator<Optional<BoardState>> evaluator,
                             Color color) {

        this.evaluator = evaluator;
        this.color = color;
    }

    @Override
    public Optional<BoardState> call() throws Exception {
        try {
            return evaluator.evaluate(color);
        } catch (Exception e) {
            var message = String.format(
                    "%s board evaluation failure",
                    evaluator.getClass().getSimpleName()
            );

            LOGGER.error(message, e);
        }

        return Optional.empty();
    }
}