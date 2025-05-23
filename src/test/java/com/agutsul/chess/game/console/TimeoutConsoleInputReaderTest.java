package com.agutsul.chess.game.console;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.color.Colors;
import com.agutsul.chess.exception.GameTimeoutException;
import com.agutsul.chess.player.UserPlayer;

@ExtendWith(MockitoExtension.class)
public class TimeoutConsoleInputReaderTest {

    @Mock
    ConsoleInputReader consoleInputReader;

    @Test
    void testNegativeTimeoutArgument() {
        var player = new UserPlayer("test", Colors.WHITE);
        var reader = new TimeoutConsoleInputReader(player, consoleInputReader, -1);

        var thrown = assertThrows(
                GameTimeoutException.class,
                () -> reader.read()
        );

        assertEquals("WHITE: 'test' entering action timeout", thrown.getMessage());
    }

    @Test
    void testTimeoutException() throws IOException {
        var player = new UserPlayer("test", Colors.WHITE);
        when(consoleInputReader.read())
            .thenAnswer(inv -> {
                Thread.sleep(100);
                return null;
            });

        var reader = new TimeoutConsoleInputReader(player, consoleInputReader, 50);

        var thrown = assertThrows(
                GameTimeoutException.class,
                () -> reader.read()
        );

        assertEquals("WHITE: 'test' entering action timeout", thrown.getMessage());
    }

    @Test
    void testIOException() throws IOException {
        var player = new UserPlayer("test", Colors.WHITE);
        when(consoleInputReader.read())
            .thenThrow(new RuntimeException("test"));

        var reader = new TimeoutConsoleInputReader(player, consoleInputReader, 50);

        var thrown = assertThrows(
                IOException.class,
                () -> reader.read()
        );

        assertEquals("WHITE: 'test' failed to enter action", thrown.getMessage());
    }

    @Test
    void testConsoleReadSuccessfully() throws IOException {
        var player = new UserPlayer("test", Colors.WHITE);
        when(consoleInputReader.read())
            .thenReturn("e2 e4");

        var reader = new TimeoutConsoleInputReader(player, consoleInputReader, 100);
        assertEquals("e2 e4", reader.read());
    }
}