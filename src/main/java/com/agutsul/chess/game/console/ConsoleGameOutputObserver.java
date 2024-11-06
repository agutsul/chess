package com.agutsul.chess.game.console;

import static java.lang.System.lineSeparator;

import java.util.Optional;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.event.ActionCancelledEvent;
import com.agutsul.chess.action.event.ActionCancellingEvent;
import com.agutsul.chess.action.event.ActionExecutionEvent;
import com.agutsul.chess.action.event.ActionPerformedEvent;
import com.agutsul.chess.action.event.DrawExecutionEvent;
import com.agutsul.chess.action.event.DrawPerformedEvent;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.game.AbstractGame;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.game.observer.AbstractGameObserver;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.Memento;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerDrawActionExceptionEvent;

final class ConsoleGameOutputObserver
        extends AbstractGameObserver {

    private static final String ENTER_ACTION_MESSAGE = "Please, enter an action in the following format: '<source_position> <target_position>'.";
    private static final String ENTER_ACTION_EXAMPLE_MESSAGE = "For example: 'e2 e4'";

    private static final String DRAW_MESSAGE = "Draw";
    private static final String ACTION_MESSAGE = "Action";
    private static final String GAME_OVER_MESSAGE = "Game over";

    ConsoleGameOutputObserver(Game game) {
        super(game);
    }

    @Override
    protected void process(GameStartedEvent event) {
        System.out.println(ENTER_ACTION_MESSAGE);
        System.out.println(ENTER_ACTION_EXAMPLE_MESSAGE);

        displayBoard(((AbstractGame) event.getGame()).getBoard());
    }

    @Override
    protected void process(GameOverEvent event) {
        var game = (AbstractGame) event.getGame();
        displayWinner(game.getWinner());

        System.out.println("-".repeat(50));
        displayJournal(game.getJournal());
    }

    @Override
    protected void process(ActionPerformedEvent ignoredEvent) {
        displayBoard(((AbstractGame) this.game).getBoard());
    }

    @Override
    protected void process(ActionExecutionEvent event) {
        displayAction(event.getAction());
    }

    @Override
    protected void process(ActionCancelledEvent ignoredEvent) {
        displayBoard(((AbstractGame) this.game).getBoard());
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
        displayBoard(((AbstractGame) this.game).getBoard());
    }

    private static void displayAction(Action<?> action) {
        System.out.println(String.format("%s: %s", ACTION_MESSAGE, action));
    }

    private static void displayBoard(Board board) {
        System.out.println(String.format("%s%s", lineSeparator(), board));
    }

    private static void displayJournal(Journal<Memento> journal) {
        System.out.println(journal);
    }

    private static void displayWinner(Optional<Player> winner) {
        String message = winner.isPresent()
                ? formatWinnerMessage(winner.get())
                : DRAW_MESSAGE;

        System.out.println(String.format("%s. %s", GAME_OVER_MESSAGE, message));
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