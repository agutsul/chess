package com.agutsul.chess.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;

abstract class AbstractActionValueSimulationTask<VALUE extends Comparable<VALUE>>
        extends AbstractActionSelectionTask<Action<?>,VALUE,ActionSimulationResult<VALUE>>
        implements SimulationTask<Action<?>,VALUE,ActionSimulationResult<VALUE>> {

    private static final long serialVersionUID = 1L;

    AbstractActionValueSimulationTask(Logger logger, Board board,
                                      Journal<ActionMemento<?,?>> journal,
                                      ForkJoinPool forkJoinPool, List<Action<?>> actions,
                                      Color color, int limit) {

        super(logger, board, journal, forkJoinPool, actions, color, limit);
    }

    @Override
    public final ActionSimulationResult<VALUE> process(List<List<Action<?>>> buckets) {
        var subTasks = buckets.stream()
                .map(actions -> createTask(actions))
                .map(ForkJoinTask::fork)
                .toList();

        var actionValues = new ArrayList<ActionSimulationResult<VALUE>>();
        for (var subTask : subTasks) {
            actionValues.add(subTask.join());
        }

        return select(actionValues);
    }

    @Override
    protected final ActionSimulationResult<VALUE> compute(Action<?> action) {
        return simulate(action);
    }

    protected abstract AbstractActionValueSimulationTask<VALUE> createTask(List<Action<?>> actions);

    protected abstract AbstractActionValueSimulationTask<VALUE> createTask(SimulationResult<Action<?>,VALUE> simulationResult,
                                                                           List<Action<?>> actions, Color color);


    protected abstract ActionSimulationResult<VALUE> select(List<ActionSimulationResult<VALUE>> list);
}