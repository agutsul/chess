package com.agutsul.chess.player.observer;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.command.TerminateGameActionCommand;
import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.event.PlayerTerminateActionEvent;
import com.agutsul.chess.player.event.PlayerTerminateActionExceptionEvent;

final class PlayerTerminateActionObserverImpl
        extends AbstractEventObserver<PlayerTerminateActionEvent> {

    private static final Logger LOGGER = getLogger(PlayerTerminateActionObserverImpl.class);

    private final PlayerActionObserver playerActionObserver;
    private final Game game;

    PlayerTerminateActionObserverImpl(Game game, PlayerActionObserver playerActionObserver) {
        this.playerActionObserver = playerActionObserver;
        this.game = game;
    }

    @Override
    protected void process(PlayerTerminateActionEvent event) {
        var player = event.getPlayer();
        var eventType = event.getType();

        try {
            var terminateGameCommand = new TerminateGameActionCommand(this.game, player, eventType);
            terminateGameCommand.execute();
        } catch (IllegalActionException e) {
            LOGGER.error(String.format("Player '%s' termination(%s) exception", player.getName(), eventType),
                    e
            );

            playerActionObserver.notifyGameEvent(
                    new PlayerTerminateActionExceptionEvent(player, e.getMessage(), eventType)
            );
            playerActionObserver.requestPlayerAction(player);
        }
    }
}