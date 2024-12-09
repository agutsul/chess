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

import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

public final class CompositeCheckActionEvaluator
        implements CheckActionEvaluator {

    private static final Logger LOGGER = getLogger(CompositeCheckActionEvaluator.class);

    private final Board board;
    private final List<CheckActionEvaluator> evaluators;

    public CompositeCheckActionEvaluator(Board board,
                                         KingPiece<?> unusedPiece,
                                         Collection<Action<?>> actions) {
        this(board, List.of(
                new AttackerCaptureCheckActionEvaluator(board, actions),
                new KingMoveCheckActionEvaluator(board, actions),
                new KingCapturePieceActionEvaluator(board, actions)
        ));
    }

    public CompositeCheckActionEvaluator(Board board,
                                         Piece<?> unusedPiece,
                                         Collection<Action<?>> actions) {
        this(board, List.of(
                new AttackerCaptureCheckActionEvaluator(board, actions),
                new AttackerPinCheckActionEvaluator(board, actions)
        ));
    }

    private CompositeCheckActionEvaluator(Board board,
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
            tasks.add(() -> evaluator.evaluate(king));
        }

        return tasks;
    }
}