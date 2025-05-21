package com.agutsul.chess.game.console;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.agutsul.chess.exception.GameTimeoutException;
import com.agutsul.chess.player.Player;

final class TimeoutConsoleActionReader
        implements ConsoleActionReader {

    private final Player player;
    private final ConsoleActionReader consoleActionReader;
    private final long timeout;

    TimeoutConsoleActionReader(Player player, ConsoleActionReader consoleActionReader,
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
}