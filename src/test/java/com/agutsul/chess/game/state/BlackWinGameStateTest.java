package com.agutsul.chess.game.state;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BlackWinGameStateTest {

    @Test
    void testGameState() {
        var state = new BlackWinGameState();
        assertEquals(GameState.Type.BLACK_WIN, state.getType());
    }
}