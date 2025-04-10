package com.agutsul.chess.ai;

import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.ai.SimulationGame;
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
    protected AbstractActionSelectionTask createActionSelectionTask(Color color) {
        return new MinMaxActionSelectionTask(this.board, this.journal, color, this.forkJoinPool, this.limit);
    }

    private static final class MinMaxActionSelectionTask
            extends AbstractActionSelectionTask {

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
            try (var game = new SimulationGame(color, board, journal, forkJoinPool, action)) {

                game.run();

                var boardValue = calculateValue(game.getBoard(), action);
                if (isDone(game)) {
                    return boardValue;
                }

                var opponentColor = this.color.invert();

                var opponentActions = getActions(game.getBoard(), opponentColor);
                if (!opponentActions.isEmpty()) {
                    var opponentTask = createOpponentTask(game, opponentActions, opponentColor, boardValue);

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

        // root level task
        @Override
        protected AbstractActionSelectionTask createTask(List<Action<?>> actions) {
            return new MinMaxActionSelectionTask(this.board, this.journal,
                    actions, this.color, this.forkJoinPool, this.limit, this.value
            );
        }

        // node level task
        protected AbstractActionSelectionTask createOpponentTask(Game game, List<Action<?>> actions,
                                                                 Color color, int value) {

            return new MinMaxActionSelectionTask(game.getBoard(), game.getJournal(),
                    actions, color, this.forkJoinPool, this.limit - 1, value
            );
        }

        private int calculateValue(Board board, Action<?> action) {
            var sourcePiece = action.getPiece();
            var direction = sourcePiece.getDirection();

            var currentPlayerValue = board.calculateValue(this.color) * direction;
            var opponentPlayerValue = board.calculateValue(this.color.invert()) * Math.negateExact(direction);

            var boardValue = action.getValue() // action type influence
                    + ((this.limit + 1) * direction) // depth influence
                    + currentPlayerValue + opponentPlayerValue // current board value
                    + this.value; // previous board value

            var boardState = board.getState();
            return boardState.isType(CHECK_MATED)
                    ? 1000 * boardValue * direction
                    : boardState.getType().rank() * boardValue;
        }
    }
}