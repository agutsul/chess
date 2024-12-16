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
        var gameState = formatGameState(game.getState());

        var builder = new StringBuilder();

        builder.append(formatTag(EVENT_TAG, EMPTY));
        builder.append(formatTag(WHITE_TAG, formatPlayer(game.getWhitePlayer())));
        builder.append(formatTag(BLACK_TAG, formatPlayer(game.getBlackPlayer())));
        builder.append(formatTag(RESULT_TAG, gameState));
        builder.append(formatTag(DATE_TAG, formatDate(game.getStartedAt())));

        builder.append(lineSeparator());

        builder.append(formatJournal(game.getJournal()));
        builder.append(SPACE);
        builder.append(gameState);

        builder.append(lineSeparator());
        return builder.toString();
    }

    private static String formatGameState(GameState gameState) {
        return String.valueOf(gameState);
    }

    private static String formatPlayer(Player player) {
        return String.valueOf(player);
    }

    private static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return EMPTY;
        }

        return dateTime.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    private static String formatJournal(Journal<ActionMemento<?,?>> journal) {
        return JournalFormatter.format(journal, Mode.SINGLE_LINE);
    }

    private static String formatTag(String name, String value) {
        return String.format("[%s \"%s\"]%s",
                defaultIfNull(name, EMPTY),
                defaultIfNull(value, EMPTY),
                lineSeparator()
        );
    }
}