package com.agutsul.chess.player;

import static org.slf4j.LoggerFactory.getLogger;

import java.time.Duration;

import org.apache.commons.lang3.ThreadUtils;
import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.command.PerformActionCommand;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.player.event.PlayerActionEvent;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;

public class PlayerEventOberver
        implements Observer {

    private static final Logger LOGGER = getLogger(PlayerEventOberver.class);

    private final Observable observable;

    public PlayerEventOberver(Observable observable) {
        this.observable = observable;
    }

    @Override
    public void observe(Event event) {
        if (event instanceof PlayerActionEvent) {
            process((PlayerActionEvent) event);
        }
    }

    private void process(PlayerActionEvent event) {
        try {
            var command = new PerformActionCommand(event.getBoard(), observable);
            command.setSource(event.getSource());
            command.setTarget(event.getTarget());

            command.execute();
        } catch (Exception e) {
            LOGGER.error("Player action exception", e);
            // display error message to player
            observable.notifyObservers(new PlayerActionExceptionEvent(e.getMessage()));

            ThreadUtils.sleepQuietly(Duration.ofMillis(1));

            // re-ask player about new action
            requestPlayerAction(event.getBoard(), event.getPlayer());
        }
    }

    private void requestPlayerAction(Board board, Player player) {
        board.notifyObservers(new RequestPlayerActionEvent(player));
    }
}