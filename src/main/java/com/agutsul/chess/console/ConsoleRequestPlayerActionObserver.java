package com.agutsul.chess.console;

import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.split;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.AbstractGame;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.event.PlayerActionEvent;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;

class ConsoleRequestPlayerActionObserver
        extends AbstractConsoleInputReader
        implements Observer {

    private final Game game;

    public ConsoleRequestPlayerActionObserver(Game game) {
        this.game = game;
    }

    @Override
    public void observe(Event event) {
        if (event instanceof RequestPlayerActionEvent) {
            process((RequestPlayerActionEvent) event);
        }
    }

    private void process(RequestPlayerActionEvent event) {
        var player = event.getPlayer();

        var consoleCommand = readConsoleCommand(player);

        var positions = split(consoleCommand, SPACE);
        if (positions == null || positions.length != 2) {
            throw new IllegalArgumentException(
                    String.format("Invalid action format: %s", consoleCommand));
        }

        var board = ((AbstractGame) game).getBoard();
        var action = new PlayerActionEvent(board, positions[0], positions[1]);

        ((Observable) game).notifyObservers(action);
    }

    private String readConsoleCommand(Player player) {
        System.out.println(String.format("%s: '%s' move:%s",
                player.getColor(), player, lineSeparator()));

        return readConsoleInput();
    }
}