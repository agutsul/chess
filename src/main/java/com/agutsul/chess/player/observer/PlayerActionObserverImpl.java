package com.agutsul.chess.player.observer;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.command.PerformActionCommand;
import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.exception.IllegalPositionException;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.event.PlayerActionEvent;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;

final class PlayerActionObserverImpl
        extends AbstractEventObserver<PlayerActionEvent> {

    private static final Logger LOGGER = getLogger(PlayerActionObserverImpl.class);

    private final PlayerActionObserver playerActionObserver;
    private final Game game;

    PlayerActionObserverImpl(Game game, PlayerActionObserver playerActionObserver) {
        this.playerActionObserver = playerActionObserver;
        this.game = game;
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

            playerActionObserver.notifyGameEvent(new PlayerActionExceptionEvent(e.getMessage()));
            playerActionObserver.requestPlayerAction(player);
        }
    }
}