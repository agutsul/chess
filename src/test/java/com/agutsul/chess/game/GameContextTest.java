package com.agutsul.chess.game;

import static com.agutsul.chess.timeout.TimeoutFactory.createActionTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createGameTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createIncrementalTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createMixedTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createUnknownTimeout;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.timeout.ActionTimeout;
import com.agutsul.chess.timeout.GameTimeout;
import com.agutsul.chess.timeout.IncrementalTimeout;
import com.agutsul.chess.timeout.MixedTimeout;
import com.agutsul.chess.timeout.Timeout.Type;

@ExtendWith(MockitoExtension.class)
public class GameContextTest {

    private static final int ACTIONS_COUNTER = 20;

    // milliseconds
    private static final long TEN_MINUTES = 10 * 60 * 1000;
    private static final long ONE_MINUTE  = 60 * 1000;
    private static final long TIMEOUT = 15 * 1000;

    @AutoClose
    GameContext context = new GameContext();

    @Test
    void testContextWihtoutAnyTimeout() throws IOException {
        assertEquals(Optional.empty(), context.getTimeout());
        assertEquals(Optional.empty(), context.getActionTimeout());
        assertEquals(Optional.empty(), context.getGameTimeout());
        assertEquals(Optional.empty(), context.getExtraActionTimeout());
        assertEquals(Optional.empty(), context.getExpectedActions());
    }

    @Test
    void testEmptyForUnknownTimeout() throws IOException {
        context.setTimeout(createUnknownTimeout());

        var optionalTimeout = context.getTimeout();
        assertTrue(optionalTimeout.isPresent());

        var timeout = optionalTimeout.get();
        assertEquals(Type.UNKNOWN, timeout.getType());
        assertEquals(Optional.empty(), timeout.getDuration());

        assertEquals(Optional.empty(), context.getActionTimeout());
        assertEquals(Optional.empty(), context.getGameTimeout());
        assertEquals(Optional.empty(), context.getExtraActionTimeout());
        assertEquals(Optional.empty(), context.getExpectedActions());
    }

    @Test
    void testActionTimeoutDurationForSandclock() throws IOException {
        context.setTimeout(createActionTimeout(TEN_MINUTES));

        var optionalTimeout = context.getTimeout();
        assertTrue(optionalTimeout.isPresent());

        var timeout = optionalTimeout.get();
        assertEquals(Type.SANDCLOCK, timeout.getType());

        var actionTimeout  = (ActionTimeout) timeout;
        var actionDuration = actionTimeout.getActionDuration();
        assertEquals(TEN_MINUTES, actionDuration.toMillis());

        var durationMillis = context.getActionTimeout();
        assertEquals(TEN_MINUTES, durationMillis.get());
    }

    @Test
    void testGameTimeoutDurationForGeneric() throws IOException {
        context.setTimeout(createGameTimeout(TEN_MINUTES));

        var optionalTimeout = context.getTimeout();
        assertTrue(optionalTimeout.isPresent());

        var timeout = optionalTimeout.get();
        assertEquals(Type.GENERIC, timeout.getType());

        var gameTimeout  = (GameTimeout) timeout;
        var gameDuration = gameTimeout.getGameDuration();
        assertEquals(TEN_MINUTES, gameDuration.toMillis());

        var durationMillis = context.getGameTimeout();
        assertEquals(TEN_MINUTES, durationMillis.get());
    }

    @Test
    void testMixedTimeout() throws IOException {
        context.setTimeout(createMixedTimeout(TEN_MINUTES, ACTIONS_COUNTER));

        var optionalTimeout = context.getTimeout();
        assertTrue(optionalTimeout.isPresent());

        var timeout = optionalTimeout.get();
        assertEquals(Type.ACTIONS_PER_PERIOD, timeout.getType());
        assertTrue(timeout.isType(Type.SANDCLOCK));
        assertTrue(timeout.isType(Type.GENERIC));

        var gameTimeout  = (GameTimeout) timeout;
        var gameDuration = gameTimeout.getGameDuration();
        assertEquals(TEN_MINUTES, gameDuration.toMillis());

        var actionTimeout  = (ActionTimeout) timeout;
        var actionDuration = actionTimeout.getActionDuration();
        assertEquals(TIMEOUT, actionDuration.toMillis());

        var totalActions = context.getExpectedActions();
        assertTrue(totalActions.isPresent());
        assertEquals(ACTIONS_COUNTER, totalActions.get());
    }

