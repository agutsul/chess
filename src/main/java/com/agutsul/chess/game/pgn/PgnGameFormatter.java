package com.agutsul.chess.game.pgn;

import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.state.GameState;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalFormatter;
import com.agutsul.chess.journal.JournalFormatter.Mode;
import com.agutsul.chess.player.Player;

public class PgnGameFormatter {

    private static final String EVENT_TAG = "Event";
    private static final String WHITE_TAG = "White";
    private static final String BLACK_TAG = "Black";
    private static final String RESULT_TAG = "Result";
    private static final String DATE_TAG = "Date";

    private static final String DATE_PATTERN = "yyyy.MM.dd";

    public static String format(Game game) {
        var gameState = format(game.getState());

        var builder = new StringBuilder();

        builder.append(format(EVENT_TAG,  EMPTY));
        builder.append(format(WHITE_TAG,  format(game.getWhitePlayer())));
        builder.append(format(BLACK_TAG,  format(game.getBlackPlayer())));
        builder.append(format(RESULT_TAG, gameState));
        builder.append(format(DATE_TAG,   format(game.getStartedAt())));

        builder.append(lineSeparator());

        builder.append(format(game.getJournal()));
        builder.append(SPACE);
        builder.append(gameState);

        builder.append(lineSeparator());
        return builder.toString();
    }

    private static String format(GameState gameState) {
        return String.valueOf(gameState);
    }

    private static String format(Player player) {
        return String.valueOf(player);
    }

    private static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return EMPTY;
        }

        return dateTime.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    private static String format(Journal<ActionMemento<?,?>> journal) {
        return JournalFormatter.format(journal, Mode.SINGLE_LINE);
    }

    private static String format(String name, String value) {
        return String.format("[%s \"%s\"]%s",
                defaultIfNull(name, EMPTY),
                defaultIfNull(value, EMPTY),
                lineSeparator()
        );
    }
}