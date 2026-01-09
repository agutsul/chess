package com.agutsul.chess.game.console;

import static com.agutsul.chess.player.PlayerFactory.playerOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.color.Colors;

@ExtendWith(MockitoExtension.class)
public class ConsoleInputBufferedReaderTest {

    @Test
    void testInputStreamRead() throws IOException {
        var text = String.format("test%s", System.lineSeparator());
        var player = playerOf(Colors.WHITE, "white_player");

        try (var inputStream = new ByteArrayInputStream(text.getBytes())) {
            var consoleActionReader = new ConsoleInputBufferedReader(player, inputStream);
            assertEquals("test", consoleActionReader.read());
        }
    }
}