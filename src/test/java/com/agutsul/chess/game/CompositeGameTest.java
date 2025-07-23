package com.agutsul.chess.game;

import static com.agutsul.chess.timeout.TimeoutFactory.createActionTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createGameTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createMixedTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createUnknownTimeout;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.game.observer.GameTimeoutTerminationObserver;
import com.agutsul.chess.game.state.DefaultGameState;
import com.agutsul.chess.game.state.DrawnGameState;
import com.agutsul.chess.game.state.WhiteWinGameState;
import com.agutsul.chess.timeout.CompositeTimeout;
import com.agutsul.chess.timeout.Timeout;

@ExtendWith(MockitoExtension.class)
public class CompositeGameTest {

    @Mock
    AbstractPlayableGame game;

    @Mock
    GameContext gameContext;

    @Test
    void testDefaultGameWithActionTimeout() {
        when(game.getState())
            .thenReturn(new DefaultGameState());
        when(game.getContext())
            .thenReturn(gameContext);

        var compositeGame = new CompositeGame<>(game,
                new CompositeTimeout(List.of(createActionTimeout(100L)))
        );

        compositeGame.addObserver(new GameTimeoutTerminationObserver());
        compositeGame.run();

        verify(game, times(1)).run();
    }

    @Test
    void testDefaultGameWithUnknownTimeout() {
        when(game.getState())
            .thenReturn(new DefaultGameState());
        when(game.getContext())
            .thenReturn(gameContext);

        var compositeGame = new CompositeGame<>(game,
                new CompositeTimeout(List.of(createUnknownTimeout()))
        );

        compositeGame.addObserver(new GameTimeoutTerminationObserver());
        compositeGame.run();

        verify(game, times(1)).run();
    }

    @Test
    void testDefaultGameWithoutAnyTimeout() {
        List<Timeout> timeouts = emptyList();

        var thrown = assertThrows(
                IllegalStateException.class,
                () -> new CompositeGame<>(game, new CompositeTimeout(timeouts)).run()
        );

        assertEquals("Unable to create composite timeout", thrown.getMessage());

        verify(game, never()).run();
    }

    @Test
    void testTimeoutGameWithGenericTimeout() {
        when(game.getState())
            .thenReturn(new WhiteWinGameState());
        when(game.getContext())
            .thenReturn(gameContext);

        var compositeGame = new CompositeGame<>(game,
                new CompositeTimeout(List.of(createGameTimeout(100)))
        );

        compositeGame.addObserver(new GameTimeoutTerminationObserver());
        compositeGame.run();

        verify(game, times(1)).run();
    }

    @Test
    void testTimeoutGameWithMixedTimeout() {
        when(game.getState())
            .thenReturn(new DrawnGameState());
        when(game.getContext())
            .thenReturn(gameContext);

        var compositeGame = new CompositeGame<>(game,
                new CompositeTimeout(List.of(createMixedTimeout(100L, 2)))
        );

        compositeGame.addObserver(new GameTimeoutTerminationObserver());
        compositeGame.run();

        verify(game, times(1)).run();
    }

    @Test
    void testTimeoutGameWithMultipleMixedTimeouts() {
        var counter = new AtomicInteger();
        counter.set(0);

        when(game.getState())
            .thenAnswer(inv -> {
                var invocations = counter.get();
                var state = invocations == 0
                        ? new DefaultGameState()
                        : new WhiteWinGameState();

                counter.set(invocations++);
                return state;
            });

        when(game.getContext())
            .thenReturn(gameContext);

        var timeout = new CompositeTimeout(
                createMixedTimeout(100L, 2),
                createMixedTimeout(400L, 4)
        );

        var compositeGame = new CompositeGame<>(game, timeout);
        compositeGame.addObserver(new GameTimeoutTerminationObserver());
        compositeGame.run();

        verify(game, times(2)).run();
    }
}