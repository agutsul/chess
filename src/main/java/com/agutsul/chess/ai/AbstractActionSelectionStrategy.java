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

    AbstractActionSelectionStrategy(Logger logger, Board board,
                                    Journal<ActionMemento<?,?>> journal, int limit) {
        this.logger = logger;
        this.board = board;
        this.journal = journal;
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
        try (var executor = new ForkJoinPool(10)) {
            var result = executor.invoke(createActionSelectionTask(color));

            // return action
            return Optional.of(result.getKey());
        } finally {
            var duration = Duration.between(startTimepoint, now());
            logger.info("Select '{}' action duration: {}ms", color, duration.toMillis());
        }
    }

    protected abstract AbstractActionSelectionTask createActionSelectionTask(Color color);
}