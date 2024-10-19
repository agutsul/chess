package com.agutsul.chess.console;

import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.split;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.event.PlayerActionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionEvent;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;

final class ConsoleRequestPlayerActionObserver
        extends AbstractConsoleInputReader
        implements Observer {

    private static final Logger LOGGER = getLogger(ConsoleRequestPlayerActionObserver.class);

    private static final String UNDO_COMMAND = "undo";

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

        LOGGER.info("Processing player '{}' command '{}'", player.getName(), consoleCommand);

        if (UNDO_COMMAND.equalsIgnoreCase(consoleCommand)) {
            processUndoCommand(player);
        } else if (contains(consoleCommand, SPACE)) {
            processActionCommand(player, consoleCommand);
        }
    }

    private void processUndoCommand(Player player) {
        ((Observable) this.game).notifyObservers(new PlayerCancelActionEvent(player));
    }

    private void processActionCommand(Player player, String command) {
        var positions = split(command, SPACE);
        if (positions == null || positions.length != 2) {
            throw new IllegalActionException(
                    String.format("Invalid action format: %s", command));
        }

        var action = new PlayerActionEvent(player, positions[0], positions[1]);
        ((Observable) this.game).notifyObservers(action);
    }

    private String readConsoleCommand(Player player) {
        System.out.println(String.format("%s: '%s' move:%s",
                player.getColor(), player, lineSeparator()));

        return lowerCase(readConsoleInput());
    }
}