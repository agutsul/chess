package com.agutsul.chess.game.state;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DrawnGameStateTest {

    @Test
    void testGameState() {
        var state = new DrawnGameState();
        assertEquals(GameState.Type.DRAWN_GAME, state.getType());
    }
}