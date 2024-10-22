package com.agutsul.chess.player.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.player.Player;

@ExtendWith(MockitoExtension.class)
public class LockedPlayerStateTest {

    @Test
    void testPlay() {
        var player = mock(Player.class);

        var state  = new LockedPlayerState();
        state.play(player);

        assertEquals(PlayerState.Type.LOCKED, state.getType());
    }
}