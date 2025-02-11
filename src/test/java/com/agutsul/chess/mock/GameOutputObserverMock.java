package com.agutsul.chess.mock;

import java.util.Map;
import java.util.function.BiConsumer;

import com.agutsul.chess.activity.action.event.ActionCancelledEvent;
import com.agutsul.chess.activity.action.event.ActionCancellingEvent;
import com.agutsul.chess.activity.action.event.ActionExecutionEvent;
import com.agutsul.chess.activity.action.event.ActionPerformedEvent;
import com.agutsul.chess.activity.action.event.DefeatExecutionEvent;
import com.agutsul.chess.activity.action.event.DefeatPerformedEvent;
import com.agutsul.chess.activity.action.event.DrawExecutionEvent;
import com.agutsul.chess.activity.action.event.DrawPerformedEvent;
import com.agutsul.chess.activity.action.event.ExitExecutionEvent;
import com.agutsul.chess.activity.action.event.ExitPerformedEvent;
import com.agutsul.chess.activity.action.event.WinExecutionEvent;
import com.agutsul.chess.activity.action.event.WinPerformedEvent;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.event.BoardStateNotificationEvent;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.game.observer.AbstractGameObserver;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerDefeatActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerDrawActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerExitActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerWinActionExceptionEvent;

public class GameOutputObserverMock
        extends AbstractGameObserver {

    private final Map<Class<? extends Event>, BiConsumer<Game,Event>> consumers;

    public GameOutputObserverMock(Game game,
                                  Map<Class<? extends Event>, BiConsumer<Game,Event>> consumers) {
        super(game);
        this.consumers = consumers;
    }

    @Override
    protected void process(GameStartedEvent event) {
        consume(event);
    }

    @Override
    protected void process(GameOverEvent event) {
        consume(event);
    }

    @Override
    protected void process(BoardStateNotificationEvent event) {
        consume(event);
    }

    @Override
    protected void process(ActionPerformedEvent event) {
        consume(event);
    }

    @Override
    protected void process(ActionExecutionEvent event) {
        consume(event);
    }

    @Override
    protected void process(ActionCancelledEvent event) {
        consume(event);
    }

    @Override
    protected void process(ActionCancellingEvent event) {
        consume(event);
    }

    @Override
    protected void process(PlayerActionExceptionEvent event) {
        consume(event);
    }

    @Override
    protected void process(PlayerCancelActionExceptionEvent event) {
        consume(event);
    }

    @Override
    protected void process(PlayerDrawActionExceptionEvent event) {
        consume(event);
    }

    @Override
    protected void process(DrawExecutionEvent event) {
        consume(event);
    }

    @Override
    protected void process(DrawPerformedEvent event) {
        consume(event);
    }

    @Override
    protected void process(DefeatExecutionEvent event) {
        consume(event);
    }

    @Override
    protected void process(DefeatPerformedEvent event) {
        consume(event);
    }

    @Override
    protected void process(ExitExecutionEvent event) {
        consume(event);
    }

    @Override
    protected void process(ExitPerformedEvent event) {
        consume(event);
    }

    @Override
    protected void process(PlayerExitActionExceptionEvent event) {
        consume(event);
    }

    @Override
    protected void process(PlayerWinActionExceptionEvent event) {
        consume(event);
    }

    @Override
    protected void process(WinExecutionEvent event) {
        consume(event);
    }

    @Override
    protected void process(WinPerformedEvent event) {
        consume(event);
    }

    @Override
    protected void process(PlayerDefeatActionExceptionEvent event) {
        consume(event);
    }

    private void consume(Event event) {
        var consumer = consumers.get(event.getClass());
        if (consumer != null) {
            consumer.accept(this.game, event);
        }
    }
}