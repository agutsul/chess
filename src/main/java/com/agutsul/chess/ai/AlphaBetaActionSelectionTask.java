package com.agutsul.chess.ai;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.command.SimulateGameCommand;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.journal.Journal;

//https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning
final class AlphaBetaActionSelectionTask
        extends AbstractActionValueSimulationTask {

    private static final Logger LOGGER = getLogger(AlphaBetaActionSelectionTask.class);

    private static final long serialVersionUID = 1L;

    private final AlphaBetaContext context;

    // root level task
    AlphaBetaActionSelectionTask(Board board, Journal<ActionMemento<?,?>> journal,
                                 ForkJoinPool forkJoinPool, Color color, int limit) {

        this(board, journal, forkJoinPool,
                getActions(board, color), color, limit, new AlphaBetaContext()
        );
    }

    // node level task
    AlphaBetaActionSelectionTask(Board board, Journal<ActionMemento<?,?>> journal,
                                 ForkJoinPool forkJoinPool, List<Action<?>> actions,
                                 Color color, int limit, AlphaBetaContext context) {

        super(LOGGER, board, journal, forkJoinPool, actions, color, limit);
        this.context = context;
    }

    @Override
    public ActionSimulationResult simulate(Action<?> action) {
        try (var command = new SimulateGameCommand(board, journal, forkJoinPool, color, action)) {
            command.setSimulationEvaluator(new AlphaBetaGameEvaluator(limit + 1));
            command.execute();

            var game = command.getGame();
            if (isDone(game)) {
                return createSimulationResult(game, command.getValue());
            }

            var value = AlphaBetaFunction.of(this.color).apply(command.getValue(), this.context);
            if (value.isPresent()) {
                return createSimulationResult(game, value.get());
            }

            var simulationResult = createSimulationResult(game, command.getValue());
            var opponentColor = this.color.invert();

            var opponentActions = getActions(game.getBoard(), opponentColor);
            if (opponentActions.isEmpty()) {
                return simulationResult;
            }

            var opponentTask = createTask(game, opponentActions, opponentColor);
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
        return new AlphaBetaActionSelectionTask(this.board, this.journal,
                this.forkJoinPool, actions, this.color, this.limit, this.context
        );
    }

    private AbstractActionValueSimulationTask createTask(Game game, List<Action<?>> actions, Color color) {
        // node level task
        return new AlphaBetaActionSelectionTask(game.getBoard(), game.getJournal(),
                this.forkJoinPool, actions, color, this.limit - 1, this.context
        );
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

    private static final class AlphaBetaGameEvaluator
            extends AbstractSimulationGameEvaluator {

        AlphaBetaGameEvaluator(int limit) {
            super(limit);
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