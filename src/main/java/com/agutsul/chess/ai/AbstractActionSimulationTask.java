package com.agutsul.chess.ai;

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
import com.agutsul.chess.journal.Journal;

abstract class AbstractActionSimulationTask
        extends AbstractActionSelectionTask<Pair<Action<?>,Integer>,Action<?>>
        implements SimulationTask<Action<?>,Integer> {

    private static final long serialVersionUID = 1L;

    AbstractActionSimulationTask(Logger logger, Board board,
                                 Journal<ActionMemento<?,?>> journal,
                                 List<Action<?>> actions, Color color,
                                 ForkJoinPool forkJoinPool, int limit) {

        super(logger, board, journal, actions, color, forkJoinPool, limit);
    }

    @Override
    public final Pair<Action<?>,Integer> process(List<List<Action<?>>> buckets) {
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
    protected final Pair<Action<?>,Integer> compute(Action<?> action) {
        return Pair.of(action, simulate(action));
    }

    protected abstract AbstractActionSimulationTask createTask(List<Action<?>> actions);
}