package com.agutsul.chess.game.console;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import com.agutsul.chess.player.Player;

final class ConsoleInputScanner
        extends AbstractConsoleInputReader {

    ConsoleInputScanner(Player player, InputStream inputStream) {
        super(player, inputStream);
    }

    @Override
    public String read() throws IOException {
        try (var scanner = new Scanner(this.inputStream, UTF_8)) {
            if (scanner.hasNextLine()) {
                var line = scanner.nextLine();
                if (!line.isBlank()) {
                    return line;
                }
            }
        } catch (Exception e) {
            var message = String.format(
                    "%s: '%s' Reading action from console failed",
                    this.player.getColor(), this.player
            );

            throw new IOException(message, e);
        }

        return null;
    }
}