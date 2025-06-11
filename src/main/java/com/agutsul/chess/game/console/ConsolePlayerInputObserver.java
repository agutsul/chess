package com.agutsul.chess.game.console;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.strip;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.upperCase;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

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
    protected Instant actionStarted;

    public ConsolePlayerInputObserver(Player player, Game game, InputStream inputStream) {
        super(LOGGER, player, game);
        this.inputStream = inputStream;
    }

    @Override
    protected void process(RequestPlayerActionEvent event) {
        notifyGameEvent(event);
        super.process(event);
    }

    @Override
    protected void process(RequestPromotionPieceTypeEvent event) {
        notifyGameEvent(event);
        super.process(event);
    }

    @Override
    protected String getActionCommand() {
        LOGGER.info("{}: '{}' move:", this.player.getColor(), this.player);

        var context = this.game.getContext();

        var timeoutMillis = context.getActionTimeout();
        if (timeoutMillis != null) {
            this.actionStarted = Instant.now();
        }

        var actionCommand = strip(lowerCase(readConsoleInput(timeoutMillis)));
        if (isEmpty(actionCommand)) {
            throw new IllegalActionException(EMPTY_LINE_MESSAGE);
        }

        if (StringUtils.equalsIgnoreCase(PlayerCommand.WIN.code(), actionCommand)) {
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
    protected String getPromotionPieceType() {
        LOGGER.info("{}: '{}' request promotion piece type",
                this.player.getColor(), this.player
        );

        var context = this.game.getContext();

        var timeoutMillis = context.getActionTimeout();
        if (timeoutMillis != null && this.actionStarted != null) {
            // calculate remaining timeout for promotion piece type selection
            var generalTimeout = this.actionStarted.toEpochMilli() + timeoutMillis;
            timeoutMillis = generalTimeout - Instant.now().toEpochMilli();

            // prevent re-usage of already set timestamp
            // because promotion happens only after some actual action like move or capture
            // that should set 'actionStarted' field properly
            this.actionStarted = null;
        }

        var input = trimToEmpty(readConsoleInput(timeoutMillis));
        if (isEmpty(input)) {
            throw new IllegalActionException(EMPTY_LINE_MESSAGE);
        }

        var pieceTypeCode = upperCase(input.substring(0, 1));
        LOGGER.info("{}: '{}' selects promotion piece type: '{}'",
                this.player.getColor(), this.player, pieceTypeCode
        );

        return pieceTypeCode;
    }

    private String readConsoleInput(Long timeoutMillis) {
        var consoleInputReader = timeoutMillis != null
                ? new TimeoutConsoleInputReader(this.player, this.inputStream, timeoutMillis)
                : new ConsoleInputScanner(this.player, this.inputStream);

        try {
            return consoleInputReader.read();
        } catch (IOException e) {
            throw new IllegalActionException(e.getMessage(), e);
        }
    }
}