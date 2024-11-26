package com.agutsul.chess.game.pgn;
import static java.lang.System.lineSeparator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.agutsul.chess.game.Game;

public class PgnGameFormatter {

    private static final String EVENT_TAG = "Event";
    private static final String WHITE_TAG = "White";
    private static final String BLACK_TAG = "Black";
    private static final String RESULT_TAG = "Result";
    private static final String DATE_TAG = "Date";

    public static String format(Game game) {
        var gameState = String.valueOf(game.getState());

        var builder = new StringBuilder();

        builder.append(formatTag(EVENT_TAG, ""));
        builder.append(formatTag(WHITE_TAG, String.valueOf(game.getWhitePlayer())));
        builder.append(formatTag(BLACK_TAG, String.valueOf(game.getBlackPlayer())));
        builder.append(formatTag(RESULT_TAG, gameState));
        builder.append(formatTag(DATE_TAG, formatDate(game.getStartedAt())));

        builder.append(lineSeparator());

        builder.append(String.valueOf(game.getJournal()));
        builder.append(" ");
        builder.append(gameState);

        builder.append(lineSeparator());
        return builder.toString();
    }

    private static String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }

    private static String formatTag(String name, String value) {
        return String.format("[%s \"%s\"]%s", name, value, lineSeparator());
    }
}