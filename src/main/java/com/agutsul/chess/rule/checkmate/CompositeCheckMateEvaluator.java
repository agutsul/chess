package com.agutsul.chess.rule.checkmate;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
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
            LOGGER.error("Checkmate evaluation interrupted", e);
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
            tasks.add(() -> evaluator.evaluate(king));
        }

        return tasks;
    }
}