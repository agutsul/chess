package com.agutsul.chess.game;

import static com.agutsul.chess.timeout.TimeoutFactory.createMixedTimeout;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.event.GameExceptionEvent;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameTimeoutTerminationEvent;
import com.agutsul.chess.game.event.GameWinnerEvent;
import com.agutsul.chess.game.observer.GameTimeoutTerminationObserver;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.UserPlayer;
import com.agutsul.chess.rule.board.BoardStateEvaluatorImpl;
import com.agutsul.chess.timeout.Timeout;

@ExtendWith(MockitoExtension.class)
public class TimeoutGameTest {

    @ParameterizedTest(name = "{index}. testInvalidTimeoutValue({0})")
    @ValueSource(ints = { 0, -1 })
    void testInvalidTimeoutValue(int timeout) {
        var originGame = mock(AbstractPlayableGame.class);

        var game = new TimeoutGame<>(originGame, timeout);
        game.run();

        verify(originGame, times(1)).notifyObservers(any(GameTimeoutTerminationEvent.class));
        verify(originGame, times(1)).notifyObservers(any(GameOverEvent.class));
        verify(originGame, times(1)).notifyObservers(any(GameWinnerEvent.class));
    }

    @Test
    void testTimeoutExceeded() {
        var whitePlayer = new UserPlayer(String.valueOf(randomUUID()), Colors.WHITE);
        var blackPlayer = new UserPlayer(String.valueOf(randomUUID()), Colors.BLACK);

        var timeout = 100L;

        var game = new TimeoutGame<>(
                new LongRunningGameMock(whitePlayer, blackPlayer, new StandardBoard(), timeout),
                timeout / 2
        );

        game.addObserver(new GameTimeoutTerminationObserver());
        game.run();

        var winner = game.getWinnerPlayer();
        assertTrue(winner.isPresent());
        assertEquals(blackPlayer, winner.get());
    }

    @Test
    void testGameExecutionException() {
        var originGame = mock(AbstractPlayableGame.class);
        when(originGame.getCurrentPlayer())
            .thenReturn(mock(Player.class));
        when(originGame.getBoard())
            .thenReturn(mock(Board.class));

        doThrow(new RuntimeException("test"))
            .when(originGame).run();

        var game = new TimeoutGame<>(originGame, 100L);
        game.run();

        verify(originGame, times(1)).notifyObservers(any(GameExceptionEvent.class));
        verify(originGame, times(1)).notifyObservers(any(GameOverEvent.class));
    }

    @Test
    void testMixedTimeoutExceededWithEmptyJournal() {
        var whitePlayer = new UserPlayer(String.valueOf(randomUUID()), Colors.WHITE);
        var blackPlayer = new UserPlayer(String.valueOf(randomUUID()), Colors.BLACK);

        var timeout = createMixedTimeout(100L, 2);

        var context = new GameContext();
        context.setTimeout(timeout);

        var timeoutMillis = toTimeoutMillis(timeout);
        var game = new TimeoutGame<>(new LongRunningGameMock(whitePlayer, blackPlayer,
                    new StandardBoard(), new JournalImpl(), context, timeoutMillis
                ),
                timeoutMillis / 2
        );

        game.addObserver(new GameTimeoutTerminationObserver());
        game.run();

        var winner = game.getWinnerPlayer();
        assertTrue(winner.isPresent());
        assertEquals(blackPlayer, winner.get());
    }

    @Test
    void testMixedTimeoutExceededWithNonEmptyJournal() {
        var whitePlayer = new UserPlayer(String.valueOf(randomUUID()), Colors.WHITE);
        var blackPlayer = new UserPlayer(String.valueOf(randomUUID()), Colors.BLACK);

        var journal = spy(new JournalImpl());
        doReturn(false)
            .when(journal).isEmpty();
        doReturn(1)
            .when(journal).size();

        var timeout = createMixedTimeout(100L, 2);

        var context = new GameContext();
        context.setTimeout(timeout);

        var timeoutMillis = toTimeoutMillis(timeout);
        var game = new TimeoutGame<>(new LongRunningGameMock(whitePlayer, blackPlayer,
                    new StandardBoard(), journal, context, timeoutMillis
                ),
                timeoutMillis / 2
        );

        game.addObserver(new GameTimeoutTerminationObserver());
        game.run();

        var winner = game.getWinnerPlayer();
        assertTrue(winner.isPresent());
        assertEquals(blackPlayer, winner.get());
    }

    @Test
    void testMixedTimeoutExceededWithValidJournal() {
        var whitePlayer = new UserPlayer(String.valueOf(randomUUID()), Colors.WHITE);
        var blackPlayer = new UserPlayer(String.valueOf(randomUUID()), Colors.BLACK);

        var journal = spy(new JournalImpl());
        doReturn(false)
            .when(journal).isEmpty();
        doReturn(2)
            .when(journal).size();

        var timeout = createMixedTimeout(100L, 2);

        var context = new GameContext();
        context.setTimeout(timeout);

        var timeoutMillis = toTimeoutMillis(timeout);
        var game = new TimeoutGame<>(new LongRunningGameMock(whitePlayer, blackPlayer,
                    new StandardBoard(), journal, context, timeoutMillis
                ),
                timeoutMillis / 2
        );

        game.addObserver(new GameTimeoutTerminationObserver());
        game.run();

        var winner = game.getWinnerPlayer();
        assertTrue(winner.isEmpty());
    }

    private static Long toTimeoutMillis(Timeout timeout) {
        return Stream.of(timeout.getDuration())
                .flatMap(Optional::stream)
                .map(Duration::toMillis)
                .findFirst()
                .orElse(0L);
    }

    private static final class LongRunningGameMock
            extends GameMock {

        private final long duration;

        LongRunningGameMock(Player whitePlayer, Player blackPlayer,
                            Board board, long durationMillis) {

            super(whitePlayer, blackPlayer, board);
            this.duration = durationMillis;
        }

        LongRunningGameMock(Player whitePlayer, Player blackPlayer,
                            Board board, Journal<ActionMemento<?,?>> journal,
                            GameContext context, long durationMillis) {

            super(whitePlayer, blackPlayer, board, journal,
                    new BoardStateEvaluatorImpl(board, journal),
                    context
            );

            this.duration = durationMillis;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }
}