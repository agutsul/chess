package com.agutsul.chess.game.console;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;

import com.agutsul.chess.exception.GameTimeoutException;
import com.agutsul.chess.player.Player;

final class TimeoutConsoleInputReader
        implements ConsoleInputReader {

    private final ConsoleInputReader reader;
    private final Player player;
    private final long timeout;

    TimeoutConsoleInputReader(Player player, InputStream inputStream, long timeoutMillis) {
        this.reader = new TimeoutConsoleInputBufferedReader(player, inputStream);
        this.player = player;
        this.timeout = timeoutMillis;
    }

    @Override
    public String read() throws IOException {
        if (this.timeout <= 0) {
            // it means that current timestamp is greater than overall action timeout
            throw createGameTimeoutException(this.player);
        }

        var executor = newSingleThreadExecutor();
        try {
            var future = executor.submit(() -> this.reader.read());
            try {
                return future.get(this.timeout, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                future.cancel(true);
                throw createGameTimeoutException(this.player);
            } catch (InterruptedException e) {
                throw createIOException(this.player, "%s: '%s' entering action interrupted", e);
            } catch (ExecutionException e) {
                throw createIOException(this.player, "%s: '%s' failed to enter action", e);
            }
        } finally {
            try {
                executor.shutdown();
                if (!executor.awaitTermination(1, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
    }

    private static GameTimeoutException createGameTimeoutException(Player player) {
        return new GameTimeoutException(createMessage(player, "%s: '%s' entering action timeout"));
    }

    private static IOException createIOException(Player player, String messageFormat, Exception e) {
        return new IOException(createMessage(player, messageFormat), e);
    }

    private static String createMessage(Player player, String messageFormat) {
        return String.format(messageFormat, player.getColor(), player);
    }

    private static final class TimeoutConsoleInputBufferedReader
            extends AbstractConsoleInputReader {

        private static final Logger LOGGER = getLogger(TimeoutConsoleInputBufferedReader.class);

        TimeoutConsoleInputBufferedReader(Player player, InputStream inputStream) {
            super(player, inputStream);
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
                LOGGER.warn(createMessage(player, "%s: '%s' console read cancelled"));
            } catch (Exception e) {
                throw createIOException(player, "%s: '%s' Reading action from console failed", e);
            }

            return line;
        }
    }
}