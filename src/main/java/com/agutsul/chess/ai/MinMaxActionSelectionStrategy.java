package com.agutsul.chess.ai;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.command.SimulateGameCommand;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.journal.Journal;

// https://en.wikipedia.org/wiki/Minimax
public final class MinMaxActionSelectionStrategy
        extends AbstractActionSelectionStrategy {

    private static final Logger LOGGER = getLogger(MinMaxActionSelectionStrategy.class);

    private static final int DEFAULT_DEPTH = 2;

    public MinMaxActionSelectionStrategy(Game game) {
        this(game, DEFAULT_DEPTH);
    }

    public MinMaxActionSelectionStrategy(Game game, int limit) {
        this(game.getBoard(), game.getJournal(), game.getForkJoinPool(), limit);
    }

    public MinMaxActionSelectionStrategy(Board board, Journal<ActionMemento<?,?>> journal,
                                         ForkJoinPool forkJoinPool, int limit) {
        super(LOGGER, board, journal, forkJoinPool, limit);
    }

    @Override
    protected Action<?> searchAction(Color color) {
        var task = new MinMaxActionSelectionTask(
                this.board, this.journal, color, this.forkJoinPool, this.limit
        );

        try {
            var result = forkJoinPool.invoke(task);
            return result.getKey();
        } catch (Exception e) {
            logger.error("Exception while action selection", e);
        }

        return null;
    }

    private static final class MinMaxActionSelectionTask
            extends AbstractActionSelectionTask<Pair<Action<?>,Integer>,Action<?>>
            implements SimulationTask<Action<?>,Integer> {

        private static final Logger LOGGER = getLogger(MinMaxActionSelectionTask.class);

        private static final long serialVersionUID = 1L;

        private final int value;

        // root level task
        MinMaxActionSelectionTask(Board board, Journal<ActionMemento<?,?>> journal,
                                  Color color, ForkJoinPool forkJoinPool, int limit) {

            this(board, journal, getActions(board, color), color, forkJoinPool, limit, 0);
        }

        // node level task
        MinMaxActionSelectionTask(Board board, Journal<ActionMemento<?,?>> journal,
                                  List<Action<?>> actions, Color color,
                                  ForkJoinPool forkJoinPool, int limit, int value) {

            super(LOGGER, board, journal, actions, color, forkJoinPool, limit);
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
        public Pair<Action<?>,Integer> process(List<List<Action<?>>> buckets) {
            var subTasks = buckets.stream()
                    .map(bucketActions -> createTask(bucketActions))
                    .map(ForkJoinTask::fork)
                    .toList();

            var actionValues = new ArrayList<Pair<Action<?>,Integer>>();
            for (var subTask : subTasks) {
                actionValues.add(subTask.join());
            }

            return ActionSelectionFunction.of(this.color).apply(actionValues);
        }

        @Override
        protected Pair<Action<?>,Integer> compute(Action<?> action) {
            return Pair.of(action, simulate(action));
        }

        // root level task
        private MinMaxActionSelectionTask createTask(List<Action<?>> actions) {
            return new MinMaxActionSelectionTask(this.board, this.journal,
                    actions, this.color, this.forkJoinPool, this.limit, this.value
            );
        }

        // node level task
        private MinMaxActionSelectionTask createTask(Game game, List<Action<?>> actions,
                                                     Color color, int value) {

            return new MinMaxActionSelectionTask(game.getBoard(), game.getJournal(),
                    actions, color, this.forkJoinPool, this.limit - 1, value
            );
        }

        private static final class MinMaxGameEvaluator
                extends AbstractSimulationGameEvaluator {

            private final int value;

            MinMaxGameEvaluator(int limit, int value) {
                super(limit);
                this.value = value;
            }

            @Override
            protected int calculateValue(Board board, Action<?> action, Color color) {
                var value = super.calculateValue(board, action, color)
                        + this.value; // previous board value

                return value;
            }
        }
    }
}