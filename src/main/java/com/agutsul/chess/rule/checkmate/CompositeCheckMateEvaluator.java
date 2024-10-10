package com.agutsul.chess.rule.checkmate;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;

public final class CompositeCheckMateEvaluator<COLOR extends Color,
                                               KING extends KingPiece<COLOR>>
        implements CheckMateEvaluator<COLOR, KING> {

    private static final Logger LOGGER = getLogger(CompositeCheckMateEvaluator.class);

    private final List<CheckMateEvaluator<COLOR, KING>> evaluators;

    public CompositeCheckMateEvaluator(Board board) {
        this.evaluators = List.of(
                new KingMoveEvaluator<>(board),
                new AttackerCaptureEvaluator<>(board),
                new AttackerPinEvaluator<>(board)
            );
    }

    @Override
    public Boolean evaluate(KING king) {
        var results = new ArrayList<Boolean>();

        var executor = newFixedThreadPool(evaluators.size());
        try {
            var tasks = evaluators.stream()
                    .map(evaluator -> new CheckMateEvaluatorTask<COLOR, KING>(king, evaluator))
                    .toList();

            try {
                for (var future : executor.invokeAll(tasks)) {
                    results.add(future.get());
                }
            } catch (InterruptedException e) {
                LOGGER.error("Checkmate evaluation interrupted", e);
            } catch (ExecutionException e) {
                LOGGER.error("Checkmate evaluation failed", e);
            }
        } finally {
            try {
                executor.shutdown();
                if (!executor.awaitTermination(1, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }

        var isCheckMated = !results.isEmpty() && !results.contains(true);
        LOGGER.info("Checkmate evaluation is done: checkMated='{}'", isCheckMated);

        return isCheckMated;
    }

    private static class CheckMateEvaluatorTask<COLOR extends Color,
                                                KING extends KingPiece<COLOR>>
            implements Callable<Boolean> {

        private final KING king;
        private final CheckMateEvaluator<COLOR, KING> checkMateEvaluator;

        CheckMateEvaluatorTask(KING king,
                               CheckMateEvaluator<COLOR, KING> checkMateEvaluator) {
            this.king = king;
            this.checkMateEvaluator = checkMateEvaluator;
        }

        @Override
        public Boolean call() throws Exception {
            return checkMateEvaluator.evaluate(king);
        }
    }
}