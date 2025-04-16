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
        extends AbstractActionSimulationTask {

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
    public Integer simulate(Action<?> action) {
        try (var command = new SimulateGameCommand(board, journal, forkJoinPool, color, action)) {
            command.setSimulationEvaluator(new MinMaxGameEvaluator(limit + 1, value));
            command.execute();

            var game = command.getGame();
            if (isDone(game)) {
                return command.getValue();
            }

            var opponentColor = this.color.invert();

            var opponentActions = getActions(game.getBoard(), opponentColor);
            if (opponentActions.isEmpty()) {
                return command.getValue();
            }

            var opponentTask = createTask(game, opponentActions, opponentColor, command.getValue());
            opponentTask.fork();

            var opponentResult = opponentTask.join();
            return opponentResult.getValue();
        } catch (Exception e) {
            var message = String.format("Simulation for '%s' action '%s' failed",
                    this.color,
                    action
            );

            logger.error(message, e);
        }

        return 0;
    }

    @Override
    protected AbstractActionSimulationTask createTask(List<Action<?>> actions) {
        // root level task
        return new MinMaxActionSelectionTask(this.board, this.journal,
                this.forkJoinPool, actions, this.color, this.limit, this.value
        );
    }

    private AbstractActionSimulationTask createTask(Game game, List<Action<?>> actions,
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