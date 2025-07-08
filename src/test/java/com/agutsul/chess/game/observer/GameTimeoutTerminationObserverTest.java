package com.agutsul.chess.game.observer;

import static com.agutsul.chess.timeout.TimeoutFactory.createGameTimeout;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.GameContext;
import com.agutsul.chess.game.PlayableGameBuilder;
import com.agutsul.chess.game.event.GameTimeoutTerminationEvent;
import com.agutsul.chess.player.UserPlayer;

@ExtendWith(MockitoExtension.class)
public class GameTimeoutTerminationObserverTest {

    @Test
    void testGameTimeoutTermination() {
        var whitePlayer = new UserPlayer(UUID.randomUUID().toString(), Colors.WHITE);
        var blackPlayer = new UserPlayer(UUID.randomUUID().toString(), Colors.BLACK);

        var context = new GameContext();
        context.setTimeout(createGameTimeout(10000L));

        var game = new PlayableGameBuilder<>(whitePlayer, blackPlayer)
                .withContext(context)
                .build();

        var initialBoardState = game.getBoard().getState();

        game.notifyObservers(new GameTimeoutTerminationEvent(game));

        var currentBoardState = game.getBoard().getState();

        assertNotEquals(initialBoardState, currentBoardState);
        assertEquals(BoardState.Type.TIMEOUT, currentBoardState.getType());
    }
}