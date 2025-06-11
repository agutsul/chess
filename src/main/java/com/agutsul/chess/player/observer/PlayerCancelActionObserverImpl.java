package com.agutsul.chess.player.observer;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.command.CancelActionCommand;
import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.exception.IllegalPositionException;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.event.PlayerCancelActionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionExceptionEvent;

final class PlayerCancelActionObserverImpl
        extends AbstractEventObserver<PlayerCancelActionEvent> {

    private static final Logger LOGGER = getLogger(PlayerCancelActionObserverImpl.class);

    private final PlayerActionObserver playerActionObserver;
    private final Game game;

    PlayerCancelActionObserverImpl(Game game, PlayerActionObserver playerActionObserver) {
        this.playerActionObserver = playerActionObserver;
        this.game = game;
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

            playerActionObserver.requestPlayerAction(player);
        } catch (IllegalActionException | IllegalPositionException e) {
            LOGGER.error(String.format("Player '%s' cancel action exception", player.getName()), e);

            playerActionObserver.notifyGameEvent(
                    new PlayerCancelActionExceptionEvent(e.getMessage())
            );

            playerActionObserver.requestPlayerAction(player);
        }
    }
}