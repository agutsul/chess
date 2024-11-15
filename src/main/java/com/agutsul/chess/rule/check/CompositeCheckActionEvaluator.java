package com.agutsul.chess.rule.check;

import static java.util.Collections.emptyList;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

public final class CompositeCheckActionEvaluator
        implements CheckActionEvaluator {

    private static final Logger LOGGER = getLogger(CompositeCheckActionEvaluator.class);

    private final List<CheckActionEvaluator> evaluators;

    public CompositeCheckActionEvaluator(Board board,
                                         Piece<?> piece,
                                         Collection<Action<?>> actions) {
        this.evaluators = List.of(
                new AttackerCaptureCheckActionEvaluator(board, actions),
                Piece.Type.KING.equals(piece.getType())
                    ? new KingMoveCheckActionEvaluator(board, actions)
                    : new AttackerPinCheckActionEvaluator(board, actions)
        );
    }

    @Override
    public Collection<Action<?>> evaluate(KingPiece<?> king) {
        var executor = newFixedThreadPool(this.evaluators.size());
        try {
            var tasks = createEvaluationTasks(king);
            try {
                var results = new ArrayList<Action<?>>();
                for (var future : executor.invokeAll(tasks)) {
                    results.addAll(future.get());
                }

                return results;
            } catch (InterruptedException e) {
                LOGGER.error("Check action evaluation interrupted", e);
            } catch (ExecutionException e) {
                LOGGER.error("Check action evaluation failed", e);
            }
        } finally {
            try {
                executor.shutdown();
                if (!executor.awaitTermination(1, MICROSECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }

        return emptyList();
    }

    private List<Callable<Collection<Action<?>>>> createEvaluationTasks(KingPiece<?> king) {
        var tasks = new ArrayList<Callable<Collection<Action<?>>>>();
        for (var evaluator : this.evaluators) {
            tasks.add(() -> evaluator.evaluate(king));
        }

        return tasks;
    }
}