package com.agutsul.chess.ai;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.command.SimulateGameCommand;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.journal.Journal;

//https://en.wikipedia.org/wiki/Minimax
final class MinMaxActionSelectionTask
        extends AbstractActionValueSimulationTask {

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
    public ActionSimulationResult simulate(Action<?> action) {
        try (var command = new SimulateGameCommand(board, journal, forkJoinPool, color, action)) {
            command.setSimulationEvaluator(new MinMaxGameEvaluator(limit + 1, value));
            command.execute();

            var game = command.getGame();

            var simulationResult = createSimulationResult(game, command.getValue());
            if (isDone(game)) {
                return simulationResult;
            }

            var opponentColor = this.color.invert();

            var opponentActions = getActions(game.getBoard(), opponentColor);
            if (opponentActions.isEmpty()) {
                return simulationResult;
            }

            var opponentTask = createTask(game, opponentActions, opponentColor, command.getValue());
            opponentTask.fork();

            simulationResult.setOpponentActionResult(opponentTask.join());
            return simulationResult;
        } catch (Exception e) {
            var message = String.format("Simulation for '%s' action '%s' failed",
                    this.color,
                    action
            );

            logger.error(message, e);
        }

        return new ActionSimulationResult(board, journal, action, color, 0);
    }

    @Override
    protected AbstractActionValueSimulationTask createTask(List<Action<?>> actions) {
        // root level task
        return new MinMaxActionSelectionTask(this.board, this.journal,
                this.forkJoinPool, actions, this.color, this.limit, this.value
        );
    }

    private AbstractActionValueSimulationTask createTask(Game game, List<Action<?>> actions,
                                                         Color color, int value) {
        // node level task
        return new MinMaxActionSelectionTask(game.getBoard(), game.getJournal(),
                this.forkJoinPool, actions, color, this.limit - 1, value
        );
    }

    private static final class MinMaxGameEvaluator
            extends AbstractSimulationGameEvaluator {

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