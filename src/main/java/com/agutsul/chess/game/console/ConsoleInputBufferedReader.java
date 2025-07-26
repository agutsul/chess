package com.agutsul.chess.game.console;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.input.CloseShieldInputStream.wrap;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;

import com.agutsul.chess.exception.GameInterruptionException;
import com.agutsul.chess.player.Player;

final class ConsoleInputBufferedReader
        implements ConsoleInputReader {

    private final Player player;
    private final InputStream inputStream;

    ConsoleInputBufferedReader(Player player, InputStream inputStream) {
        this.player = player;
        this.inputStream = wrap(inputStream);
    }

    @Override
    public String read() throws IOException {
        String line = null;
        try (var reader = new BufferedReader(new InputStreamReader(this.inputStream, UTF_8))) {
            do {
                while (!reader.ready()) {
                    Thread.sleep(Duration.ofMillis(10));
                }

                line = reader.readLine();
            } while (isBlank(line));
        } catch (InterruptedException e) {
            throw new GameInterruptionException(String.format(
                    "%s: '%s' entering action interrupted",
                    player.getColor(), player
            ));
        } catch (IOException e) {
            var message = String.format(
                    "%s: '%s' Reading action from console failed",
                    player.getColor(), player
            );

            throw new IOException(message, e.getCause());
        }

        return line;
    }
}