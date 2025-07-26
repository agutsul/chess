package com.agutsul.chess.game.console;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.color.Colors;
import com.agutsul.chess.player.UserPlayer;

@ExtendWith(MockitoExtension.class)
public class ConsoleInputBufferedReaderTest {

    @Test
    void testInputStreamRead() throws IOException {
        var text = String.format("test%s", System.lineSeparator());
        var player = new UserPlayer("white_player", Colors.WHITE);

        try (var inputStream = new ByteArrayInputStream(text.getBytes())) {
            var consoleActionReader = new ConsoleInputBufferedReader(player, inputStream);
            assertEquals("test", consoleActionReader.read());
        }
    }

    @Test
    @Disabled
    void testInputStreamReadBlankText() throws IOException {
        var text = String.format("%s", System.lineSeparator());
        var player = new UserPlayer("white_player", Colors.WHITE);

        try (var inputStream = new ByteArrayInputStream(text.getBytes())) {
            var consoleActionReader = new ConsoleInputBufferedReader(player, inputStream);
            assertNull(consoleActionReader.read());
        }
    }

    @Test
    @Disabled
    void testExceptionWhileReading() throws IOException {
        var player = new UserPlayer("white_player", Colors.WHITE);
        var consoleActionReader = new ConsoleInputBufferedReader(player, null);

        var thrown = assertThrows(
                IOException.class,
                () -> consoleActionReader.read()
        );

        assertEquals("WHITE: 'white_player' Reading action from console failed", thrown.getMessage());
        assertEquals(NullPointerException.class, thrown.getCause().getClass());
    }
}