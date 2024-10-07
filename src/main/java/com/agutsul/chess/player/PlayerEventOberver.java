package com.agutsul.chess.player;

import com.agutsul.chess.command.PerformActionCommand;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.player.event.PlayerActionEvent;

public class PlayerEventOberver implements Observer {

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
        var command = new PerformActionCommand(event.getBoard(), observable);
        command.setSource(event.getSource());
        command.setTarget(event.getTarget());

        command.execute();
    }
}