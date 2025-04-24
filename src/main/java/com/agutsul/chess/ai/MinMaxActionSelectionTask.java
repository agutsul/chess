package com.agutsul.chess.ai;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.command.SimulateGameActionCommand;
import com.agutsul.chess.journal.Journal;

//https://en.wikipedia.org/wiki/Minimax
final class MinMaxActionSelectionTask
        extends AbstractActionIntegerValueSimulationTask {

    private static final Logger LOGGER = getLogger(MinMaxActionSelectionTask.class);

    private static final long serialVersionUID = 1L;

    private final int value;

    // root level task
    MinMaxActionSelectionTask(Board board, Journal<ActionMemento<?,?>> journal,
                              ForkJoinPool forkJoinPool, Color color, int limit) {

        this(board, journal, forkJoinPool, getActions(board, color), color, limit, 0);
    }

    // node level task
    MinMaxActionSelectionTask(Board board, Journal<ActionMemento<?,?>> journal,
                              ForkJoinPool forkJoinPool, List<Action<?>> actions,
                              Color color, int limit, int value) {

        super(LOGGER, board, journal, forkJoinPool, actions, color, limit);
        this.value = value;
    }

    @Override
    public SimulationResult<Action<?>,Integer> simulate(Action<?> action) {
        try (var command = new SimulateGameActionCommand<Integer>(board, journal, forkJoinPool, color, action)) {
            command.setSimulationEvaluator(new MinMaxGameEvaluator(limit + 1, value));
            command.execute();

            var simulationResult = (ActionSimulationResult<Integer>) command.getSimulationResult();
            if (isDone(simulationResult)) {
                return simulationResult;
            }

            var opponentColor = this.color.invert();

            var opponentActions = getActions(simulationResult.getBoard(), opponentColor);
            if (opponentActions.isEmpty()) {
                return simulationResult;
            }

            var opponentTask = createTask(simulationResult, opponentActions, opponentColor);
            opponentTask.fork();

            var opponentResult = opponentTask.join();

            simulationResult.setOpponentResult(opponentResult);
            simulationResult.setValue(opponentResult.getValue());

            return simulationResult;
        } catch (Exception e) {
            var message = String.format("Simulation for '%s' action '%s' failed",
                    this.color, action
            );

            logger.error(message, e);
        }

        return new ActionSimulationResult<>(board, journal, action, color, 0);
    }

    @Override
    protected MinMaxActionSelectionTask createTask(List<Action<?>> actions) {
        // root level task
        return new MinMaxActionSelectionTask(this.board, this.journal,
                this.forkJoinPool, actions, this.color, this.limit, this.value
        );
    }

    @Override
    protected MinMaxActionSelectionTask createTask(SimulationResult<Action<?>,Integer> simulationResult,
                                                   List<Action<?>> actions, Color color) {
        // node level task
        return new MinMaxActionSelectionTask(simulationResult.getBoard(),
                simulationResult.getJournal(), this.forkJoinPool, actions, color,
                this.limit - 1, simulationResult.getValue()
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