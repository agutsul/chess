package com.agutsul.chess.player.observer;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;
import com.agutsul.chess.player.state.PlayerState;

public class PlayableObserver
        extends AbstractEventObserver<RequestPlayerActionEvent> {

    private static final Logger LOGGER = getLogger(PlayableObserver.class);

    private final Board board;

    public PlayableObserver(Board board) {
        this.board = board;
    }

    @Override
    protected void process(RequestPlayerActionEvent event) {
        var player = event.getPlayer();

        LOGGER.info("Request player '{}' action", player);

        if (isActive(player.getState())) {
            ((Observable) this.board).notifyObservers(event);
        }
    }

    private static boolean isActive(PlayerState playerState) {
        return PlayerState.Type.ACTIVE.equals(playerState.getType());
    }
}