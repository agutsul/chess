package com.agutsul.chess.game.observer;

import static com.agutsul.chess.player.PlayerFactory.playerOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.AbstractGame;
import com.agutsul.chess.game.event.SwitchPlayerEvent;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.state.PlayerState;

@ExtendWith(MockitoExtension.class)
public class SwitchPlayerObserverTest {

    @Mock
    AbstractGame game;
    @Spy
    Player whitePlayer = playerOf(Colors.WHITE, "testA");
    @Spy
    Player blackPlayer = playerOf(Colors.BLACK, "testB");

    @InjectMocks
    SwitchPlayerObserver observer;

    @Test
    void testSwitchPlayer() {
        when(game.getCurrentPlayer())
            .thenReturn(whitePlayer);

        when(game.getOpponentPlayer())
            .thenReturn(blackPlayer);

        assertNull(whitePlayer.getState());
        assertNull(blackPlayer.getState());

        observer.observe(new SwitchPlayerEvent());

        assertEquals(PlayerState.Type.LOCKED, blackPlayer.getState().getType());
        assertEquals(PlayerState.Type.ACTIVE, whitePlayer.getState().getType());

        verify(blackPlayer, times(1)).disable();
        verify(whitePlayer, times(1)).enable();
    }
}