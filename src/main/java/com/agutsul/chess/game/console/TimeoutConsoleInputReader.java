package com.agutsul.chess.game.console;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.apache.commons.io.input.CloseShieldInputStream.wrap;
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

    private final Player player;
    private final ConsoleInputReader consoleActionReader;
    private final long timeout;

    TimeoutConsoleInputReader(Player player, InputStream inputStream, long timeoutMillis) {
        this(player, new TimeoutConsoleInputReaderImpl(player, inputStream), timeoutMillis);
    }

    TimeoutConsoleInputReader(Player player, ConsoleInputReader consoleActionReader,
                              long timeoutMillis) {

        this.player = player;
        this.consoleActionReader = consoleActionReader;
        this.timeout = timeoutMillis;
    }

    @Override
    public String read() throws IOException {
        if (this.timeout <= 0) {
            // it means that current timestamp is greater than general action timeout
            throw createGameTimeoutException(this.player);
        }

        var executor = newSingleThreadExecutor();
        try {
            var future = executor.submit(() -> this.consoleActionReader.read());
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

    private static final class TimeoutConsoleInputReaderImpl
            implements ConsoleInputReader {

        private static final Logger LOGGER = getLogger(TimeoutConsoleInputReaderImpl.class);

        private final Player player;
        private final InputStream inputStream;

        TimeoutConsoleInputReaderImpl(Player player, InputStream inputStream) {
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
                LOGGER.warn("{}: '{}' console read cancelled",
                        this.player.getColor(), this.player
                );
            } catch (Exception e) {
                var message = String.format(
                        "%s: '%s' Reading action from console failed",
                        this.player.getColor(), this.player
                );

                throw new IOException(message, e);
            }

            return line;
        }
    }
}