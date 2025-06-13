package com.agutsul.chess.player.observer;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.command.CancelActionCommand;
import com.agutsul.chess.command.PerformActionCommand;
import com.agutsul.chess.command.TerminateGameActionCommand;
import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.event.CompositeEventObserver;
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
        extends AbstractPlayerObserver {

    public PlayerActionObserver(Game game) {
        super(game);
    }

    @Override
    protected final Observer createObserver() {
        return new CompositeEventObserver(
                new PlayerActionObserverImpl(),
                new PlayerCancelActionObserverImpl(),
                new PlayerTerminateActionObserverImpl()
        );
    }

    protected void requestPlayerAction(Player player) {
        // re-ask player about new action
        notifyBoardEvent(new RequestPlayerActionEvent(player));
    }

    private final class PlayerActionObserverImpl
            extends AbstractEventObserver<PlayerActionEvent> {

        private static final Logger LOGGER = getLogger(PlayerActionObserverImpl.class);

        @Override
        protected void process(PlayerActionEvent event) {
            var player = event.getPlayer();
            try {
                var command = new PerformActionCommand(player, game.getBoard(), (Observable) game);
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

    private final class PlayerCancelActionObserverImpl
            extends AbstractEventObserver<PlayerCancelActionEvent> {

        private static final Logger LOGGER = getLogger(PlayerCancelActionObserverImpl.class);

        @Override
        protected void process(PlayerCancelActionEvent event) {
            var player = event.getPlayer();
            var playerColor = player.getColor();

            try {
                // cancel opponent player action
                var cancelOpponentActionCommand = new CancelActionCommand(game, playerColor.invert());
                cancelOpponentActionCommand.execute();

                // cancel current player action
                var cancelActionCommand = new CancelActionCommand(game, playerColor);
                cancelActionCommand.execute();

                requestPlayerAction(player);
            } catch (IllegalActionException | IllegalPositionException e) {
                var message = String.format("Player '%s' cancel action exception", player.getName());
                LOGGER.error(message, e);

                notifyGameEvent(new PlayerCancelActionExceptionEvent(e.getMessage()));
                requestPlayerAction(player);
            }
        }
    }

    private final class PlayerTerminateActionObserverImpl
            extends AbstractEventObserver<PlayerTerminateActionEvent> {

        private static final Logger LOGGER = getLogger(PlayerTerminateActionObserverImpl.class);

        @Override
        protected void process(PlayerTerminateActionEvent event) {
            var player = event.getPlayer();
            var eventType = event.getType();

            try {
                var terminateGameCommand = new TerminateGameActionCommand(game, player, eventType);
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