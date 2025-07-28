package com.agutsul.chess.game.console;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import com.agutsul.chess.exception.ActionTimeoutException;
import com.agutsul.chess.exception.GameInterruptionException;
import com.agutsul.chess.player.Player;

final class TimeoutConsoleInputReader
        implements ConsoleInputReader {

    private final ConsoleInputReader reader;
    private final Player player;
    private final long timeout;

    TimeoutConsoleInputReader(Player player, InputStream inputStream, long timeoutMillis) {
        this.reader = new ConsoleInputBufferedReader(player, inputStream);
        this.player = player;
        this.timeout = timeoutMillis;
    }

    @Override
    public String read() throws IOException {
        if (this.timeout <= 0) {
            // it means that current timestamp is greater than overall action timeout
            throw new ActionTimeoutException(player);
        }

        try (var executor = newSingleThreadExecutor()) {
            var future = executor.submit(() -> this.reader.read());
            try {
                return future.get(this.timeout, MILLISECONDS);
            } catch (TimeoutException e) {
                future.cancel(true);
                throw new ActionTimeoutException(player);
            } catch (InterruptedException e) {
                throw new GameInterruptionException(String.format(
                        "%s: '%s' entering action interrupted",
                        player.getColor(), player
                ));
            } catch (ExecutionException e) {
                var message = String.format(
                        "%s: '%s' failed to enter action",
                        player.getColor(), player
                );

                throw new IOException(message, e.getCause());
            }
        }
    }

    private static ExecutorService newSingleThreadExecutor() {
        return new ThreadPoolExecutor(1, 1, 0L, MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                BasicThreadFactory.builder()
                    .namingPattern("TimeoutConsoleInputReaderThread-%d")
                    .priority(Thread.MAX_PRIORITY)
                    .build()
        );
    }
}