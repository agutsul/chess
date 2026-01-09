package com.agutsul.chess.game.observer;

import static com.agutsul.chess.player.PlayerFactory.playerOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.GameMock;
import com.agutsul.chess.game.event.GameOverEvent;

@ExtendWith(MockitoExtension.class)
public class GameOverObserverTest {

    @Spy
    StandardBoard board = new StandardBoard();

    @Test
    void testGameOverEvent() {
        var whitePlayer = playerOf(Colors.WHITE);
        var blackPlayer = playerOf(Colors.BLACK);

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