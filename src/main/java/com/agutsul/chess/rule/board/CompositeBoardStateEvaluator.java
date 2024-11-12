package com.agutsul.chess.rule.board;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.slf4j.Logger;

import com.agutsul.chess.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.DefaultBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;

final class CompositeBoardStateEvaluator
        implements BoardStateEvaluator<BoardState> {

    private static final Logger LOGGER = getLogger(CompositeBoardStateEvaluator.class);

    private final Board board;
    private final List<Function<Color,Optional<BoardState>>> evaluators;

    CompositeBoardStateEvaluator(Board board, Journal<ActionMemento<?,?>> journal) {
        this(board,
                new CheckedBoardStateEvaluator(board),
                new CheckMatedBoardStateEvaluator(board),
                new StaleMatedBoardStateEvaluator(board),
                new FoldRepetitionBoardStateEvaluator(board, journal),
                new MovesBoardStateEvaluator(board, journal)
        );
    }

    CompositeBoardStateEvaluator(Board board,
                                 CheckedBoardStateEvaluator checkedEvaluator,
                                 CheckMatedBoardStateEvaluator checkMatedEvaluator,
                                 StaleMatedBoardStateEvaluator staleMatedEvaluator,
                                 FoldRepetitionBoardStateEvaluator foldRepetitionEvaluator,
                                 MovesBoardStateEvaluator movesBoardStateEvaluator) {
        this.board = board;
        this.evaluators = List.of(
                color -> evaluate(color, movesBoardStateEvaluator),
                color -> evaluate(color, foldRepetitionEvaluator),
                color -> evaluate(color, checkedEvaluator, checkMatedEvaluator),
                color -> evaluate(color, staleMatedEvaluator)
        );
    }

    @Override
    public BoardState evaluate(Color playerColor) {
        var results = evaluate(evaluators, playerColor);

        var boardState = results.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(comparing(bs -> bs.getType().priority()))
                .findFirst();

        if (boardState.isPresent()) {
            return boardState.get();
        }

        return new DefaultBoardState(board, playerColor);
    }

    private static List<Optional<BoardState>> evaluate(List<Function<Color,Optional<BoardState>>> evaluators,
                                                       Color color) {
        var executor = newFixedThreadPool(evaluators.size());
        try {
            var tasks = createEvaluationTasks(evaluators, color);
            try {
                var results = new ArrayList<Optional<BoardState>>();
                for (var future : executor.invokeAll(tasks)) {
                    results.add(future.get());
                }

                return results;
            } catch (InterruptedException e) {
                LOGGER.error("Board state evaluation interrupted", e);
            } catch (ExecutionException e) {
                LOGGER.error("Board state evaluation failed", e);
            }
        } finally {
            try {
                executor.shutdown();
                if (!executor.awaitTermination(1, TimeUnit.MICROSECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }

        return emptyList();
    }

    private static List<Callable<Optional<BoardState>>> createEvaluationTasks(List<Function<Color,Optional<BoardState>>> evaluators,
                                                                              Color color) {
        var tasks = new ArrayList<Callable<Optional<BoardState>>>();
        for (var evaluator : evaluators) {
            tasks.add(new Callable<Optional<BoardState>>() {
                @Override
                public Optional<BoardState> call() throws Exception {
                    return evaluator.apply(color);
                }
            });
        }

        return tasks;
    }

    private static Optional<BoardState> evaluate(Color color,
            CheckedBoardStateEvaluator checkedEvaluator,
            CheckMatedBoardStateEvaluator checkMatedEvaluator) {

        var checked = checkedEvaluator.evaluate(color);
        if (checked.isPresent()) {
            var checkMated = checkMatedEvaluator.evaluate(color);
            return checkMated.isPresent() ? checkMated : checked;
        }

        return Optional.empty();
    }

    private static Optional<BoardState> evaluate(Color color,
            BoardStateEvaluator<Optional<BoardState>> evaluator) {

        return evaluator.evaluate(color);
    }
}