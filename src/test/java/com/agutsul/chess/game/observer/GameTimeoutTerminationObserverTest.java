package com.agutsul.chess.game.observer;

import static com.agutsul.chess.player.PlayerFactory.playerOf;
import static com.agutsul.chess.timeout.TimeoutFactory.createGameTimeout;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.GameContext;
import com.agutsul.chess.game.PlayableGameBuilder;
import com.agutsul.chess.game.event.GameTimeoutTerminationEvent;

@ExtendWith(MockitoExtension.class)
public class GameTimeoutTerminationObserverTest {

    @AutoClose
    GameContext context = new GameContext();

    @Test
    void testGameTimeoutTermination() {
        var whitePlayer = playerOf(Colors.WHITE);
        var blackPlayer = playerOf(Colors.BLACK);

        context.setTimeout(createGameTimeout(10000L));

        var game = new PlayableGameBuilder<>(whitePlayer, blackPlayer)
                .withContext(context)
                .build();

        var initialBoardState = game.getBoard().getState();

        game.notifyObservers(new GameTimeoutTerminationEvent(game, whitePlayer));

        var currentBoardState = game.getBoard().getState();

        assertNotEquals(initialBoardState, currentBoardState);
        assertEquals(BoardState.Type.TIMEOUT, currentBoardState.getType());
    }
}