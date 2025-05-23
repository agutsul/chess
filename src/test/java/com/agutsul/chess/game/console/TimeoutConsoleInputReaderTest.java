package com.agutsul.chess.game.console;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.color.Colors;
import com.agutsul.chess.exception.GameTimeoutException;
import com.agutsul.chess.player.UserPlayer;

@ExtendWith(MockitoExtension.class)
public class TimeoutConsoleInputReaderTest {

    @Test
    void testNegativeTimeoutArgument() {
        var player = new UserPlayer("test", Colors.WHITE);
        var reader = new TimeoutConsoleInputReader(player, mock(InputStream.class), -1);

        var thrown = assertThrows(
                GameTimeoutException.class,
                () -> reader.read()
        );

        assertEquals("WHITE: 'test' entering action timeout", thrown.getMessage());
    }

    @Test
    void testTimeoutException() throws IOException {
        var player = new UserPlayer("test", Colors.WHITE);
        var reader = new TimeoutConsoleInputReader(player, new DelayedInputStreamMock(), 50);

        var thrown = assertThrows(
                GameTimeoutException.class,
                () -> reader.read()
        );

        assertEquals("WHITE: 'test' entering action timeout", thrown.getMessage());
    }

    @Test
    void testConsoleReadSuccessfully() throws IOException {
        var text = String.format("e2 e4%s", System.lineSeparator());
        var player = new UserPlayer("test", Colors.WHITE);

        try (var inputStream = new ByteArrayInputStream(text.getBytes())) {
            var reader = new TimeoutConsoleInputReader(player, inputStream, 100);
            assertEquals("e2 e4", reader.read());
        }
    }

    private static class DelayedInputStreamMock extends InputStream {

        @Override
        public int read() throws IOException {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // ignore
            }
            return 0;
        }
    }
}