    @Test
    void testActionTimeoutDurationForIncrementalAction() throws IOException {
        var actionTimeout = createActionTimeout(TEN_MINUTES);
        context.setTimeout(createIncrementalTimeout(actionTimeout, ONE_MINUTE));

        var optionalTimeout = context.getTimeout();
        assertTrue(optionalTimeout.isPresent());

        var timeout = optionalTimeout.get();
        assertEquals(Type.INCREMENTAL, timeout.getType());
        assertTrue(timeout.isType(Type.SANDCLOCK));

        var incrementalTimeout = (IncrementalTimeout<?>) timeout;

        var originTimeout = incrementalTimeout.getTimeout();
        assertEquals(actionTimeout, originTimeout);

        var timeoutDuration = originTimeout.getDuration();
        assertTrue(timeoutDuration.isPresent());
        assertEquals(TEN_MINUTES, timeoutDuration.get().toMillis());

        var extraDuration = incrementalTimeout.getExtraDuration();
        assertNotNull(extraDuration);
        assertEquals(ONE_MINUTE, extraDuration.toMillis());

        var durationMillis = context.getExtraActionTimeout();
        assertEquals(ONE_MINUTE, durationMillis.get());

        var timeoutMillis = context.getActionTimeout();
        assertTrue(timeoutMillis.isPresent());
        assertEquals(TEN_MINUTES, timeoutMillis.get());
    }

    @Test
    void testActionTimeoutForIncrementalActionsPerPeriod() throws IOException {
        var actionsPerPeriod = createMixedTimeout(TEN_MINUTES, ACTIONS_COUNTER);
        context.setTimeout(createIncrementalTimeout(actionsPerPeriod, ONE_MINUTE));

        var optionalTimeout = context.getTimeout();
        assertTrue(optionalTimeout.isPresent());

        var timeout = optionalTimeout.get();
        assertEquals(Type.INCREMENTAL, timeout.getType());
        assertTrue(timeout.isType(Type.ACTIONS_PER_PERIOD));
        assertTrue(timeout.isType(Type.SANDCLOCK));
        assertTrue(timeout.isType(Type.GENERIC));

        var incrementalTimeout = (IncrementalTimeout<?>) timeout;

        var originTimeout = incrementalTimeout.getTimeout();
        assertEquals(actionsPerPeriod, originTimeout);
        assertEquals(ACTIONS_COUNTER, ((MixedTimeout) originTimeout).getActionsCounter());

        var actionDuration = ((ActionTimeout) originTimeout).getActionDuration();
        assertEquals(TIMEOUT, actionDuration.toMillis());

        var timeoutDuration = originTimeout.getDuration();
        assertTrue(timeoutDuration.isPresent());
        assertEquals(TEN_MINUTES, timeoutDuration.get().toMillis());

        var extraDuration = incrementalTimeout.getExtraDuration();
        assertNotNull(extraDuration);
        assertEquals(ONE_MINUTE, extraDuration.toMillis());

        var durationMillis = context.getExtraActionTimeout();
        assertEquals(ONE_MINUTE, durationMillis.get());
    }

    @Test
    void testGameTimeoutDurationForIncrementalGame() throws IOException {
        var gameTimeout = createGameTimeout(TEN_MINUTES);
        context.setTimeout(createIncrementalTimeout(gameTimeout, ONE_MINUTE));

        var optionalTimeout = context.getTimeout();
        assertTrue(optionalTimeout.isPresent());

        var timeout = optionalTimeout.get();
        assertEquals(Type.INCREMENTAL, timeout.getType());
        assertTrue(timeout.isType(Type.GENERIC));

        var incrementalTimeout = (IncrementalTimeout<?>) timeout;

        var originTimeout = incrementalTimeout.getTimeout();
        assertEquals(gameTimeout, originTimeout);

        var timeoutDuration = originTimeout.getDuration();
        assertTrue(timeoutDuration.isPresent());
        assertEquals(TEN_MINUTES, timeoutDuration.get().toMillis());

        var extraDuration = incrementalTimeout.getExtraDuration();
        assertNotNull(extraDuration);
        assertEquals(ONE_MINUTE, extraDuration.toMillis());

        var durationMillis = context.getExtraActionTimeout();
        assertEquals(ONE_MINUTE, durationMillis.get());
    }
}