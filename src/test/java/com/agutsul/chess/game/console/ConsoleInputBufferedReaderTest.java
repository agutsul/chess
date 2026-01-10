package com.agutsul.chess.game.console;

import static com.agutsul.chess.player.PlayerFactory.playerOf;
import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.color.Colors;
import com.agutsul.chess.player.Player;

@ExtendWith(MockitoExtension.class)
public class ConsoleInputBufferedReaderTest {

    @Spy
    Player player = playerOf(Colors.WHITE, "white_player");

    @Test
    void testInputStreamRead() throws IOException {
        var text = String.format("test%s", lineSeparator());

        try (var inputStream = new ByteArrayInputStream(text.getBytes())) {
            var consoleActionReader = new ConsoleInputBufferedReader(player, inputStream);
            assertEquals("test", consoleActionReader.read());
        }
    }
}