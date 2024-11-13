package com.agutsul.chess.rule.board;

import static java.util.Collections.emptyList;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
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
import com.agutsul.chess.board.state.CheckedBoardState;
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

        var boardStates = results.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toMap(BoardState::getType, identity()));

        if (boardStates.isEmpty()) {
            return new DefaultBoardState(board, playerColor);
        }

        if (boardStates.size() == 1) {
            return boardStates.values().iterator().next();
        }

        if (boardStates.containsKey(BoardState.Type.CHECK_MATED)) {
            return boardStates.get(BoardState.Type.CHECK_MATED);
        }

        var boardState = boardStates.get(BoardState.Type.CHECKED);
        var terminalStates = boardStates.values().stream()
                .filter(BoardState::isTerminal)
                .toList();

        if (terminalStates.isEmpty()) {
            return defaultIfNull(boardState, new DefaultBoardState(board, playerColor));
        }

        if (boardState == null) {
            return terminalStates.get(0);
        }

        var checkedState = (CheckedBoardState) boardState;
        checkedState.setTerminal(true);

        return checkedState;
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
            tasks.add(() -> evaluator.apply(color));
        }

        return tasks;
    }

    private static Optional<BoardState> evaluate(Color color,
            CheckedBoardStateEvaluator checkedEvaluator,
            CheckMatedBoardStateEvaluator checkMatedEvaluator) {

        var checked = checkedEvaluator.evaluate(color);
        if (checked.isEmpty()) {
            return Optional.empty();
        }

        var checkMated = checkMatedEvaluator.evaluate(color);
        return checkMated.isPresent() ? checkMated : checked;
    }

    private static Optional<BoardState> evaluate(Color color,
            BoardStateEvaluator<Optional<BoardState>> evaluator) {

        return evaluator.evaluate(color);
    }
}