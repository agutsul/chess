package com.agutsul.chess.game.observer;

import static java.util.Collections.unmodifiableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

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
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.event.BoardStateNotificationEvent;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerDefeatActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerDrawActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerExitActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerWinActionExceptionEvent;

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

        processors.put(GameStartedEvent.class,      event -> process((GameStartedEvent) event));
        processors.put(GameOverEvent.class,         event -> process((GameOverEvent) event));
        processors.put(BoardStateNotificationEvent.class, event -> process((BoardStateNotificationEvent) event));

        processors.put(ActionExecutionEvent.class,  event -> process((ActionExecutionEvent) event));
        processors.put(ActionCancellingEvent.class, event -> process((ActionCancellingEvent) event));
        processors.put(DrawExecutionEvent.class,    event -> process((DrawExecutionEvent) event));
        processors.put(DefeatExecutionEvent.class,  event -> process((DefeatExecutionEvent) event));
        processors.put(WinExecutionEvent.class,     event -> process((WinExecutionEvent) event));
        processors.put(ExitExecutionEvent.class,    event -> process((ExitExecutionEvent) event));

        processors.put(ActionPerformedEvent.class,  event -> process((ActionPerformedEvent) event));
        processors.put(ActionCancelledEvent.class,  event -> process((ActionCancelledEvent) event));
        processors.put(DrawPerformedEvent.class,    event -> process((DrawPerformedEvent) event));
        processors.put(DefeatPerformedEvent.class,  event -> process((DefeatPerformedEvent) event));
        processors.put(WinPerformedEvent.class,     event -> process((WinPerformedEvent) event));
        processors.put(ExitPerformedEvent.class,    event -> process((ExitPerformedEvent) event));

        processors.put(PlayerActionExceptionEvent.class,       event -> process((PlayerActionExceptionEvent) event));
        processors.put(PlayerCancelActionExceptionEvent.class, event -> process((PlayerCancelActionExceptionEvent) event));
        processors.put(PlayerDrawActionExceptionEvent.class,   event -> process((PlayerDrawActionExceptionEvent) event));
        processors.put(PlayerDefeatActionExceptionEvent.class, event -> process((PlayerDefeatActionExceptionEvent) event));
        processors.put(PlayerWinActionExceptionEvent.class,    event -> process((PlayerWinActionExceptionEvent) event));
        processors.put(PlayerExitActionExceptionEvent.class,   event -> process((PlayerExitActionExceptionEvent) event));

        return unmodifiableMap(processors);
    }

    protected abstract void process(GameStartedEvent event);

    protected abstract void process(GameOverEvent event);

    protected abstract void process(BoardStateNotificationEvent event);

    protected abstract void process(ActionPerformedEvent event);

    protected abstract void process(ActionExecutionEvent event);

    protected abstract void process(ActionCancelledEvent event);

    protected abstract void process(ActionCancellingEvent event);

    protected abstract void process(PlayerActionExceptionEvent event);

    protected abstract void process(PlayerCancelActionExceptionEvent event);

    protected abstract void process(PlayerDrawActionExceptionEvent event);

    protected abstract void process(PlayerDefeatActionExceptionEvent event);

    protected abstract void process(PlayerWinActionExceptionEvent event);

    protected abstract void process(WinExecutionEvent event);

    protected abstract void process(WinPerformedEvent event);

    protected abstract void process(DrawExecutionEvent event);

    protected abstract void process(DrawPerformedEvent event);

    protected abstract void process(DefeatExecutionEvent event);

    protected abstract void process(DefeatPerformedEvent event);

    protected abstract void process(ExitExecutionEvent event);

    protected abstract void process(ExitPerformedEvent event);

    protected abstract void process(PlayerExitActionExceptionEvent event);
}