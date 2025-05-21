package com.agutsul.chess.game.console;

import static org.apache.commons.io.input.CloseShieldInputStream.wrap;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import com.agutsul.chess.player.Player;

final class ConsoleActionReaderImpl
        implements ConsoleActionReader {

    private final Player player;
    private final InputStream inputStream;

    ConsoleActionReaderImpl(Player player, InputStream inputStream) {
        this.player = player;
        this.inputStream = wrap(inputStream);
    }

    @Override
    public String read() throws IOException {
        try (var scanner = new Scanner(this.inputStream, StandardCharsets.UTF_8)) {
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