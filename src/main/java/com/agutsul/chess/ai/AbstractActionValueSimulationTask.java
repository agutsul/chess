package com.agutsul.chess.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.journal.Journal;

abstract class AbstractActionValueSimulationTask<VALUE extends Comparable<VALUE>>
        extends AbstractActionSelectionTask<Action<?>,VALUE,TaskResult<Action<?>,VALUE>>
        implements SimulationTask<Action<?>,VALUE,TaskResult<Action<?>,VALUE>> {

    private static final long serialVersionUID = 1L;

    AbstractActionValueSimulationTask(Logger logger, Board board, Journal<ActionMemento<?,?>> journal,
                                      ForkJoinPool forkJoinPool, List<Action<?>> actions,
                                      Color color, int limit,
                                      ResultMatcher<Action<?>,VALUE,TaskResult<Action<?>,VALUE>> resultMatcher) {

        super(logger, board, journal, forkJoinPool, actions, color, limit, resultMatcher);
    }

    @Override
    public final TaskResult<Action<?>,VALUE> process(List<List<Action<?>>> buckets) {
        var subTasks = Stream.of(buckets)
                .flatMap(Collection::stream)
                .map(this::createTask)
                .map(ForkJoinTask::fork)
                .toList();

        var actionValues = new ArrayList<TaskResult<Action<?>,VALUE>>();
        for (var subTask : subTasks) {
            actionValues.add(subTask.join());
        }

        return select(actionValues);
    }

    @Override
    protected final TaskResult<Action<?>,VALUE> compute(Action<?> action) {
        return simulate(action);
    }

    protected abstract TaskResult<Action<?>,VALUE> createTaskResult(Action<?> action, VALUE value);

    protected abstract TaskResult<Action<?>,VALUE> createTaskResult(Game game, Action<?> action, VALUE value);

    protected abstract TaskResult<Action<?>,VALUE> createTaskResult(TaskResult<Action<?>,VALUE> result,
                                                                    Color color, VALUE value);

    protected abstract AbstractActionValueSimulationTask<VALUE> createTask(List<Action<?>> actions);

    protected abstract AbstractActionValueSimulationTask<VALUE> createTask(TaskResult<Action<?>,VALUE> simulationResult,
                                                                           List<Action<?>> actions, Color color);


    protected abstract TaskResult<Action<?>,VALUE> select(List<TaskResult<Action<?>,VALUE>> list);
}