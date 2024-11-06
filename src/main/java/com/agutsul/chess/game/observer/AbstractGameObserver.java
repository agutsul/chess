package com.agutsul.chess.game.observer;

import com.agutsul.chess.action.event.ActionCancelledEvent;
import com.agutsul.chess.action.event.ActionCancellingEvent;
import com.agutsul.chess.action.event.ActionExecutionEvent;
import com.agutsul.chess.action.event.ActionPerformedEvent;
import com.agutsul.chess.action.event.DrawExecutionEvent;
import com.agutsul.chess.action.event.DrawPerformedEvent;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerDrawActionExceptionEvent;

public abstract class AbstractGameObserver
        implements Observer {

    protected final Game game;

    public AbstractGameObserver(Game game) {
        this.game = game;
    }

    @Override
    public final void observe(Event event) {
        if (event instanceof GameStartedEvent) {
            process((GameStartedEvent) event);
        } else if (event instanceof GameOverEvent) {
            process((GameOverEvent) event);
        } else if (event instanceof ActionPerformedEvent) {
            process((ActionPerformedEvent) event);
        } else if (event instanceof ActionExecutionEvent) {
            process((ActionExecutionEvent) event);
        } else if (event instanceof PlayerActionExceptionEvent) {
            process((PlayerActionExceptionEvent) event);
        } else if (event instanceof PlayerCancelActionExceptionEvent) {
            process((PlayerCancelActionExceptionEvent) event);
        } else if (event instanceof ActionCancelledEvent) {
            process((ActionCancelledEvent) event);
        } else if (event instanceof ActionCancellingEvent) {
            process((ActionCancellingEvent) event);
        } else if (event instanceof PlayerDrawActionExceptionEvent) {
            process((PlayerDrawActionExceptionEvent) event);
        } else if (event instanceof DrawExecutionEvent) {
            process((DrawExecutionEvent) event);
        } else if (event instanceof DrawPerformedEvent) {
            process((DrawPerformedEvent) event);
        }
    }

    protected abstract void process(GameStartedEvent event);

    protected abstract void process(GameOverEvent event);

    protected abstract void process(ActionPerformedEvent event);

    protected abstract void process(ActionExecutionEvent event);

    protected abstract void process(ActionCancelledEvent event);

    protected abstract void process(ActionCancellingEvent event);

    protected abstract void process(PlayerActionExceptionEvent event);

    protected abstract void process(PlayerCancelActionExceptionEvent event);

    protected abstract void process(PlayerDrawActionExceptionEvent event);

    protected abstract void process(DrawExecutionEvent event);

    protected abstract void process(DrawPerformedEvent event);
}