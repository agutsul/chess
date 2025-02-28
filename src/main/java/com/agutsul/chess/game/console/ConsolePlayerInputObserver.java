package com.agutsul.chess.game.console;

import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.upperCase;
import static org.slf4j.LoggerFactory.getLogger;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.slf4j.Logger;

import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.observer.AbstractPlayerInputObserver;

public final class ConsolePlayerInputObserver
        extends AbstractPlayerInputObserver {

    private static final Logger LOGGER = getLogger(ConsolePlayerInputObserver.class);

    private static final String PROMOTION_PIECE_TYPE_MESSAGE = "Choose promotion piece type:";
    private static final String EMPTY_LINE_MESSAGE = "Unable to process an empty line";

    private static final String PROMPT_PROMOTION_PIECE_TYPE_MESSAGE =
            createPromptPromotionPieceTypeMessage();

    public ConsolePlayerInputObserver(Player player, Game game) {
        super(LOGGER, player, game);
    }

    @Override
    protected String getActionCommand() {
        var message = String.format("%s: '%s' move:%s",
                this.player.getColor(),
                this.player,
                lineSeparator()
        );

        LOGGER.info(message);
        System.out.println(message);

        var actionCommand = lowerCase(readConsoleInput());
        LOGGER.info("{}: '{}' performs move: '{}'",
                this.player.getColor(),
                this.player,
                actionCommand
        );

        return actionCommand;
    }

    @Override
    protected String getPromotionPieceType() {
        LOGGER.info("{}: '{}' request promotion piece type",
                this.player.getColor(),
                this.player
        );

        System.out.println(PROMPT_PROMOTION_PIECE_TYPE_MESSAGE);

        var input = trimToEmpty(readConsoleInput());
        if (isEmpty(input)) {
            throw new IllegalActionException(EMPTY_LINE_MESSAGE);
        }

        var pieceTypeCode = upperCase(input.substring(0, 1));
        LOGGER.info("{}: '{}' selects promotion piece type: '{}'",
                this.player.getColor(),
                this.player,
                pieceTypeCode
        );

        return pieceTypeCode;
    }

    private static String readConsoleInput() {
        // Keep System.in open to allow users enter their commands in console
        // Once console is closed it can't be reopen for further entering new commands
        @SuppressWarnings("resource")
        var scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (!line.isBlank()) {
                return line;
            }
        }

        return null;
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