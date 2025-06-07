package com.agutsul.chess.ai;

import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static java.util.function.Predicate.not;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.GameInterruptionException;
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
        this(game.getBoard(), game.getJournal(), game.getContext().getForkJoinPool(), type);
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
        LOGGER.info("Select('{}') '{}' action", this.type, color);

        if (isNull(forkJoinPool)) {
            throw new IllegalStateException(String.format(
                    "Unable to select action for '%s': fork-join pool not set",
                    color
            ));
        }

        if (!isAnyAction(color)) {
            LOGGER.info("Select('{}') '{}' action: No action found", this.type, color);
            return Optional.empty();
        }

        var task = switch (this.type) {
            case ALPHA_BETA -> new AlphaBetaActionSelectionTask(board, journal, forkJoinPool, color);
            case MIN_MAX -> new MinMaxActionSelectionTask(board, journal, forkJoinPool, color);
        };

        var startTimepoint = now();
        try {
            var result = forkJoinPool.invoke(task);
            return Optional.ofNullable(result.getAction());
        } catch (CancellationException e) {
            throw new GameInterruptionException(String.format(
                    "Select('%s') '%s' action interrupted", this.type, color
            ));
        } finally {
            var duration = Duration.between(startTimepoint, now());
            LOGGER.info("Select('{}') '{}' action duration: {}ms",
                    this.type, color, duration.toMillis()
            );
        }
    }

    @Override
    public Optional<Action<?>> select(Color color, BoardState.Type boardState) {
        LOGGER.info("Select('{}') '{}' action", color, boardState);

        if (isNull(forkJoinPool)) {
            throw new IllegalStateException(String.format(
                    "Unable to select action for '%s' and board state '%s': fork-join pool not set",
                    color, boardState.name()
            ));
        }

        if (!isAnyAction(color)) {
            LOGGER.info("Select('{}') '{}' action: No action found", color, boardState);
            return Optional.empty();
        }

        var task = switch (this.type) {
            case ALPHA_BETA -> new AlphaBetaActionSelectionTask(board, journal, forkJoinPool, color, boardState);
            case MIN_MAX -> new MinMaxActionSelectionTask(board, journal, forkJoinPool, color, boardState);
        };

        var startTimepoint = now();
        try {
            var result = forkJoinPool.invoke(task);

            var resultMatcher = new PlayerBoardStateResultMatcher<>(color, boardState);
            if (resultMatcher.match(result)) {
                return Optional.ofNullable(result.getAction());
            }
        } catch (CancellationException e) {
            throw new GameInterruptionException(String.format(
                    "Select('%s') '%s' action interrupted", this.type, color
            ));
        } finally {
            var duration = Duration.between(startTimepoint, now());
            LOGGER.info("Select('{}') '{}' action duration: {}ms",
                    color, boardState, duration.toMillis()
            );
        }

        return Optional.empty();
    }

    private boolean isAnyAction(Color color) {
        return board.getPieces(color).stream()
                .map(piece -> board.getActions(piece))
                .anyMatch(not(Collection::isEmpty));
    }
}