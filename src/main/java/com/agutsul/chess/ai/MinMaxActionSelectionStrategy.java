package com.agutsul.chess.ai;

import static java.time.LocalDateTime.now;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.ListUtils.partition;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.Serializable;
import java.time.Duration;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.SimulationGame;
import com.agutsul.chess.journal.Journal;

// https://en.wikipedia.org/wiki/Minimax
public final class MinMaxActionSelectionStrategy
        implements ActionSelectionStrategy {

    private static final Logger LOGGER = getLogger(MinMaxActionSelectionStrategy.class);

    private final Game game;
    private final int limit;

    public MinMaxActionSelectionStrategy(Game game, int limit) {
        this.game  = game;
        this.limit = limit;
    }

    @Override
    public Optional<Action<?>> select(Color color) {
        LOGGER.info("Select '{}' action", color);

        var board = this.game.getBoard();
        var isAnyAction = board.getPieces(color).stream()
                .map(piece -> board.getActions(piece))
                .anyMatch(actions -> !actions.isEmpty());

        if (!isAnyAction) {
            LOGGER.info("Select '{}' action: No action found", color);
            return Optional.empty();
        }

        var startTimepoint = now();
        try (var executor = new ForkJoinPool(10)) {
            var result = executor.invoke(new ActionSelectionTask(
                    board, this.game.getJournal(), color, this.limit
            ));

            // return action
            return Optional.of(result.getKey());
        } finally {
            var duration = Duration.between(startTimepoint, now());
            LOGGER.info("Select '{}' action duration: {}ms", color, duration.toMillis());
        }
    }

    private static final class ActionSelectionTask
            extends RecursiveTask<Pair<Action<?>,Integer>> {

        private static final Logger LOGGER = getLogger(ActionSelectionTask.class);

        private static final long serialVersionUID = 1L;

        private static final PromoteActionAdapter PROMOTE_ADAPTER = new PromoteActionAdapter();
        private static final Comparator<Pair<Action<?>,Integer>> ACTION_VALUE_COMPARATOR =
                new ActionValueComparator();

        private final Board board;
        private final Journal<ActionMemento<?,?>> journal;
        private final List<Action<?>> actions;
        private final Color color;
        private final int limit;
        private final int value;

        ActionSelectionTask(Board board, Journal<ActionMemento<?,?>> journal, Color color, int limit) {
            this(board, journal, getActions(board, color), color, limit, 0);
        }

        private ActionSelectionTask(Board board, Journal<ActionMemento<?,?>> journal,
                                    List<Action<?>> actions, Color color, int limit, int value) {

            this.board = board;
            this.journal = journal;
            this.actions = actions;
            this.color = color;
            this.limit = limit;
            this.value = value;
        }

        @Override
        protected Pair<Action<?>,Integer> compute() {
            if (this.actions.size() == 1) {
                var action = this.actions.get(0);
                return Pair.of(action, simulate(action));
            }

            var subTasks = partition(this.actions, this.actions.size() / 2).stream()
                    .map(partitionActions -> new ActionSelectionTask(
                            this.board, this.journal,
                            partitionActions, this.color,
                            this.limit, this.value
                    ))
                    .toList();

            for (var subTask : subTasks) {
                subTask.fork();
            }

            var actionValues = new TreeSet<Pair<Action<?>,Integer>>(ACTION_VALUE_COMPARATOR);
            for (var subTask : subTasks) {
                actionValues.add(subTask.join());
            }

            var selectionFunction = ActionSelectionFunction.of(this.color);
            return selectionFunction.apply(actionValues);
        }

        private Integer simulate(Action<?> action) {
            try (var game = new SimulationGame(this.color, this.board, this.journal, action)) {

                game.run();

                var gameBoard = game.getBoard();
                var boardValue = calculateValue(gameBoard, action);

                if (this.limit > 0 && !gameBoard.getState().isTerminal()) {
                    var opponentColor = this.color.invert();

                    var opponentActions = getActions(gameBoard, opponentColor);
                    if (!opponentActions.isEmpty()) {
                        var opponentTask = new ActionSelectionTask(
                                gameBoard, game.getJournal(),
                                opponentActions, opponentColor,
                                this.limit - 1, boardValue
                        );

                        opponentTask.fork();

                        var opponentResult = opponentTask.join();
                        boardValue = opponentResult.getValue();
                    }
                }

                return boardValue;
            } catch (IOException e) {
                var message = String.format("Closing '%s' game simulation for action '%s' failed",
                        this.color,
                        action
                );

                LOGGER.error(message, e);
            }

            return 0;
        }

        private int calculateValue(Board board, Action<?> action) {
            var sourcePiece = action.getPiece();
            var boardValue = action.getValue()                         // action type influence
                    + ((this.limit + 1)  * sourcePiece.getDirection()) // depth influence
                    + board.calculateValue(this.color)                 // current board pieces
                    + this.value;                                      // previous board pieces

            var boardState = board.getState();
            return boardState.getType().rank() * boardValue;
        }

        private static List<Action<?>> getActions(Board board, Color color) {
            List<Action<?>> actions = board.getPieces(color).stream()
                    .map(piece -> board.getActions(piece))
                    .flatMap(Collection::stream)
                    .map(action -> {
                        if (!Action.Type.PROMOTE.equals(action.getType())) {
                            return List.of(action);
                        }

                        // replace origin promote action with pre-generated ones
                        // containing promoted piece type because action selection
                        // should be evaluated with all possible piece types:
                        // BISHOP, ROOK, KNIGHT, QUEEN
                        return PROMOTE_ADAPTER.adapt((PiecePromoteAction<?,?>) action);
                    })
                    .flatMap(Collection::stream)
                    .collect(toList());

            return actions;
        }
    }

    enum ActionSelectionFunction
            implements Function<TreeSet<Pair<Action<?>,Integer>>,Pair<Action<?>,Integer>> {

        WHITE_MODE(Colors.WHITE, actionValues -> actionValues.getLast()),   // max
        BLACK_MODE(Colors.BLACK, actionValues -> actionValues.getFirst());  // min

        private static final Map<Color,ActionSelectionFunction> MODES =
                Stream.of(values()).collect(toMap(ActionSelectionFunction::color, identity()));

        private Color color;
        private Function<TreeSet<Pair<Action<?>,Integer>>,Pair<Action<?>,Integer>> function;

        ActionSelectionFunction(Color color,
                                Function<TreeSet<Pair<Action<?>,Integer>>,Pair<Action<?>,Integer>> function) {

            this.color = color;
            this.function = function;
        }

        @Override
        public Pair<Action<?>,Integer> apply(TreeSet<Pair<Action<?>,Integer>> actionValues) {
            return function.apply(actionValues);
        }

        private Color color() {
            return color;
        }

        public static ActionSelectionFunction of(Color color) {
            return MODES.get(color);
        }
    }

    static final class ActionValueComparator
            implements Comparator<Pair<Action<?>,Integer>>, Serializable {

        private static final long serialVersionUID = 1L;

        @Override
        public int compare(Pair<Action<?>,Integer> pair1, Pair<Action<?>,Integer> pair2) {
            int compared = Integer.compare(pair1.getValue(), pair2.getValue());
            if (compared != 0) {
                return compared;
            }

            // compare actions
            return ObjectUtils.compare(pair2.getKey(), pair1.getKey());
        }
    }
}