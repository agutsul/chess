package com.agutsul.chess.mock;

import java.util.Map;
import java.util.function.BiConsumer;

import com.agutsul.chess.activity.action.event.ActionCancelledEvent;
import com.agutsul.chess.activity.action.event.ActionCancellingEvent;
import com.agutsul.chess.activity.action.event.ActionExecutionEvent;
import com.agutsul.chess.activity.action.event.ActionPerformedEvent;
import com.agutsul.chess.activity.action.event.ActionTerminatedEvent;
import com.agutsul.chess.activity.action.event.ActionTerminationEvent;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.event.BoardStateNotificationEvent;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.game.observer.AbstractGameObserver;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerTerminateActionExceptionEvent;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;
import com.agutsul.chess.player.event.RequestPromotionPieceTypeEvent;

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
    protected void process(RequestPlayerActionEvent event) {
        consume(event);
    }

    @Override
    protected void process(RequestPromotionPieceTypeEvent event) {
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
    protected void process(ActionTerminatedEvent event) {
        consume(event);
    }

    @Override
    protected void process(ActionTerminationEvent event) {
        consume(event);
    }

    @Override
    protected void process(PlayerTerminateActionExceptionEvent event) {
        consume(event);
    }

    private void consume(Event event) {
        var consumer = consumers.get(event.getClass());
        if (consumer != null) {
            consumer.accept(this.game, event);
        }
    }
}