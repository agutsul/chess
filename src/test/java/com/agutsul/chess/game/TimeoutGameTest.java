package com.agutsul.chess.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.event.GameExceptionEvent;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameTimeoutTerminationEvent;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.UserPlayer;
import com.agutsul.chess.rule.winner.GameTimeoutWinnerEvaluator;

@ExtendWith(MockitoExtension.class)
public class TimeoutGameTest {

    @ParameterizedTest(name = "{index}. testInvalidTimeoutValue({0})")
    @ValueSource(ints = { 0, -1 })
    void testInvalidTimeoutValue(int timeout) {
        var originGame = mock(AbstractPlayableGame.class);

        var game = new TimeoutGame(originGame, timeout);
        game.run();

        verify(originGame, times(1)).notifyObservers(any(GameTimeoutTerminationEvent.class));
        verify(originGame, times(1)).notifyObservers(any(GameOverEvent.class));
        verify(originGame, times(1)).evaluateWinner(any(GameTimeoutWinnerEvaluator.class));
    }

    @Test
    void testTimeoutExceeded() {
        var whitePlayer = new UserPlayer(UUID.randomUUID().toString(), Colors.WHITE);
        var blackPlayer = new UserPlayer(UUID.randomUUID().toString(), Colors.BLACK);

        var timeout = 100L;
        var game = new TimeoutGame(
                new LongRunningGameMock(whitePlayer, blackPlayer, new StandardBoard(), timeout),
                timeout / 2
        );

        game.run();

        var winner = game.getWinner();
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

        var game = new TimeoutGame(originGame, 100L);
        game.run();

        verify(originGame, times(1)).notifyObservers(any(GameExceptionEvent.class));
        verify(originGame, times(1)).notifyObservers(any(GameOverEvent.class));
    }

    private static final class LongRunningGameMock
            extends GameMock {

        private final long duration;

        LongRunningGameMock(Player whitePlayer, Player blackPlayer,
                            Board board, long durationMillis) {

            super(whitePlayer, blackPlayer, board);
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