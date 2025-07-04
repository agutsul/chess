package com.agutsul.chess.game.observer;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.command.TerminateGameActionCommand;
import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.game.event.GameExceptionEvent;
import com.agutsul.chess.game.event.GameTimeoutTerminationEvent;

public final class GameTimeoutTerminationObserver
        extends AbstractEventObserver<GameTimeoutTerminationEvent> {

    private static final Logger LOGGER = getLogger(GameTimeoutTerminationObserver.class);

    @Override
    protected void process(GameTimeoutTerminationEvent event) {
        var game = event.getGame();
        try {
            var command = new TerminateGameActionCommand(game, game.getCurrentPlayer(), event.getType());
            command.execute();
        } catch (Throwable throwable) {
            LOGGER.error(String.format("Game termination(%s) exception", event.getType()), throwable);
            ((Observable) game).notifyObservers(new GameExceptionEvent(game, throwable));
        }
    }
}