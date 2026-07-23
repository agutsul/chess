package com.agutsul.chess.ai;

import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.collections4.ListUtils.partition;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.adapter.Adapter;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.PieceComparator;

abstract class AbstractActionSelectionTask<ACTION extends Action<?>,
                                           VALUE  extends Number & Comparable<VALUE>,
                                           RESULT extends TaskResult<ACTION,VALUE>>
        extends RecursiveTask<RESULT>
        implements ActionSelectionTask<ACTION,VALUE,RESULT> {

    private static final long serialVersionUID = 1L;

    private static final Adapter<Action<?>,Collection<Action<?>>> ADAPTER = new ActionAdapter();
    private static final Comparator<Piece<?>> COMPARATOR = new PieceComparator();

    protected final Logger logger;

    protected final Board board;
    protected final Journal<ActionMemento<?,?>> journal;
    protected final List<ACTION> actions;
    protected final Color color;
    protected final ForkJoinPool forkJoinPool;
    protected final int limit;
    protected final ResultMatcher<ACTION,VALUE,RESULT> resultMatcher;

    AbstractActionSelectionTask(Logger logger, Board board, Journal<ActionMemento<?,?>> journal,
                                ForkJoinPool forkJoinPool, List<ACTION> actions, Color color,
                                int limit, ResultMatcher<ACTION,VALUE,RESULT> resultMatcher) {

        this.logger = logger;
        this.board = board;
        this.journal = journal;
        this.actions = actions;
        this.color = color;
        this.forkJoinPool = forkJoinPool;
        this.limit = limit;
        this.resultMatcher = resultMatcher;
    }

    @Override
    public final String toString() {
        return format("[%s]", Stream.of(this.actions)
                .flatMap(Collection::stream)
                .map(String::valueOf)
                .collect(joining(",")
        ));
    }

    @Override
    protected final RESULT compute() {
        return this.actions.size() == 1
            ? compute(this.actions.getFirst())                           // simulate action
            : process(partition(this.actions, this.actions.size() / 2)); // split actions
    }

    protected abstract RESULT compute(ACTION action);

    protected boolean isDone(RESULT result) {
        return this.limit == 0 || resultMatcher.match(result);
    }

    protected static List<Action<?>> getActions(Board board, Color color) {
        var actions = Stream.of(board.getPieces(color))
                .flatMap(Collection::parallelStream)
                .map(piece -> board.getActions(piece))
                .flatMap(Collection::parallelStream)
                .map(ADAPTER::adapt)
                .flatMap(Collection::parallelStream)
                .sorted(comparing(Action::getPiece, COMPARATOR.reversed()))
                .distinct()
                .toList();

        return actions;
    }
}