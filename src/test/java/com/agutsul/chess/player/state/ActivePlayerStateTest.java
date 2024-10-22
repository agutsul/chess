package com.agutsul.chess.player.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.event.Observable;
import com.agutsul.chess.player.Player;

@ExtendWith(MockitoExtension.class)
public class ActivePlayerStateTest {

    @Test
    void testPlay() {
        var player = mock(Player.class);
        var observable = mock(Observable.class);


        var state  = new ActivePlayerState(observable);
        state.play(player);

        assertEquals(PlayerState.Type.ACTIVE, state.getType());
        verify(observable, times(1)).notifyObservers(any());
    }
}