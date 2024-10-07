package com.agutsul.chess.player.state;

import com.agutsul.chess.event.Observable;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;

public class ActivePlayerState extends AbstractPlayerState {

    private final Observable observable;

    public ActivePlayerState(Observable observable) {
        super(Type.ACTIVE);
        this.observable = observable;
    }

    @Override
    public void play(Player player) {
        observable.notifyObservers(new RequestPlayerActionEvent(player));
    }
}