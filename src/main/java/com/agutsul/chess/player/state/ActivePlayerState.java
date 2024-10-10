package com.agutsul.chess.player.state;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.event.Observable;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;

public class ActivePlayerState
        extends AbstractPlayerState {

    private static final Logger LOGGER = getLogger(ActivePlayerState.class);

    private final Observable observable;

    public ActivePlayerState(Observable observable) {
        super(Type.ACTIVE);
        this.observable = observable;
    }

    @Override
    public void play(Player player) {
        LOGGER.info("Request player '{}' action", player);
        observable.notifyObservers(new RequestPlayerActionEvent(player));
    }
}