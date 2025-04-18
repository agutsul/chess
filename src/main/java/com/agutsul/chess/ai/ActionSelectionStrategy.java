package com.agutsul.chess.ai;

import static java.time.LocalDateTime.now;
import static java.util.function.Predicate.not;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.journal.Journal;

public final class ActionSelectionStrategy
        implements SelectionStrategy<Action<?>> {

    private static final Logger LOGGER = getLogger(ActionSelectionStrategy.class);

    private final Board board;
    private final Journal<ActionMemento<?,?>> journal;
    private final ForkJoinPool forkJoinPool;
    private final SelectionStrategy.Type type;

    public ActionSelectionStrategy(Game game, SelectionStrategy.Type type) {
        this(game.getBoard(), game.getJournal(), game.getForkJoinPool(), type);
    }

    public ActionSelectionStrategy(Board board, Journal<ActionMemento<?,?>> journal,
                                   ForkJoinPool forkJoinPool, SelectionStrategy.Type type) {

        this.board = board;
        this.journal = journal;
        this.forkJoinPool = forkJoinPool;
        this.type = type;
    }

    @Override
    public Optional<Action<?>> select(Color color) {
        LOGGER.info("Select('{}') '{}' action", type, color);

        if (!isAnyAction(color)) {
            LOGGER.info("Select('{}') '{}' action: No action found", type, color);
            return Optional.empty();
        }

        var task = switch (type) {
            case ALPHA_BETA -> new AlphaBetaActionSelectionTask(board, journal, forkJoinPool, color, 3);
            case MIN_MAX -> new MinMaxActionSelectionTask(board, journal, forkJoinPool, color, 2);
        };

        var startTimepoint = now();
        try {
            var result = forkJoinPool.invoke(task);
            return Optional.of(result.getAction());
        } catch (Exception e) {
            LOGGER.error(String.format("Select('%s') '%s' action failure", type, color), e);
        } finally {
            var duration = Duration.between(startTimepoint, now());
            LOGGER.info("Select('{}') '{}' action duration: {}ms",
                    type, color, duration.toMillis()
            );
        }

        return Optional.empty();
    }

    @Override
    public Optional<Action<?>> select(Color color, BoardState.Type boardState) {
        return Optional.empty();
    }

    private boolean isAnyAction(Color color) {
        return board.getPieces(color).stream()
                .map(piece -> board.getActions(piece))
                .anyMatch(not(Collection::isEmpty));
    }
}