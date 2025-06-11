package com.agutsul.chess.player.observer;
import static org.apache.commons.lang3.ThreadUtils.sleepQuietly;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Duration;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.command.CancelActionCommand;
import com.agutsul.chess.command.PerformActionCommand;
import com.agutsul.chess.command.TerminateGameActionCommand;
import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.event.CompositeEventObserver;
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

public class PlayerActionObserver
        implements Observer {

    private static final Logger LOGGER = getLogger(PlayerActionObserver.class);

    private final Observer observer;

    public PlayerActionObserver(Game game) {
        this.observer = new CompositeEventObserver(
                new PlayerActionObserverImpl(game),
                new PlayerCancelActionObserverImpl(game),
                new PlayerTerminateActionObserverImpl(game)
        );
    }

    @Override
    public void observe(Event event) {
        this.observer.observe(event);
    }

    protected void requestPlayerAction(Board board, Player player) {
        // re-ask player about new action
        ((Observable) board).notifyObservers(new RequestPlayerActionEvent(player));
    }

    abstract class AbstractPlayerEventObserver<EVENT extends Event>
            extends AbstractEventObserver<EVENT> {

        protected final Game game;

        AbstractPlayerEventObserver(Game game) {
            this.game = game;
        }

        protected void notifyGameEvent(Event event) {
            // display error message to player
            ((Observable) this.game).notifyObservers(event);
            sleepQuietly(Duration.ofMillis(1));
        }

        protected void requestPlayerAction(Player player) {
            PlayerActionObserver.this.requestPlayerAction(this.game.getBoard(), player);
        }
    }

    final class PlayerActionObserverImpl
            extends AbstractPlayerEventObserver<PlayerActionEvent> {

        PlayerActionObserverImpl(Game game) {
            super(game);
        }

        @Override
        protected void process(PlayerActionEvent event) {
            var player = event.getPlayer();
            try {
                var command = new PerformActionCommand(player, this.game.getBoard(), (Observable) this.game);
                command.setSource(event.getSource());
                command.setTarget(event.getTarget());

                command.execute();
            } catch (IllegalActionException | IllegalPositionException e) {
                LOGGER.error(String.format("Player '%s' action exception", player.getName()), e);
                notifyGameEvent(new PlayerActionExceptionEvent(e.getMessage()));
                requestPlayerAction(player);
            }
        }
    }

    final class PlayerCancelActionObserverImpl
            extends AbstractPlayerEventObserver<PlayerCancelActionEvent> {

        PlayerCancelActionObserverImpl(Game game) {
            super(game);
        }

        @Override
        protected void process(PlayerCancelActionEvent event) {
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
    }

    final class PlayerTerminateActionObserverImpl
            extends AbstractPlayerEventObserver<PlayerTerminateActionEvent> {

        PlayerTerminateActionObserverImpl(Game game) {
            super(game);
        }

        @Override
        protected void process(PlayerTerminateActionEvent event) {
            var player = event.getPlayer();
            var eventType = event.getType();

            try {
                var terminateGameCommand = new TerminateGameActionCommand(this.game, player, eventType);
                terminateGameCommand.execute();
            } catch (IllegalActionException e) {
                var message = String.format("Player '%s' termination(%s) exception",
                        player.getName(), eventType
                );

                LOGGER.error(message, e);

                notifyGameEvent(new PlayerTerminateActionExceptionEvent(
                        player, e.getMessage(), eventType
                ));

                requestPlayerAction(player);
            }
        }
    }
}