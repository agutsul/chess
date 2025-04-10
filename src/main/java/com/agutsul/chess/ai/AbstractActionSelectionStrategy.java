package com.agutsul.chess.ai;

import static java.time.LocalDateTime.now;
import static java.util.function.Predicate.not;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;

abstract class AbstractActionSelectionStrategy
        implements ActionSelectionStrategy {

    protected final Logger logger;
    protected final Board board;
    protected final Journal<ActionMemento<?,?>> journal;
    protected final int limit;

    protected final ForkJoinPool forkJoinPool;

    AbstractActionSelectionStrategy(Logger logger, Board board,
                                    Journal<ActionMemento<?,?>> journal,
                                    ForkJoinPool forkJoinPool, int limit) {
        this.logger = logger;
        this.board = board;
        this.journal = journal;
        this.forkJoinPool = forkJoinPool;
        this.limit = limit;
    }

    @Override
    public final Optional<Action<?>> select(Color color) {
        logger.info("Select '{}' action", color);

        var isAnyAction = board.getPieces(color).stream()
                .map(piece -> board.getActions(piece))
                .anyMatch(not(Collection::isEmpty));

        if (!isAnyAction) {
            logger.info("Select '{}' action: No action found", color);
            return Optional.empty();
        }

        var startTimepoint = now();
        try {
            return Optional.ofNullable(searchAction(color));
        } finally {
            var duration = Duration.between(startTimepoint, now());
            logger.info("Select '{}' action duration: {}ms", color, duration.toMillis());
        }
    }

    protected abstract AbstractActionSelectionTask createActionSelectionTask(Color color);

    private Action<?> searchAction(Color color) {
        var task = createActionSelectionTask(color);
        try {
            var result = forkJoinPool.invoke(task);
            return result.getKey();
        } catch (Exception e) {
            logger.error("Exception while action selection", e);
        }
        return null;
    }
}