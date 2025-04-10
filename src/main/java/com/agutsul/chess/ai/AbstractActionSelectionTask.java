package com.agutsul.chess.ai;

import static com.agutsul.chess.activity.action.Action.isPromote;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.ListUtils.partition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.journal.Journal;

abstract class AbstractActionSelectionTask
        extends RecursiveTask<Pair<Action<?>,Integer>>
        implements SimulationTask {

    private static final long serialVersionUID = 1L;

    private static final PromoteActionAdapter PROMOTE_ADAPTER = new PromoteActionAdapter();

    protected final Logger logger;
    protected final Board board;
    protected final Journal<ActionMemento<?,?>> journal;
    protected final List<Action<?>> actions;
    protected final Color color;
    protected final ForkJoinPool forkJoinPool;
    protected final int limit;

    AbstractActionSelectionTask(Logger logger, Board board, Journal<ActionMemento<?,?>> journal,
                                List<Action<?>> actions, Color color, ForkJoinPool forkJoinPool,
                                int limit) {

        this.logger = logger;
        this.board = board;
        this.journal = journal;
        this.actions = actions;
        this.color = color;
        this.forkJoinPool = forkJoinPool;
        this.limit = limit;
    }

    @Override
    protected final Pair<Action<?>,Integer> compute() {
        if (this.actions.size() == 1) {
            var action = this.actions.getFirst();
            return Pair.of(action, simulate(action));
        }

        var subTasks = partition(this.actions, this.actions.size() / 2).stream()
                .map(partitionedActions -> createTask(partitionedActions))
                .toList();

        for (var subTask : subTasks) {
            subTask.fork();
        }

        var actionValues = new ArrayList<Pair<Action<?>,Integer>>();
        for (var subTask : subTasks) {
            actionValues.add(subTask.join());
        }

        return process(actionValues);
    }

    protected abstract AbstractActionSelectionTask createTask(List<Action<?>> actions);

    protected Pair<Action<?>,Integer> process(List<Pair<Action<?>,Integer>> actionValues) {
        return ActionSelectionFunction.of(this.color).apply(actionValues);
    }

    protected boolean isDone(Game game) {
        if (this.limit == 0) {
            return true;
        }

        var gameBoard = game.getBoard();
        var boardState = gameBoard.getState();

        return boardState.isTerminal();
    }

    protected static List<Action<?>> getActions(Board board, Color color) {
        List<Action<?>> actions = board.getPieces(color).stream()
                .map(piece -> board.getActions(piece))
                .flatMap(Collection::stream)
                .map(action -> isPromote(action)
                        // replace origin promote action with pre-generated ones
                        // containing promoted piece type because action selection
                        // should be evaluated with all possible piece types:
                        // BISHOP, ROOK, KNIGHT, QUEEN
                        ? PROMOTE_ADAPTER.adapt((PiecePromoteAction<?, ?>) action)
                        : List.of(action))
                .flatMap(Collection::stream)
                .collect(toList());

        return actions;
    }
}