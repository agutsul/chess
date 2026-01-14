package com.agutsul.chess.journal.statistic;

import static java.util.Collections.max;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.GameInterruptionException;
import com.agutsul.chess.journal.Journal;

public final class JournalActionCalculation {

    private static final Logger LOGGER = getLogger(JournalActionCalculation.class);

    private final Board board;
    private final Journal<ActionMemento<?,?>> journal;

    public JournalActionCalculation(Board board, Journal<ActionMemento<?,?>> journal) {
        this.board = board;
        this.journal = journal;
    }

    public int calculate(Color color, int limit) {
        var actions = journal.get(color);
        try {
            return calculate(List.of(
                    new CaptureCalculationTask(actions,  limit),
                    new PawnMoveCalculationTask(actions, limit)
            ));
        } catch (InterruptedException e) {
            throw new GameInterruptionException("Journal action calculation interrupted");
        } catch (ExecutionException e) {
            LOGGER.error("Journal action calculation failed", e);
        }

        return -1;
    }

    private Integer calculate(List<Callable<Integer>> tasks)
            throws InterruptedException, ExecutionException {

        var executor = board.getExecutorService();

        var results = new ArrayList<Integer>();
        for (var future : executor.invokeAll(tasks)) {
            results.add(future.get());
        }

        return max(results);
    }
}