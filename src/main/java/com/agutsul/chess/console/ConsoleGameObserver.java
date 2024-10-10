package com.agutsul.chess.console;

import static java.lang.System.lineSeparator;

import java.util.Optional;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.event.ActionExecutionEvent;
import com.agutsul.chess.action.event.ActionPerformedEvent;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.AbstractGame;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;

class ConsoleGameObserver
        implements Observer {

    private final Game game;

    public ConsoleGameObserver(Game game) {
        this.game = game;
    }

    @Override
    public void observe(Event event) {
        if (event instanceof GameStartedEvent) {
            process((GameStartedEvent) event);
        } else if (event instanceof GameOverEvent) {
            process((GameOverEvent) event);
        } else  if (event instanceof ActionPerformedEvent) {
            process((ActionPerformedEvent) event);
        } else if (event instanceof ActionExecutionEvent) {
            process((ActionExecutionEvent) event);
        } else if (event instanceof PlayerActionExceptionEvent) {
            process((PlayerActionExceptionEvent) event);
        }
    }

    private void process(GameStartedEvent event) {
        System.out.println("Please, enter an action in the following format: '<source_position> <target_position>'.");
        System.out.println("For example: 'e2 e4'");
        displayBoard(((AbstractGame) event.getGame()).getBoard());
    }

    private void process(GameOverEvent event) {
        displayWinner(event.getGame().getWinner());
    }

    private void process(ActionPerformedEvent event) {
        displayBoard(((AbstractGame) game).getBoard());
    }

    private void process(ActionExecutionEvent event) {
        displayAction(event.getAction());
    }

    private void process(PlayerActionExceptionEvent event) {
        displayErrorMessage(event.getMessage());
    }

    private void displayAction(Action<?> action) {
        System.out.println(String.format("Performed action: %s", action));
    }

    private void displayBoard(Board board) {
        System.out.println(String.format("%s%s", lineSeparator(), board));
    }

    private void displayWinner(Optional<Player> winner) {
        String message = winner.isPresent()
                ? formatWinnerMessage(winner.get())
                : "Draw.";

        System.out.println(String.format("Game over. %s", message));
    }

    private String formatWinnerMessage(Player player) {
        return String.format("%s wins! Congratulations, '%s' !!!",
                player.getColor(), player);
    }

    private void displayErrorMessage(String message) {
        System.err.println(message);
    }
}