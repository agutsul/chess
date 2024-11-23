package com.agutsul.chess.game.console;

import static com.agutsul.chess.journal.JournalFormatter.format;
import static java.lang.System.lineSeparator;
import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.time.Duration;
import java.util.Optional;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.event.ActionCancelledEvent;
import com.agutsul.chess.action.event.ActionCancellingEvent;
import com.agutsul.chess.action.event.ActionExecutionEvent;
import com.agutsul.chess.action.event.ActionPerformedEvent;
import com.agutsul.chess.action.event.DrawExecutionEvent;
import com.agutsul.chess.action.event.DrawPerformedEvent;
import com.agutsul.chess.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.game.observer.AbstractGameObserver;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalFormatter.Mode;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerDrawActionExceptionEvent;

public final class ConsoleGameOutputObserver
        extends AbstractGameObserver {

    private static final String ENTER_ACTION_MESSAGE = "Please, enter an action in the following format: '<source_position> <target_position>'.";
    private static final String ENTER_ACTION_EXAMPLE_MESSAGE = "For example: 'e2 e4'";

    private static final String DRAW_MESSAGE = "Draw";
    private static final String ACTION_MESSAGE = "Action";
    private static final String GAME_OVER_MESSAGE = "Game over";
    private static final String DURATION_MESSAGE = "Duration (minutes)";

    public ConsoleGameOutputObserver(Game game) {
        super(game);
    }

    @Override
    protected void process(GameStartedEvent event) {
        System.out.println(ENTER_ACTION_MESSAGE);
        System.out.println(ENTER_ACTION_EXAMPLE_MESSAGE);

        displayBoard(((AbstractPlayableGame) event.getGame()).getBoard());
    }

    @Override
    protected void process(GameOverEvent event) {
        var game = (AbstractPlayableGame) event.getGame();
        var line = "-".repeat(50);

        System.out.println(line);
        displayJournal(game.getJournal());

        System.out.println(line);
        displayWinner(game.getWinner());

        var finishedAt = defaultIfNull(game.getFinishedAt(), now());
        displayDuration(Duration.between(game.getStartedAt(), finishedAt));
    }

    @Override
    protected void process(ActionPerformedEvent ignoredEvent) {
        displayBoard(((AbstractPlayableGame) this.game).getBoard());
    }

    @Override
    protected void process(ActionExecutionEvent event) {
        displayAction(event.getAction());
    }

    @Override
    protected void process(ActionCancelledEvent ignoredEvent) {
        displayBoard(((AbstractPlayableGame) this.game).getBoard());
    }

    @Override
    protected void process(ActionCancellingEvent event) {
        displayAction(event.getAction());
    }

    @Override
    protected void process(PlayerActionExceptionEvent event) {
        displayErrorMessage(event.getMessage());
    }

    @Override
    protected void process(PlayerCancelActionExceptionEvent event) {
        displayErrorMessage(event.getMessage());
    }

    @Override
    protected void process(PlayerDrawActionExceptionEvent event) {
        displayErrorMessage(event.getMessage());
    }

    @Override
    protected void process(DrawExecutionEvent event) {
        var player = event.getPlayer();
        System.out.println(String.format("%s: Player '%s' asked a draw",
                player.getColor(),
                player.getName()
        ));
    }

    @Override
    protected void process(DrawPerformedEvent ignoredEvent) {
        displayBoard(((AbstractPlayableGame) this.game).getBoard());
    }

    private static void displayAction(Action<?> action) {
        System.out.println(String.format("%s: %s", ACTION_MESSAGE, action));
    }

    private static void displayBoard(Board board) {
        System.out.println(String.format("%s%s", lineSeparator(), board));
    }

    private static void displayJournal(Journal<ActionMemento<?,?>> journal) {
        System.out.println(format(journal, Mode.MULTI_LINE));
    }

    private static void displayWinner(Optional<Player> winner) {
        String message = winner.isPresent()
                ? formatWinnerMessage(winner.get())
                : DRAW_MESSAGE;

        System.out.println(String.format("%s. %s", GAME_OVER_MESSAGE, message));
    }

    private static void displayDuration(Duration duration) {
        System.out.println(String.format("%s: %s", DURATION_MESSAGE, duration.toMinutes()));
    }

    private static String formatWinnerMessage(Player player) {
        return String.format("%s wins! Congratulations, '%s' !!!",
                player.getColor(),
                player
        );
    }

    private static void displayErrorMessage(String message) {
        System.err.println(message);
    }

}