package com.agutsul.chess.game.observer;

import static java.util.Collections.unmodifiableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.agutsul.chess.activity.action.event.ActionCancelledEvent;
import com.agutsul.chess.activity.action.event.ActionCancellingEvent;
import com.agutsul.chess.activity.action.event.ActionExecutionEvent;
import com.agutsul.chess.activity.action.event.ActionPerformedEvent;
import com.agutsul.chess.activity.action.event.ActionTerminatedEvent;
import com.agutsul.chess.activity.action.event.ActionTerminationEvent;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.event.BoardStateNotificationEvent;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerTerminateActionExceptionEvent;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;
import com.agutsul.chess.player.event.RequestPromotionPieceTypeEvent;

public abstract class AbstractGameObserver
        implements Observer {

    private final Map<Class<? extends Event>, Consumer<Event>> processors;

    protected final Game game;

    public AbstractGameObserver(Game game) {
        this.game = game;
        this.processors = createEventProcessors();
    }

    @Override
    public final void observe(Event event) {
        var processor = this.processors.get(event.getClass());
        if (processor != null) {
            processor.accept(event);
        }
    }

    private Map<Class<? extends Event>, Consumer<Event>> createEventProcessors() {
        var processors = new HashMap<Class<? extends Event>, Consumer<Event>>();

        processors.put(GameStartedEvent.class,            event -> process((GameStartedEvent) event));
        processors.put(GameOverEvent.class,               event -> process((GameOverEvent) event));
        processors.put(BoardStateNotificationEvent.class, event -> process((BoardStateNotificationEvent) event));

        processors.put(RequestPlayerActionEvent.class,       event -> process((RequestPlayerActionEvent) event));
        processors.put(RequestPromotionPieceTypeEvent.class, event -> process((RequestPromotionPieceTypeEvent) event));

        processors.put(ActionExecutionEvent.class,   event -> process((ActionExecutionEvent) event));
        processors.put(ActionCancellingEvent.class,  event -> process((ActionCancellingEvent) event));

        processors.put(ActionTerminatedEvent.class,  event -> process((ActionTerminatedEvent) event));
        processors.put(ActionTerminationEvent.class, event -> process((ActionTerminationEvent) event));

        processors.put(ActionPerformedEvent.class,   event -> process((ActionPerformedEvent) event));
        processors.put(ActionCancelledEvent.class,   event -> process((ActionCancelledEvent) event));

        processors.put(PlayerActionExceptionEvent.class,          event -> process((PlayerActionExceptionEvent) event));
        processors.put(PlayerCancelActionExceptionEvent.class,    event -> process((PlayerCancelActionExceptionEvent) event));
        processors.put(PlayerTerminateActionExceptionEvent.class, event -> process((PlayerTerminateActionExceptionEvent) event));

        return unmodifiableMap(processors);
    }

    protected abstract void process(GameStartedEvent event);

    protected abstract void process(GameOverEvent event);

    protected abstract void process(BoardStateNotificationEvent event);

    protected abstract void process(RequestPlayerActionEvent event);

    protected abstract void process(RequestPromotionPieceTypeEvent event);

    protected abstract void process(ActionPerformedEvent event);

    protected abstract void process(ActionExecutionEvent event);

    protected abstract void process(ActionCancelledEvent event);

    protected abstract void process(ActionCancellingEvent event);

    protected abstract void process(PlayerActionExceptionEvent event);

    protected abstract void process(PlayerCancelActionExceptionEvent event);

    protected abstract void process(ActionTerminatedEvent event);

    protected abstract void process(ActionTerminationEvent event);

    protected abstract void process(PlayerTerminateActionExceptionEvent event);
}