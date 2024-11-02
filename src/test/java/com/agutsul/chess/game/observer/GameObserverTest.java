package com.agutsul.chess.game.observer;

import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Map;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.event.ActionCancelledEvent;
import com.agutsul.chess.action.event.ActionCancellingEvent;
import com.agutsul.chess.action.event.ActionExecutionEvent;
import com.agutsul.chess.action.event.ActionPerformedEvent;
import com.agutsul.chess.action.memento.ActionMemento;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameStartedEvent;
import com.agutsul.chess.mock.GameOutputObserverMock;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionExceptionEvent;

@ExtendWith(MockitoExtension.class)
public class GameObserverTest {

    private static final String MESSAGE = "test";

    @Test
    void testObserveGameStartedEvent() {
        var game = mock(Game.class);
        var event = new GameStartedEvent(game);

        var observer = new GameOutputObserverMock(game,
                createMap(event, (gm, evt) -> {
                    assertEquals(game, gm);
                    assertEquals(event, evt);
                    assertEquals(game, ((GameStartedEvent) evt).getGame());
                })
        );

        observer.observe(event);
    }

    @Test
    void testObserveGameOverEvent() {
        var game = mock(Game.class);
        var event = new GameOverEvent(game);

        var observer = new GameOutputObserverMock(game,
                createMap(event, (gm, evt) -> {
                    assertEquals(game, gm);
                    assertEquals(event, evt);
                    assertEquals(game, ((GameOverEvent) evt).getGame());
                })
        );

        observer.observe(event);
    }

    @Test
    void testObserveActionPerformedEvent() {
        var memento = mock(ActionMemento.class);
        var event = new ActionPerformedEvent(memento);

        var game = mock(Game.class);
        var observer = new GameOutputObserverMock(game,
                createMap(event, (gm, evt) -> {
                    assertEquals(game, gm);
                    assertEquals(event, evt);
                    assertEquals(memento, ((ActionPerformedEvent) evt).getActionMemento());
                })
        );

        observer.observe(event);
    }

    @Test
    void testObserveActionExecutionEvent() {
        var action = mock(Action.class);
        var event = new ActionExecutionEvent(action);

        var game = mock(Game.class);
        var observer = new GameOutputObserverMock(game,
                createMap(event, (gm, evt) -> {
                    assertEquals(game, gm);
                    assertEquals(event, evt);
                    assertEquals(action, ((ActionExecutionEvent) evt).getAction());
                })
        );

        observer.observe(event);
    }

    @Test
    void testObserveActionCancelledEvent() {
        var event = new ActionCancelledEvent();

        var game = mock(Game.class);
        var observer = new GameOutputObserverMock(game,
                createMap(event, (gm, evt) -> {
                    assertEquals(game, gm);
                    assertEquals(event, evt);
                })
        );

        observer.observe(event);
    }

    @Test
    void testObserveActionCancellingEvent() {
        var action = mock(Action.class);
        var event = new ActionCancellingEvent(action);

        var game = mock(Game.class);
        var observer = new GameOutputObserverMock(game,
                createMap(event, (gm, evt) -> {
                    assertEquals(game, gm);
                    assertEquals(event, evt);
                    assertEquals(action, ((ActionCancellingEvent) evt).getAction());
                })
        );

        observer.observe(event);
    }

    @Test
    void testObservePlayerActionExceptionEvent() {
        var event = new PlayerActionExceptionEvent(MESSAGE);
        var game = mock(Game.class);

        var observer = new GameOutputObserverMock(game,
                createMap(event, (gm, evt) -> {
                    assertEquals(game, gm);
                    assertEquals(event, evt);
                    assertEquals(MESSAGE, ((PlayerActionExceptionEvent) evt).getMessage());
                })
        );

        observer.observe(event);
    }

    @Test
    void testObservePlayerCancelActionExceptionEvent() {
        var event = new PlayerCancelActionExceptionEvent(MESSAGE);
        var game = mock(Game.class);

        var observer = new GameOutputObserverMock(game,
                createMap(event, (gm, evt) -> {
                    assertEquals(game, gm);
                    assertEquals(event, evt);
                    assertEquals(MESSAGE, ((PlayerCancelActionExceptionEvent) evt).getMessage());
                })
        );

        observer.observe(event);
    }

    private static Map<Class<? extends Event>, BiConsumer<Game,Event>>
            createMap(Event event, BiConsumer<Game,Event> consumer) {

        return singletonMap(event.getClass(), consumer);
    }
}