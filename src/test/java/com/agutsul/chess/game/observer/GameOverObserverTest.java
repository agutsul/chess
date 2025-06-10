package com.agutsul.chess.game.observer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.GameContext;
import com.agutsul.chess.game.GameMock;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.player.UserPlayer;
import com.agutsul.chess.rule.board.BoardStateEvaluator;

@ExtendWith(MockitoExtension.class)
public class GameOverObserverTest {

    @Test
    @SuppressWarnings("unchecked")
    void testGameOverEvent() throws IOException {
        var whitePlayer = new UserPlayer(UUID.randomUUID().toString(), Colors.WHITE);
        var blackPlayer = new UserPlayer(UUID.randomUUID().toString(), Colors.BLACK);

        var board = spy(new StandardBoard());
        doCallRealMethod()
            .when(board).notifyObservers(any());

        var forkJoinPool = spy(new ForkJoinPool());
        doCallRealMethod()
            .when(forkJoinPool).shutdown();
        doCallRealMethod()
            .when(forkJoinPool).isShutdown();
        doCallRealMethod()
            .when(forkJoinPool).isTerminated();

        var context = spy(new GameContext(forkJoinPool));
        doCallRealMethod()
            .when(context).getForkJoinPool();
        doCallRealMethod()
            .when(context).close();

        var game = new GameMock(whitePlayer, blackPlayer,
                board, mock(Journal.class), mock(BoardStateEvaluator.class), context
        );
        assertNull(game.getFinishedAt());

        var observer = new GameOverObserver();
        observer.observe(new GameOverEvent(game));

        assertNotNull(game.getFinishedAt());

        assertTrue(forkJoinPool.isShutdown());
        assertTrue(forkJoinPool.isTerminated());

        verify(board, times(1)).notifyObservers(any(GameOverEvent.class));
        verify(context, times(1)).close();
        verify(forkJoinPool, times(1)).shutdown();
    }
}