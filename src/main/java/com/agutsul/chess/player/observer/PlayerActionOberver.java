package com.agutsul.chess.player.observer;
import static org.apache.commons.lang3.ThreadUtils.sleepQuietly;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.command.CancelActionCommand;
import com.agutsul.chess.command.PerformActionCommand;
import com.agutsul.chess.command.TerminateGameActionCommand;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.exception.IllegalPositionException;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.event.PlayerActionEvent;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerTerminateActionEvent;
import com.agutsul.chess.player.event.PlayerTerminateActionExceptionEvent;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;

public class PlayerActionOberver
        implements Observer {

    private static final Logger LOGGER = getLogger(PlayerActionOberver.class);

    private final Map<Class<? extends Event>, Consumer<Event>> processors = Map.of(
            PlayerActionEvent.class,          event -> process((PlayerActionEvent) event),
            PlayerCancelActionEvent.class,    event -> process((PlayerCancelActionEvent) event),
            PlayerTerminateActionEvent.class, event -> process((PlayerTerminateActionEvent) event)
    );

    private final Game game;

    public PlayerActionOberver(Game game) {
        this.game = game;
    }

    @Override
    public void observe(Event event) {
        var processor = this.processors.get(event.getClass());
        if (processor != null) {
            processor.accept(event);
        }
    }

    private void process(PlayerActionEvent event) {
        var player = event.getPlayer();

        try {
            var command = new PerformActionCommand(player, game.getBoard(), (Observable) this.game);
            command.setSource(event.getSource());
            command.setTarget(event.getTarget());

            command.execute();
        } catch (IllegalActionException | IllegalPositionException e) {
            LOGGER.error(String.format("Player '%s' action exception", player.getName()), e);
            notifyGameEvent(new PlayerActionExceptionEvent(e.getMessage()));

            requestPlayerAction(player);
        }
    }

    private void process(PlayerCancelActionEvent event) {
        var player = event.getPlayer();
        var playerColor = player.getColor();

        try {
            // cancel opponent player action
            var cancelOpponentActionCommand = new CancelActionCommand(this.game, playerColor.invert());
            cancelOpponentActionCommand.execute();

            // cancel current player action
            var cancelActionCommand = new CancelActionCommand(this.game, playerColor);
            cancelActionCommand.execute();

            requestPlayerAction(player);
        } catch (IllegalActionException | IllegalPositionException e) {
            LOGGER.error(String.format("Player '%s' cancel action exception", player.getName()), e);
            notifyGameEvent(new PlayerCancelActionExceptionEvent(e.getMessage()));

            requestPlayerAction(player);
        }
    }

    private void process(PlayerTerminateActionEvent event) {
        var player = event.getPlayer();
        var eventType = event.getType();

        try {
            var terminateGameCommand = new TerminateGameActionCommand(this.game, player, eventType);
            terminateGameCommand.execute();
        } catch (IllegalActionException e) {
            LOGGER.error(
                    String.format("Player '%s' termination(%s) exception", player.getName(), eventType),
                    e
            );

            notifyGameEvent(new PlayerTerminateActionExceptionEvent(
                    player, e.getMessage(), eventType
            ));

            requestPlayerAction(player);
        }
    }

    private void notifyGameEvent(Event event) {
        // display error message to player
        ((Observable) this.game).notifyObservers(event);
        sleepQuietly(Duration.ofMillis(1));
    }

    protected void requestPlayerAction(Player player) {
        requestPlayerAction(this.game.getBoard(), player);
    }

    private static void requestPlayerAction(Board board, Player player) {
        // re-ask player about new action
        ((Observable) board).notifyObservers(new RequestPlayerActionEvent(player));
    }
}