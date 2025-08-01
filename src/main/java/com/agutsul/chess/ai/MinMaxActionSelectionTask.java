package com.agutsul.chess.ai;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.command.SimulateGameActionCommand;
import com.agutsul.chess.exception.GameInterruptionException;
import com.agutsul.chess.journal.Journal;

//https://en.wikipedia.org/wiki/Minimax
final class MinMaxActionSelectionTask
        extends AbstractActionIntegerValueSimulationTask {

    private static final Logger LOGGER = getLogger(MinMaxActionSelectionTask.class);

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_LIMIT = 2;

    private final int value;

    MinMaxActionSelectionTask(Board board, Journal<ActionMemento<?,?>> journal,
                              ForkJoinPool forkJoinPool, Color color) {

        // best matched action selection
        this(board, journal, forkJoinPool, color, DEFAULT_LIMIT, TERMINAL_BOARD_STATE_RESULT_MATCHER);
    }

    @SuppressWarnings("unchecked")
    MinMaxActionSelectionTask(Board board, Journal<ActionMemento<?,?>> journal,
                              ForkJoinPool forkJoinPool, Color color, BoardState.Type boardState) {

        // first matched action selection
        this(board, journal, forkJoinPool, color, DEFAULT_LIMIT,
                new CompositeResultMatcher<Action<?>,Integer,TaskResult<Action<?>,Integer>>(
                        new PlayerBoardStateResultMatcher<>(color, boardState),
                        TERMINAL_BOARD_STATE_RESULT_MATCHER
                )
        );
    }

    private MinMaxActionSelectionTask(Board board, Journal<ActionMemento<?,?>> journal,
                                      ForkJoinPool forkJoinPool, Color color, int limit,
                                      ResultMatcher<Action<?>,Integer,TaskResult<Action<?>,Integer>> resultMatcher) {

        this(board, journal, forkJoinPool,
                getActions(board, color), color, limit, resultMatcher, 0
        );
    }


    private MinMaxActionSelectionTask(Board board, Journal<ActionMemento<?,?>> journal,
                                      ForkJoinPool forkJoinPool, List<Action<?>> actions,
                                      Color color, int limit,
                                      ResultMatcher<Action<?>,Integer,TaskResult<Action<?>,Integer>> resultMatcher,
                                      int value) {

        super(LOGGER, board, journal, forkJoinPool, actions, color, limit, resultMatcher);
        this.value = value;
    }

    @Override
    public TaskResult<Action<?>,Integer> simulate(Action<?> action) {
        try (var command = new SimulateGameActionCommand<Integer>(board, journal, forkJoinPool, color, action)) {
            command.setSimulationEvaluator(new MinMaxGameEvaluator(limit + 1, value));
            command.execute();

            var simulationResult = createTaskResult(command.getGame(), action, command.getValue());
            if (isDone(simulationResult)) {
                return simulationResult;
            }

            var opponentColor = this.color.invert();

            var opponentActions = getActions(simulationResult.getBoard(), opponentColor);
            if (opponentActions.isEmpty()) {
                simulationResult.setOpponentResult(createTaskResult(simulationResult, opponentColor, 0));
                return simulationResult;
            }

            var opponentTask = createTask(simulationResult, opponentActions, opponentColor);
            opponentTask.fork();

            var opponentResult = opponentTask.join();

            simulationResult.setOpponentResult(opponentResult);
            simulationResult.setValue(opponentResult.getValue());

            return simulationResult;
        } catch (CancellationException e) {
            throw new GameInterruptionException(String.format(
                    "Simulation for '%s' action '%s' interrupted",
                    this.color, action
            ));
        } catch (IOException e) {
            logger.error(
                    String.format("Simulation for '%s' action '%s' failed", this.color, action),
                    e
            );
        }

        return createTaskResult(action, 0);
    }

    @Override
    protected MinMaxActionSelectionTask createTask(List<Action<?>> actions) {
        // root level task
        return new MinMaxActionSelectionTask(this.board, this.journal,
                this.forkJoinPool, actions, this.color, this.limit, this.resultMatcher, this.value
        );
    }

    @Override
    protected MinMaxActionSelectionTask createTask(TaskResult<Action<?>,Integer> result,
                                                   List<Action<?>> actions, Color color) {
        // node level task
        return new MinMaxActionSelectionTask(result.getBoard(), result.getJournal(),
                this.forkJoinPool, actions, color, this.limit - 1, this.resultMatcher, result.getValue()
        );
    }

    private static final class MinMaxGameEvaluator
            extends AbstractIntegerGameEvaluator {

        // previous board value
        private final int value;

        MinMaxGameEvaluator(int limit, int value) {
            super(limit);
            this.value = value;
        }

        @Override
        protected int calculateValue(Board board, Action<?> action, Color color) {
            return super.calculateValue(board, action, color) + this.value;
        }
    }
}