package com.agutsul.chess.ai;

import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.ai.SimulationGame;
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
        this(game.getBoard(), game.getJournal(), limit);
    }

    public AlphaBetaActionSelectionStrategy(Board board, Journal<ActionMemento<?,?>> journal,
                                            int limit) {

        super(LOGGER, board, journal, limit);
    }

    @Override
    protected AbstractActionSelectionTask createActionSelectionTask(Color color) {
        return new AlphaBetaActionSelectionTask(this.board, this.journal, color, this.limit);
    }

    private static final class AlphaBetaActionSelectionTask
            extends AbstractActionSelectionTask {

        private static final Logger LOGGER = getLogger(AlphaBetaActionSelectionTask.class);

        private static final long serialVersionUID = 1L;

        private final AlphaBetaContext context;

        // root level task
        AlphaBetaActionSelectionTask(Board board, Journal<ActionMemento<?,?>> journal,
                                     Color color, int limit) {

            this(board, journal, getActions(board, color), color, limit, new AlphaBetaContext());
        }

        // node level task
        AlphaBetaActionSelectionTask(Board board, Journal<ActionMemento<?,?>> journal,
                                     List<Action<?>> actions, Color color,
                                     int limit, AlphaBetaContext context) {

            super(LOGGER, board, journal, actions, color, limit);
            this.context = context;
        }

        @Override
        public Integer simulate(Action<?> action) {
            try (var game = new SimulationGame(this.color, this.board, this.journal, action)) {

                game.run();

                var boardValue = calculateValue(game.getBoard(), action);
                if (isDone(game)) {
                    return boardValue;
                }

                var value = AlphaBetaFunction.of(this.color).apply(boardValue, this.context);
                if (value.isPresent()) {
                    return value.get();
                }

                var opponentColor = this.color.invert();

                var opponentActions = getActions(game.getBoard(), opponentColor);
                if (!opponentActions.isEmpty()) {
                    var opponentTask = createOpponentTask(game, opponentActions, opponentColor);

                    opponentTask.fork();

                    var opponentResult = opponentTask.join();
                    boardValue = opponentResult.getValue();
                }

                return boardValue;
            } catch (IOException e) {
                var message = String.format("Closing '%s' game simulation for action '%s' failed",
                        this.color,
                        action
                );

                logger.error(message, e);
            }

            return 0;
        }

//        @Override
//        protected Pair<Action<?>,Integer> process(List<Pair<Action<?>,Integer>> actionValues) {
//            var pair = super.process(actionValues);
//
//            var value = AlphaBetaFunction.of(this.color).apply(pair.getValue(), this.context);
//            return value.isPresent()
//                ? Pair.of(pair.getKey(), value.get())
//                : pair;
//        }

        // root level task
        @Override
        protected AbstractActionSelectionTask createTask(List<Action<?>> actions) {
            return new AlphaBetaActionSelectionTask(this.board, this.journal,
                    actions, this.color, this.limit, this.context
            );
        }

        // node level task
        protected AbstractActionSelectionTask createOpponentTask(Game game, List<Action<?>> actions,
                                                                 Color color) {

            return new AlphaBetaActionSelectionTask(game.getBoard(), game.getJournal(),
                    actions, color, this.limit - 1, this.context
            );
        }

        private int calculateValue(Board board, Action<?> action) {
            var sourcePiece = action.getPiece();
            var direction = sourcePiece.getDirection();

            var currentPlayerValue = board.calculateValue(this.color) * direction;
            var opponentPlayerValue = board.calculateValue(this.color.invert()) * Math.negateExact(direction);

            var boardValue = action.getValue() // action type influence
                    + ((this.limit + 1) * direction) // depth influence
                    + currentPlayerValue + opponentPlayerValue; // current board value

            var boardState = board.getState();
            return boardState.isType(CHECK_MATED)
                    ? 1000 * boardValue * direction
                    : boardState.getType().rank() * boardValue;
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
        }
    }
}