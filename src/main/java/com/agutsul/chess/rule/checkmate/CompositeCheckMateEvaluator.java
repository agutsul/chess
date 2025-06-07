package com.agutsul.chess.rule.checkmate;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.exception.GameInterruptionException;
import com.agutsul.chess.piece.KingPiece;

public final class CompositeCheckMateEvaluator
        implements CheckMateEvaluator {

    private static final Logger LOGGER = getLogger(CompositeCheckMateEvaluator.class);

    private final Board board;
    private final List<CheckMateEvaluator> evaluators;

    public CompositeCheckMateEvaluator(Board board) {
        this.board = board;
        this.evaluators = List.of(
                new KingMoveCheckMateEvaluator(board),
                new KingCaptureCheckMateEvaluator(board),
                new AttackerCaptureCheckMateEvaluator(board),
                new AttackerPinCheckMateEvaluator(board)
            );
    }

    @Override
    public Boolean evaluate(KingPiece<?> king) {
        var results = new ArrayList<Boolean>();

        var tasks = createEvaluationTasks(this.evaluators, king);
        try {
            var executor = board.getExecutorService();
            for (var future : executor.invokeAll(tasks)) {
                results.add(future.get());
            }
        } catch (InterruptedException e) {
            throw new GameInterruptionException("Checkmate evaluation interrupted");
        } catch (ExecutionException e) {
            LOGGER.error("Checkmate evaluation failed", e);
        }

        var isCheckMated = results.size() == this.evaluators.size() && !results.contains(true);
        LOGGER.info("Checkmate evaluation is done: checkMated='{}'", isCheckMated);

        return isCheckMated;
    }

    private static List<Callable<Boolean>> createEvaluationTasks(List<CheckMateEvaluator> evaluators,
                                                                 KingPiece<?> king) {
        var tasks = new ArrayList<Callable<Boolean>>();
        for (var evaluator : evaluators) {
            tasks.add(new CheckMateEvaluationTask(evaluator, king));
        }

        return tasks;
    }

    private static final class CheckMateEvaluationTask
            implements Callable<Boolean> {

        private static final Logger LOGGER = getLogger(CheckMateEvaluationTask.class);

        private CheckMateEvaluator evaluator;
        private KingPiece<?> king;

        CheckMateEvaluationTask(CheckMateEvaluator evaluator, KingPiece<?> king) {
            this.evaluator = evaluator;
            this.king = king;
        }

        @Override
        public Boolean call() throws Exception {
            try {
                return evaluator.evaluate(king);
            } catch (Exception e) {
                var message = String.format("%s evaluation failure",
                        evaluator.getClass().getSimpleName()
                );

                LOGGER.error(message, e);
            }

            return false;
        }
    }
}