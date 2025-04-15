package com.agutsul.chess.ai;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.command.SimulateGameCommand;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.journal.Journal;

// https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning
public final class AlphaBetaActionSelectionStrategy
        extends AbstractActionSelectionStrategy {

    private static final Logger LOGGER = getLogger(AlphaBetaActionSelectionStrategy.class);

    private static final int DEFAULT_DEPTH = 3;

    public AlphaBetaActionSelectionStrategy(Game game) {
        this(game, DEFAULT_DEPTH);
    }

    public AlphaBetaActionSelectionStrategy(Game game, int limit) {
        this(game.getBoard(), game.getJournal(), game.getForkJoinPool(), limit);
    }

    public AlphaBetaActionSelectionStrategy(Board board, Journal<ActionMemento<?,?>> journal,
                                            ForkJoinPool forkJoinPool, int limit) {

        super(LOGGER, board, journal, forkJoinPool, limit);
    }

    @Override
    protected Action<?> searchAction(Color color) {
        var task = new AlphaBetaActionSelectionTask(
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

    private static final class AlphaBetaActionSelectionTask
            extends AbstractActionSelectionTask<Pair<Action<?>,Integer>,Action<?>>
            implements SimulationTask<Action<?>,Integer> {

        private static final Logger LOGGER = getLogger(AlphaBetaActionSelectionTask.class);

        private static final long serialVersionUID = 1L;

        private final AlphaBetaContext context;

        // root level task
        AlphaBetaActionSelectionTask(Board board, Journal<ActionMemento<?,?>> journal,
                                     Color color, ForkJoinPool forkJoinPool, int limit) {

            this(board, journal, getActions(board, color),
                    color, forkJoinPool, limit, new AlphaBetaContext()
            );
        }

        // node level task
        AlphaBetaActionSelectionTask(Board board, Journal<ActionMemento<?,?>> journal,
                                     List<Action<?>> actions, Color color,
                                     ForkJoinPool forkJoinPool, int limit,
                                     AlphaBetaContext context) {

            super(LOGGER, board, journal, actions, color, forkJoinPool, limit);
            this.context = context;
        }

        @Override
        public Integer simulate(Action<?> action) {
            try (var command = new SimulateGameCommand(board, journal, forkJoinPool, color, action)) {
                command.setSimulationEvaluator(new AlphaBetaGameEvaluator(limit + 1));
                command.execute();

                var game = command.getGame();
                if (isDone(game)) {
                    return command.getValue();
                }

                var value = AlphaBetaFunction.of(this.color).apply(command.getValue(), this.context);
                if (value.isPresent()) {
                    return value.get();
                }

                var opponentColor = this.color.invert();

                var opponentActions = getActions(game.getBoard(), opponentColor);
                if (opponentActions.isEmpty()) {
                    return command.getValue();
                }

                var opponentTask = createTask(game, opponentActions, opponentColor);
                opponentTask.fork();

                var opponentResult = opponentTask.join();
                return command.getValue() + opponentResult.getValue();
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
        private AlphaBetaActionSelectionTask createTask(List<Action<?>> actions) {
            return new AlphaBetaActionSelectionTask(this.board, this.journal,
                    actions, this.color, this.forkJoinPool, this.limit, this.context
            );
        }

        // node level task
        private AlphaBetaActionSelectionTask createTask(Game game, List<Action<?>> actions,
                                                        Color color) {

            return new AlphaBetaActionSelectionTask(game.getBoard(), game.getJournal(),
                    actions, color, this.forkJoinPool, this.limit - 1, this.context
            );
        }

        private static final class AlphaBetaGameEvaluator
                extends AbstractSimulationGameEvaluator {

            AlphaBetaGameEvaluator(int limit) {
                super(limit);
            }
        }

        private enum AlphaBetaFunction
                implements BiFunction<Integer,AlphaBetaContext,Optional<Integer>> {

            WHITE_MODE(Colors.WHITE) {
                @Override
                public Optional<Integer> apply(Integer boardValue, AlphaBetaContext context) {
                    var value = Math.max(boardValue, context.getAlpha());
                    if (value >= context.getBeta()) {
                        return Optional.of(value);
                    }

                    context.setAlpha(Math.max(context.getAlpha(), value));
                    return Optional.empty();
                }
            },
            BLACK_MODE(Colors.BLACK) {
                @Override
                public Optional<Integer> apply(Integer boardValue, AlphaBetaContext context) {
                    var value = Math.min(boardValue, context.getBeta());
                    if (value <= context.getAlpha()) {
                        return Optional.of(value);
                    }

                    context.setBeta(Math.min(context.getBeta(), value));
                    return Optional.empty();
                }
            };

            private static final Map<Color,AlphaBetaFunction> MODES =
                    Stream.of(values()).collect(toMap(AlphaBetaFunction::color,identity()));

            private Color color;

            AlphaBetaFunction(Color color) {
                this.color = color;
            }

            private Color color() {
                return color;
            }

            public static AlphaBetaFunction of(Color color) {
                return MODES.get(color);
            }
        }

        private static final class AlphaBetaContext implements Serializable {

            private static final long serialVersionUID = 1L;

            private final AtomicInteger alpha;
            private final AtomicInteger beta;

            public AlphaBetaContext() {
                this(Integer.MIN_VALUE, Integer.MAX_VALUE);
            }

            private AlphaBetaContext(int alpha, int beta) {
                this.alpha = new AtomicInteger(alpha);
                this.beta = new AtomicInteger(beta);
            }

            public int getAlpha() {
                return this.alpha.get();
            }

            public int getBeta() {
                return this.beta.get();
            }

            public void setAlpha(int alpha) {
                this.alpha.set(alpha);
            }

            public void setBeta(int beta) {
                this.beta.set(beta);
            }

            @Override
            public String toString() {
                return String.format("[%d,%d]", getAlpha(), getBeta());
            }
        }
    }
}