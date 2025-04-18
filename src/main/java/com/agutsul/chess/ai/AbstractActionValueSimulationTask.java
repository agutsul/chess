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
import com.agutsul.chess.game.ai.SimulationGame;
import com.agutsul.chess.journal.Journal;

abstract class AbstractActionValueSimulationTask
        extends AbstractActionSelectionTask<Action<?>,ActionSimulationResult>
        implements SimulationTask<Action<?>,ActionSimulationResult> {

    private static final long serialVersionUID = 1L;

    AbstractActionValueSimulationTask(Logger logger, Board board,
                                      Journal<ActionMemento<?,?>> journal,
                                      ForkJoinPool forkJoinPool,
                                      List<Action<?>> actions,
                                      Color color, int limit) {

        super(logger, board, journal, forkJoinPool, actions, color, limit);
    }

    @Override
    public final ActionSimulationResult process(List<List<Action<?>>> buckets) {
        var subTasks = buckets.stream()
                .map(bucketActions -> createTask(bucketActions))
                .map(ForkJoinTask::fork)
                .toList();

        var actionValues = new ArrayList<ActionSimulationResult>();
        for (var subTask : subTasks) {
            actionValues.add(subTask.join());
        }

        return ActionSelectionFunction.of(this.color).apply(actionValues);
    }

    @Override
    protected final ActionSimulationResult compute(Action<?> action) {
        return simulate(action);
    }

    protected abstract AbstractActionValueSimulationTask createTask(List<Action<?>> actions);

    protected static ActionSimulationResult createSimulationResult(SimulationGame game, int value) {
        return new ActionSimulationResult(game.getBoard(), game.getJournal(),
                game.getAction(), game.getColor(), value
        );
    }
}