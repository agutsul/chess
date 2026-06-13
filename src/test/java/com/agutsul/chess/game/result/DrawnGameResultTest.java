package com.agutsul.chess.game.result;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DrawnGameResultTest {

    @Test
    void testGameResult() {
        var result = new DrawnGameResult();
        assertEquals(GameResult.Type.DRAWN_GAME, result.getType());
    }
}