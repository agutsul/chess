package com.agutsul.chess.game.result;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BlackWinGameResultTest {

    @Test
    void testGameResult() {
        var result = new BlackWinGameResult();
        assertEquals(GameResult.Type.BLACK_WIN, result.getType());
    }
}