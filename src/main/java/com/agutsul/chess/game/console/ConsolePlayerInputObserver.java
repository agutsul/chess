package com.agutsul.chess.game.console;

import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.upperCase;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

import org.slf4j.Logger;

import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.observer.AbstractPlayerInputObserver;

public class ConsolePlayerInputObserver
        extends AbstractPlayerInputObserver {

    private static final Logger LOGGER = getLogger(ConsolePlayerInputObserver.class);

    private static final String PROMOTION_PIECE_TYPE_MESSAGE = "Choose promotion piece type:";
    private static final String EMPTY_LINE_MESSAGE = "Unable to process an empty line";
    private static final String UNSUPPORTED_COMMAND_MESSAGE = "Unsupported command";

    private static final String PROMPT_PROMOTION_PIECE_TYPE_MESSAGE =
            createPromptPromotionPieceTypeMessage();

    protected InputStream inputStream;
    protected Instant actionStarted;

    public ConsolePlayerInputObserver(Player player, Game game, InputStream inputStream) {
        super(LOGGER, player, game);
        this.inputStream = inputStream;
    }

    @Override
    protected String getActionCommand() {
        var message = String.format("%s: '%s' move:%s",
                this.player.getColor(), this.player, lineSeparator()
        );

        LOGGER.info(message);
        System.out.println(message);

        var timeoutMillis = this.game.getActionTimeout();
        if (timeoutMillis != null) {
            this.actionStarted = Instant.now();
        }

        var actionCommand = lowerCase(readConsoleInput(timeoutMillis));
        if (isEmpty(actionCommand)) {
            throw new IllegalActionException(EMPTY_LINE_MESSAGE);
        }

        if (WIN_COMMAND.equalsIgnoreCase(actionCommand)) {
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

        System.out.println(PROMPT_PROMOTION_PIECE_TYPE_MESSAGE);

        var timeoutMillis = this.game.getActionTimeout();
        if (timeoutMillis != null && this.actionStarted != null) {
            // calculate remaining timeout for promotion piece type selection
            var generalTimeout = this.actionStarted.toEpochMilli() + timeoutMillis;
            timeoutMillis = generalTimeout - Instant.now().toEpochMilli();
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
        var consoleActionReader = createConsoleActionReader(timeoutMillis);
        try {
            return consoleActionReader.read();
        } catch (IOException e) {
            throw new IllegalActionException(e.getMessage(), e);
        }
    }

    private ConsoleActionReader createConsoleActionReader(Long timeoutMillis) {
        var consoleActionReader = new ConsoleActionReaderImpl(this.player, this.inputStream);
        return timeoutMillis != null
                ? new TimeoutConsoleActionReader(this.player, consoleActionReader, timeoutMillis)
                : consoleActionReader;
    }

    private static String createPromptPromotionPieceTypeMessage() {
        var builder = new StringBuilder();
        builder.append(PROMOTION_PIECE_TYPE_MESSAGE).append(lineSeparator());

        for (var pieceType : PROMOTION_TYPES.values()) {
            builder.append("'").append(pieceType).append("' - ");
            builder.append(pieceType.name()).append(lineSeparator());
        }

        return builder.toString();
    }
}