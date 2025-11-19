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

    private final CheckMateEvaluator kingMoveEvaluator;         // escape from check
    private final CheckMateEvaluator kingCaptureEvaluator;      // attack any piece to escape
    private final CheckMateEvaluator attackerCaptureEvaluator;  // attack checkmaker by any non-king piece
    private final CheckMateEvaluator attackerPinEvaluator;      // pin attack line

    public CompositeCheckMateEvaluator(Board board) {
        this.board = board;

        this.kingMoveEvaluator = new KingMoveCheckMateEvaluator(board);
        this.kingCaptureEvaluator = new KingCaptureCheckMateEvaluator(board);
        this.attackerCaptureEvaluator = new AttackerCaptureCheckMateEvaluator(board);
        this.attackerPinEvaluator = new AttackerPinCheckMateEvaluator(board);
    }

    @Override
    public Boolean evaluate(KingPiece<?> king) {
        var results = new ArrayList<Boolean>();

        var tasks = createEvaluationTasks(king);
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

        var isCheckMated = results.size() == tasks.size() && !results.contains(true);
        LOGGER.info("Checkmate evaluation is done: checkMated='{}'", isCheckMated);

        return isCheckMated;
    }

    private List<Callable<Boolean>> createEvaluationTasks(KingPiece<?> king) {
        var checkMakers = board.getAttackers(king);

        var evaluators = checkMakers.size() == 1
                ? List.of(kingMoveEvaluator, kingCaptureEvaluator, attackerCaptureEvaluator, attackerPinEvaluator)
                : List.of(kingMoveEvaluator, kingCaptureEvaluator);

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