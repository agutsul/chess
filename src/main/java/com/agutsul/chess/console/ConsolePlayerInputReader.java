package com.agutsul.chess.console;

import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.upperCase;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.observer.AbstractPlayerInputObserver;

final class ConsolePlayerInputReader
        extends AbstractPlayerInputObserver {

    static final String PROMOTION_PIECE_TYPE_MESSAGE = "Choose promotion piece type:";
    static final String EMPTY_LINE_MESSAGE = "Unable to process an empty line";

    ConsolePlayerInputReader(Player player, Game game) {
        super(player, game);
    }

    @Override
    protected String getActionCommand() {
        System.out.println(String.format("%s: '%s' move:%s",
                this.player.getColor(), this.player, lineSeparator()));

        return lowerCase(readConsoleInput());
    }

    @Override
    protected String getPieceType() {
        promptPieceType();

        var input = trimToEmpty(readConsoleInput());
        if (isEmpty(input)) {
            throw new IllegalActionException(EMPTY_LINE_MESSAGE);
        }

        return upperCase(input.substring(0, 1));
    }

    private static void promptPieceType() {
        var builder = new StringBuilder();
        builder.append(PROMOTION_PIECE_TYPE_MESSAGE).append(lineSeparator());

        for (var pieceType : PROMOTION_TYPES.values()) {
            builder.append("'").append(pieceType).append("' - ");
            builder.append(pieceType.name()).append(lineSeparator());
        }

        System.out.println(builder.toString());
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
}