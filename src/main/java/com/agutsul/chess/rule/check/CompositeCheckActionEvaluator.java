package com.agutsul.chess.rule.check;

import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;

final class CompositeCheckActionEvaluator
        implements CheckActionEvaluator {

    private static final Logger LOGGER = getLogger(CompositeCheckActionEvaluator.class);

    private final Board board;
    private final List<CheckActionEvaluator> evaluators;

    CompositeCheckActionEvaluator(Board board,
                                  List<CheckActionEvaluator> evaluators) {
        this.board = board;
        this.evaluators = evaluators;
    }

    @Override
    public Collection<Action<?>> evaluate(KingPiece<?> king) {
        var tasks = createEvaluationTasks(king);
        try {
            var results = new LinkedHashSet<Action<?>>();

            var executor = board.getExecutorService();
            for (var future : executor.invokeAll(tasks)) {
                results.addAll(future.get());
            }

            return results;
        } catch (InterruptedException e) {
            LOGGER.error("Check action evaluation interrupted", e);
        } catch (ExecutionException e) {
            LOGGER.error("Check action evaluation failed", e);
        }

        return emptyList();
    }

    private List<Callable<Collection<Action<?>>>> createEvaluationTasks(KingPiece<?> king) {
        var tasks = new ArrayList<Callable<Collection<Action<?>>>>();
        for (var evaluator : this.evaluators) {
            tasks.add(new CheckEvaluationTask(evaluator, king));
        }

        return tasks;
    }

    private static final class CheckEvaluationTask
            implements Callable<Collection<Action<?>>> {

        private static final Logger LOGGER = getLogger(CheckEvaluationTask.class);

        private CheckActionEvaluator evaluator;
        private KingPiece<?> king;

        CheckEvaluationTask(CheckActionEvaluator evaluator, KingPiece<?> king) {
            this.evaluator = evaluator;
            this.king = king;
        }

        @Override
        public Collection<Action<?>> call() throws Exception {
            try {
                return evaluator.evaluate(king);
            } catch (Exception e) {
                var message = String.format("%s evaluation failure",
                        evaluator.getClass().getSimpleName()
                );

                LOGGER.error(message, e);
            }

            return emptyList();
        }
    }
}