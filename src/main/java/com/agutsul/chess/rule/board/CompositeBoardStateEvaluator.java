package com.agutsul.chess.rule.board;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.GameInterruptionException;

final class CompositeBoardStateEvaluator
        implements BoardStateEvaluator<List<BoardState>> {

    private static final Logger LOGGER = getLogger(CompositeBoardStateEvaluator.class);

    private final Board board;
    private final List<BoardStateEvaluator<Optional<BoardState>>> evaluators;

    @SuppressWarnings("unchecked")
    CompositeBoardStateEvaluator(Board board,
                                 BoardStateEvaluator<Optional<BoardState>> evaluator,
                                 BoardStateEvaluator<Optional<BoardState>>... evaluators) {

        this(board, compose(evaluator, evaluators));
    }

    private CompositeBoardStateEvaluator(Board board,
                                         List<BoardStateEvaluator<Optional<BoardState>>> evaluators) {

        this.board = board;
        this.evaluators = evaluators;
    }

    @Override
    public List<BoardState> evaluate(Color playerColor) {
        var boardStates = evaluate(evaluators, playerColor);
        return Stream.of(boardStates)
                .flatMap(Collection::stream)
                .flatMap(Optional::stream)
                .toList();
    }

    private List<Optional<BoardState>> evaluate(List<BoardStateEvaluator<Optional<BoardState>>> evaluators,
                                                Color color) {

        var tasks = createEvaluationTasks(evaluators, color);
        try {
            var results = new ArrayList<Optional<BoardState>>();

            var executor = board.getExecutorService();
            for (var future : executor.invokeAll(tasks)) {
                results.add(future.get());
            }

            return results;
        } catch (InterruptedException e) {
            throw new GameInterruptionException("Board state evaluation interrupted");
        } catch (ExecutionException e) {
            LOGGER.error("Board state evaluation failed", e);
        }

        return emptyList();
    }

    private static List<Callable<Optional<BoardState>>> createEvaluationTasks(List<BoardStateEvaluator<Optional<BoardState>>> evaluators,
                                                                              Color color) {
        var tasks = new ArrayList<Callable<Optional<BoardState>>>();
        for (var evaluator : evaluators) {
            tasks.add(new BoardStateEvaluationTask(evaluator, color));
        }

        return tasks;
    }

    @SuppressWarnings("unchecked")
    private static List<BoardStateEvaluator<Optional<BoardState>>> compose(BoardStateEvaluator<Optional<BoardState>> evaluator,
                                                                           BoardStateEvaluator<Optional<BoardState>>... evaluators) {

        var list = new ArrayList<BoardStateEvaluator<Optional<BoardState>>>();

        list.add(evaluator);
        list.addAll(List.of(evaluators));

        return unmodifiableList(list);
    }
}