package com.agutsul.chess.game.result;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WhiteWinGameResultTest {

    @Test
    void testGameResult() {
        var result = new WhiteWinGameResult();
        assertEquals(GameResult.Type.WHITE_WIN, result.getType());
    }
}