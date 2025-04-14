package com.agutsul.chess.ai;

import static com.agutsul.chess.activity.action.Action.isPromote;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.ListUtils.partition;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.journal.Journal;

abstract class AbstractActionSelectionTask<RESULT,ACTION extends Action<?>>
        extends RecursiveTask<RESULT> {

    private static final long serialVersionUID = 1L;

    private static final PromoteActionAdapter PROMOTE_ADAPTER = new PromoteActionAdapter();

    protected final Logger logger;
    protected final Board board;
    protected final Journal<ActionMemento<?,?>> journal;
    protected final List<ACTION> actions;
    protected final Color color;
    protected final ForkJoinPool forkJoinPool;
    protected final int limit;

    AbstractActionSelectionTask(Logger logger, Board board, Journal<ActionMemento<?,?>> journal,
                                List<ACTION> actions, Color color, ForkJoinPool forkJoinPool,
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
    public final String toString() {
        return String.format("[%s]",
                this.actions.stream().map(Action::toString).collect(joining(","))
        );
    }

    @Override
    protected final RESULT compute() {
        if (this.actions.size() == 1) {
            // simulate action
            return compute(this.actions.getFirst());
        }

        // split actions
        var actions = partition(this.actions, this.actions.size() / 2);
        return process(actions);
    }

    protected abstract RESULT compute(ACTION action);

    protected abstract RESULT process(List<List<ACTION>> actions);

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
                        : List.of(action)
                )
                .flatMap(Collection::stream)
                .collect(toList());

        return actions;
    }
}