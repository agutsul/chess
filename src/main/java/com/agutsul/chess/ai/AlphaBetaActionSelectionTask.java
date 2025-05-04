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
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.command.SimulateGameActionCommand;
import com.agutsul.chess.journal.Journal;

//https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning
final class AlphaBetaActionSelectionTask
        extends AbstractActionIntegerValueSimulationTask {

    private static final Logger LOGGER = getLogger(AlphaBetaActionSelectionTask.class);

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_LIMIT = 3;

    private final AlphaBetaContext context;

    AlphaBetaActionSelectionTask(Board board, Journal<ActionMemento<?,?>> journal,
                                 ForkJoinPool forkJoinPool, Color color) {

        // best matched action selection
        this(board, journal, forkJoinPool, color, DEFAULT_LIMIT, TERMINAL_BOARD_STATE_RESULT_MATCHER);
    }

    @SuppressWarnings("unchecked")
    AlphaBetaActionSelectionTask(Board board, Journal<ActionMemento<?,?>> journal,
                                 ForkJoinPool forkJoinPool, Color color, BoardState.Type boardState) {

        // first matched action selection
        this(board, journal, forkJoinPool, color, DEFAULT_LIMIT,
                new CompositeResultMatcher<Action<?>,Integer,TaskResult<Action<?>,Integer>>(
                        new PlayerBoardStateResultMatcher<>(color, boardState),
                        TERMINAL_BOARD_STATE_RESULT_MATCHER
                )
        );
    }

    private AlphaBetaActionSelectionTask(Board board, Journal<ActionMemento<?,?>> journal,
                                         ForkJoinPool forkJoinPool, Color color, int limit,
                                         ResultMatcher<Action<?>,Integer,TaskResult<Action<?>,Integer>> resultMatcher) {

        this(board, journal, forkJoinPool,
                getActions(board, color), color, limit, resultMatcher, new AlphaBetaContext()
        );
    }

    private AlphaBetaActionSelectionTask(Board board, Journal<ActionMemento<?,?>> journal,
                                         ForkJoinPool forkJoinPool, List<Action<?>> actions,
                                         Color color, int limit,
                                         ResultMatcher<Action<?>,Integer,TaskResult<Action<?>,Integer>> resultMatcher,
                                         AlphaBetaContext context) {

        super(LOGGER, board, journal, forkJoinPool, actions, color, limit, resultMatcher);
        this.context = context;
    }

    @Override
    public TaskResult<Action<?>,Integer> simulate(Action<?> action) {
        try (var command = new SimulateGameActionCommand<Integer>(board, journal, forkJoinPool, color, action)) {
            command.setSimulationEvaluator(new AlphaBetaGameEvaluator(limit + 1));
            command.execute();

            var simulationResult = createTaskResult(command.getGame(), action, command.getValue());
            if (isDone(simulationResult)) {
                return simulationResult;
            }

            var selectionFunction = AlphaBetaFunction.of(this.color);

            var selectedValue = selectionFunction.apply(simulationResult.getValue(), this.context);
            if (selectedValue.isPresent()) {
                simulationResult.setValue(selectedValue.get());
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
            simulationResult.setValue(simulationResult.getValue() + opponentResult.getValue());

            return simulationResult;
        } catch (Exception e) {
            var message = String.format("Simulation for '%s' action '%s' failed",
                    this.color, action
            );

            logger.error(message, e);
        }

        return createTaskResult(action, 0);
    }

    @Override
    protected AlphaBetaActionSelectionTask createTask(List<Action<?>> actions) {
        // root level task
        return new AlphaBetaActionSelectionTask(this.board, this.journal,
                this.forkJoinPool, actions, this.color, this.limit, this.resultMatcher, this.context
        );
    }

    @Override
    protected AlphaBetaActionSelectionTask createTask(TaskResult<Action<?>,Integer> simulationResult,
                                                      List<Action<?>> actions, Color color) {
        // node level task
        return new AlphaBetaActionSelectionTask(simulationResult.getBoard(), simulationResult.getJournal(),
                this.forkJoinPool, actions, color, this.limit - 1, this.resultMatcher, this.context
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
            extends AbstractIntegerGameEvaluator {

        public AlphaBetaGameEvaluator(int limit) {
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
            this.beta  = new AtomicInteger(beta);
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