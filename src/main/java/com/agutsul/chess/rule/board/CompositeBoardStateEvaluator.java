package com.agutsul.chess.rule.board;
import static com.agutsul.chess.board.state.BoardStateFactory.defaultBoardState;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.CompositeBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;

final class CompositeBoardStateEvaluator
        implements BoardStateEvaluator<BoardState> {

    private static final Logger LOGGER = getLogger(CompositeBoardStateEvaluator.class);

    private final Board board;
    private final List<BoardStateEvaluator<Optional<BoardState>>> evaluators;

    CompositeBoardStateEvaluator(Board board,
                                 Journal<ActionMemento<?,?>> journal) {
        this(board,
                new CheckedBoardStateEvaluator(board),
                new CheckMatedBoardStateEvaluator(board),
                new StaleMatedBoardStateEvaluator(board),
                new FoldRepetitionBoardStateEvaluator(board, journal),
                new MovesBoardStateEvaluator(board, journal),
                new InsufficientMaterialBoardStateEvaluator(board)
        );
    }

    CompositeBoardStateEvaluator(Board board,
                                 CheckedBoardStateEvaluator checkedEvaluator,
                                 CheckMatedBoardStateEvaluator checkMatedEvaluator,
                                 StaleMatedBoardStateEvaluator staleMatedEvaluator,
                                 FoldRepetitionBoardStateEvaluator foldRepetitionEvaluator,
                                 MovesBoardStateEvaluator movesBoardStateEvaluator,
                                 InsufficientMaterialBoardStateEvaluator insufficientMaterialBoardStateEvaluator) {
        this.board = board;
        this.evaluators = List.of(
                new BoardStatisticStateEvaluator(movesBoardStateEvaluator),
                new BoardStatisticStateEvaluator(foldRepetitionEvaluator),
                new CheckableBoardStateEvaluator(checkedEvaluator, checkMatedEvaluator),
                staleMatedEvaluator,
                insufficientMaterialBoardStateEvaluator
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
            return defaultBoardState(board, playerColor);
        }

        if (boardStates.size() == 1) {
            return boardStates.values().iterator().next();
        }

        if (boardStates.containsKey(BoardState.Type.CHECK_MATED)) {
            return boardStates.get(BoardState.Type.CHECK_MATED);
        }

        var terminalStates = boardStates.values().stream()
                .filter(BoardState::isTerminal)
                .toList();

        if (!terminalStates.isEmpty()) {
            return new CompositeBoardState(boardStates.values().stream()
                    //  terminal states first
                    .sorted(comparing(BoardState::isTerminal).reversed())
                    .toList()
            );
        }

        var states = new ArrayList<BoardState>();

        if (!boardStates.containsKey(BoardState.Type.CHECKED)) {
            states.add(defaultBoardState(board, playerColor));
        }

        states.addAll(boardStates.values().stream()
                .sorted(comparing(BoardState::getType))
                .toList()
        );

        return new CompositeBoardState(states);
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
            LOGGER.error("Board state evaluation interrupted", e);
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
}