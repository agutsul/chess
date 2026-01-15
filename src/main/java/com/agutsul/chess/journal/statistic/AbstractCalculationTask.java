package com.agutsul.chess.journal.statistic;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.memento.ActionMemento;

abstract class AbstractCalculationTask implements Callable<Integer> {

    private final Logger logger;
    private final List<ActionMemento<?,?>> actions;
    private final int limit;

    AbstractCalculationTask(Logger logger, List<ActionMemento<?,?>> actions, int limit) {
        this.logger = logger;
        this.actions = actions;
        this.limit = limit;
    }

    @Override
    public Integer call() throws Exception {
        try {
            int counter = 0;
            for (int i = actions.size() - 1, j = 0; i >= 0 && j < limit; i--, j++) {
                counter += count(actions.get(i));
            }

            return counter;
        } catch (Exception e) {
            logger.error("Journal action calculation failed", e);
        }

        return -1;
    }

    abstract int count(ActionMemento<?,?> memento);
}