package com.agutsul.chess.game.observer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.AbstractGame;
import com.agutsul.chess.game.event.SwitchPlayerEvent;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.UserPlayer;
import com.agutsul.chess.player.state.PlayerState;

@ExtendWith(MockitoExtension.class)
public class SwitchPlayerObserverTest {

    @Mock
    AbstractGame game;

    @Test
    void testSwitchPlayer() {
        var whitePlayer = createPlayer("testA", Colors.WHITE);
        var blackPlayer = createPlayer("testB", Colors.BLACK);

        when(game.getCurrentPlayer())
            .thenReturn(whitePlayer);

        when(game.getOpponentPlayer())
            .thenReturn(blackPlayer);

        assertNull(whitePlayer.getState());
        assertNull(blackPlayer.getState());

        var observer = new SwitchPlayerObserver(game);
        observer.observe(new SwitchPlayerEvent());

        assertEquals(PlayerState.Type.LOCKED, blackPlayer.getState().getType());
        assertEquals(PlayerState.Type.ACTIVE, whitePlayer.getState().getType());

        verify(blackPlayer, times(1)).disable();
        verify(whitePlayer, times(1)).enable();
    }

    private static Player createPlayer(String name, Color color) {
        return spy(new UserPlayer(name, color));
    }
}