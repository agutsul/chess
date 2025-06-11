package com.agutsul.chess.player.observer;
import static org.apache.commons.lang3.ThreadUtils.sleepQuietly;

import java.time.Duration;

import com.agutsul.chess.event.CompositeEventObserver;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;

public class PlayerActionObserver
        implements Observer {

    private final Observer observer;
    private final Game game;

    public PlayerActionObserver(Game game) {
        this.game = game;
        this.observer = new CompositeEventObserver(
                new PlayerActionObserverImpl(game, this),
                new PlayerCancelActionObserverImpl(game, this),
                new PlayerTerminateActionObserverImpl(game, this)
        );
    }

    @Override
    public final void observe(Event event) {
        this.observer.observe(event);
    }

    protected void notifyGameEvent(Event event) {
        // display error message to player
        ((Observable) this.game).notifyObservers(event);
        sleepQuietly(Duration.ofMillis(1));
    }

    protected void requestPlayerAction(Player player) {
        var board = this.game.getBoard();
        // re-ask player about new action
        ((Observable) board).notifyObservers(new RequestPlayerActionEvent(player));
    }
}