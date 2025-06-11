package com.agutsul.chess.game.observer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.GameMock;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.player.UserPlayer;

@ExtendWith(MockitoExtension.class)
public class GameOverObserverTest {

    @Test
    void testGameOverEvent() {
        var whitePlayer = new UserPlayer(UUID.randomUUID().toString(), Colors.WHITE);
        var blackPlayer = new UserPlayer(UUID.randomUUID().toString(), Colors.BLACK);

        var board = spy(new StandardBoard());
        doCallRealMethod()
            .when(board).notifyObservers(any());

        var game = new GameMock(whitePlayer, blackPlayer, board);
        assertNull(game.getFinishedAt());

        var observer = new GameOverObserver();
        observer.observe(new GameOverEvent(game));

        assertNotNull(game.getFinishedAt());

        verify(board, times(1)).notifyObservers(any(GameOverEvent.class));
    }
}