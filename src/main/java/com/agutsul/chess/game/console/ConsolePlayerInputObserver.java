package com.agutsul.chess.game.console;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.strip;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.upperCase;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.Strings;
import org.slf4j.Logger;

import com.agutsul.chess.event.CompositeEventObserver;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.PlayerCommand;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;
import com.agutsul.chess.player.event.RequestPromotionPieceTypeEvent;
import com.agutsul.chess.player.observer.AbstractPlayerInputObserver;

public class ConsolePlayerInputObserver
        extends AbstractPlayerInputObserver {

    private static final Logger LOGGER = getLogger(ConsolePlayerInputObserver.class);

    private static final String EMPTY_LINE_MESSAGE = "Unable to process an empty line";
    private static final String UNSUPPORTED_COMMAND_MESSAGE = "Unsupported command";

    protected InputStream inputStream;

    public ConsolePlayerInputObserver(Player player, Game game, InputStream inputStream) {
        super(player, game);
        this.inputStream = inputStream;

//        ((Observable) game).addObserver(new CloseableGameOverObserver(inputStream));
    }

    @Override
    protected Observer createObserver() {
        return new CompositeEventObserver(
                new RequestPlayerActionConsoleObserver(),
                new RequestPromotionPieceTypeConsoleObserver()
        );
    }

    @Override
    protected String getActionCommand(Optional<Long> timeout) {
        LOGGER.info("{}: '{}' move:", this.player.getColor(), this.player);

        var actionCommand = strip(lowerCase(readConsoleInput(timeout)));
        if (isEmpty(actionCommand)) {
            throw new IllegalActionException(EMPTY_LINE_MESSAGE);
        }

        if (Strings.CI.equals(PlayerCommand.WIN.code(), actionCommand)) {
            throw new IllegalActionException(String.format("%s: '%s'",
                    UNSUPPORTED_COMMAND_MESSAGE, actionCommand
            ));
        }

        LOGGER.info("{}: '{}' performs move: '{}'",
                this.player.getColor(), this.player, actionCommand
        );

        return actionCommand;
    }

    @Override
    protected String getPromotionPieceType(Optional<Long> timeout) {
        LOGGER.info("{}: '{}' request promotion piece type",
                this.player.getColor(), this.player
        );

        var input = trimToEmpty(readConsoleInput(timeout));
        if (isEmpty(input)) {
            throw new IllegalActionException(EMPTY_LINE_MESSAGE);
        }

        var pieceTypeCode = upperCase(input.substring(0, 1));
        LOGGER.info("{}: '{}' selects promotion piece type: '{}'",
                this.player.getColor(), this.player, pieceTypeCode
        );

        return pieceTypeCode;
    }

    private String readConsoleInput(Optional<Long> timeout) {
        var consoleInputReader = Stream.of(timeout)
                .flatMap(Optional::stream)
                .map(this::createTimeoutConsoleInputReader)
                .findFirst()
                .orElse(createConsoleInputReader());

        try {
            return consoleInputReader.read();
        } catch (IOException e) {
            throw new IllegalActionException(e.getMessage(), e);
        }
    }

    private ConsoleInputReader createTimeoutConsoleInputReader(long timeoutMillis) {
        return new TimeoutConsoleInputReader(this.player, this.inputStream, timeoutMillis);
    }

    private ConsoleInputReader createConsoleInputReader() {
        return new ConsoleInputBufferedReader(this.player, this.inputStream);
    }

    private final class RequestPlayerActionConsoleObserver
            extends RequestPlayerActionObserver {

        @Override
        protected void process(RequestPlayerActionEvent event) {
            notifyGameEvent(event);
            super.process(event);
        }
    }

    private final class RequestPromotionPieceTypeConsoleObserver
            extends RequestPromotionPieceTypeObserver {

        @Override
        protected void process(RequestPromotionPieceTypeEvent event) {
            notifyGameEvent(event);
            super.process(event);
        }
    }
}