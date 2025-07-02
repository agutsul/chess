package com.agutsul.chess.game.pgn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.timeout.ActionTimeout;
import com.agutsul.chess.timeout.ActionsGameTimeout;
import com.agutsul.chess.timeout.CompositeTimeout;
import com.agutsul.chess.timeout.GameTimeout;
import com.agutsul.chess.timeout.IncrementalTimeout;
import com.agutsul.chess.timeout.Timeout;
import com.agutsul.chess.timeout.UnknownTimeout;

@ExtendWith(MockitoExtension.class)
public class PgnTimeControlTest {

    @ParameterizedTest(name = "{index}. testBlankPgnTimeControl({0})")
    @ValueSource(strings = { "", " ", "  " })
    void testBlankTimeControl(String timeControl) {
        assertEquals(Optional.empty(), PgnTimeControl.timeoutOf(timeControl));
    }

    @Test
    void testNullTimeControl() {
        assertEquals(Optional.empty(), PgnTimeControl.timeoutOf(null));
    }

    @ParameterizedTest(name = "{index}. testUnknownTimeControl({0})")
    @ValueSource(strings = { "?", "?:-", "?:-:" })
    void testUnknownTimeControl(String timeControl) {
        var parsedTimeout = PgnTimeControl.timeoutOf(timeControl);
        assertTrue(parsedTimeout.isPresent());

        var timeout = parsedTimeout.get();
        assertTrue(timeout.isType(Timeout.Type.UNKNOWN));
        assertTrue(timeout instanceof UnknownTimeout);
        assertEquals(Optional.empty(), timeout.getDuration());
    }

    @ParameterizedTest(name = "{index}. testInvalidUnknownTimeControl({0})")
    @ValueSource(strings = { "??", "?!", "!?" })
    void testInvalidUnknownTimeControl(String timeControl) {
        assertEquals(Optional.empty(), PgnTimeControl.timeoutOf(timeControl));
    }

    @ParameterizedTest(name = "{index}. testNoTimeoutTimeControl({0})")
    @ValueSource(strings = { "-", "--", "=-" })
    void testNoTimeoutTimeControl(String timeControl) {
        assertEquals(Optional.empty(), PgnTimeControl.timeoutOf(timeControl));
    }

    @Test
    void testGeneralTimeControl() {
        var parsedTimeout = PgnTimeControl.timeoutOf("60");
        assertTrue(parsedTimeout.isPresent());

        var timeout = parsedTimeout.get();
        assertTrue(timeout.isType(Timeout.Type.GENERIC));
        assertTrue(timeout instanceof GameTimeout);

        var duration = timeout.getDuration();
        assertTrue(duration.isPresent());
        assertEquals(60000L, duration.get().toMillis());
    }

    @ParameterizedTest(name = "{index}. testInvalidGeneralTimeControl({0})")
    @ValueSource(strings = { "12 3", "-123", "12.3" })
    void testInvalidGeneralTimeControl(String timeControl) {
        assertEquals(Optional.empty(), PgnTimeControl.timeoutOf(timeControl));
    }

    @ParameterizedTest(name = "{index}. testIncrementalTimeControl({0})")
    @ValueSource(strings = { "*180+60", "40/9000+60", "4500+60" })
    void testIncrementalTimeControl(String timeControl) {
        var parsedTimeout = PgnTimeControl.timeoutOf(timeControl);
        assertTrue(parsedTimeout.isPresent());

        var timeout = parsedTimeout.get();
        assertTrue(timeout.getDuration().isPresent());

        assertTrue(timeout.isType(Timeout.Type.INCREMENTAL));
        assertTrue(timeout instanceof IncrementalTimeout);

        var incrementalTimeout = (IncrementalTimeout) timeout;
        assertEquals(60000L, incrementalTimeout.getExtraDuration().toMillis());

        var originTimeout = incrementalTimeout.getTimeout();
        assertTrue(originTimeout instanceof ActionTimeout || originTimeout instanceof GameTimeout);

        assertTrue(originTimeout.isAnyType(Timeout.Type.SANDCLOCK,
                Timeout.Type.GENERIC, Timeout.Type.ACTIONS_PER_PERIOD
        ));
    }

    @ParameterizedTest(name = "{index}. testInvalidIncrementalTimeControl({0})")
    @ValueSource(strings = { "*180++60", "40/9000+60+", "+4500+60" })
    void testInvalidIncrementalTimeControl(String timeControl) {
        assertEquals(Optional.empty(), PgnTimeControl.timeoutOf(timeControl));
    }

    @Test
    void testSandclockTimeControl() {
        var parsedTimeout = PgnTimeControl.timeoutOf("*60");
        assertTrue(parsedTimeout.isPresent());

        var timeout = parsedTimeout.get();
        assertTrue(timeout.isType(Timeout.Type.SANDCLOCK));
        assertTrue(timeout instanceof ActionTimeout);

        var duration = timeout.getDuration();
        assertTrue(duration.isPresent());
        assertEquals(60000L, duration.get().toMillis());
    }

    @ParameterizedTest(name = "{index}. testInvalidSandclockTimeControl({0})")
    @ValueSource(strings = { "**180", "*180*", "180*" })
    void testInvalidSandclockTimeControl(String timeControl) {
        assertEquals(Optional.empty(), PgnTimeControl.timeoutOf(timeControl));
    }

    @Test
    void testActionsPerPeriodTimeControl() {
        var parsedTimeout = PgnTimeControl.timeoutOf("10/300");
        assertTrue(parsedTimeout.isPresent());

        var timeout = parsedTimeout.get();

        var duration = timeout.getDuration();
        assertTrue(duration.isPresent());
        assertEquals(300000L, duration.get().toMillis());

        assertTrue(timeout.isType(Timeout.Type.SANDCLOCK));
        assertTrue(timeout instanceof ActionTimeout);

        var actionTimeout = (ActionTimeout) timeout;
        assertEquals(15000L, actionTimeout.getActionDuration().toMillis());

        assertTrue(timeout.isType(Timeout.Type.GENERIC));
        assertTrue(timeout instanceof GameTimeout);

        var gameTimeout = (GameTimeout) timeout;
        assertEquals(300000L, gameTimeout.getGameDuration().toMillis());

        assertTrue(timeout.isType(Timeout.Type.ACTIONS_PER_PERIOD));
        assertTrue(timeout instanceof ActionsGameTimeout);

        var actionsGameTimeout = (ActionsGameTimeout) timeout;
        assertEquals(10, actionsGameTimeout.getActionsCounter());
    }

    @ParameterizedTest(name = "{index}. testInvalidActionsPerPeriodTimeControl({0})")
    @ValueSource(strings = { "10//300", "/10", "10/", "ab/cd", "ab/10", "10/ab" })
    void testInvalidActionsPerPeriodTimeControl(String timeControl) {
        assertEquals(Optional.empty(), PgnTimeControl.timeoutOf(timeControl));
    }

    @ParameterizedTest(name = "{index}. testCompositeTimeControl({0})")
    @ValueSource(strings = { "40/5400+30:1800+30", "40/5400:*300", "5400:*300", "5400+30:*300", "5400:*300+30" })
    void testCompositeTimeControl(String timeControl) {
        var optionalTimeout = PgnTimeControl.timeoutOf(timeControl);
        assertTrue(optionalTimeout.isPresent());

        var timeout = optionalTimeout.get();
        assertTrue(timeout instanceof CompositeTimeout);

        assertTrue(timeout.isAnyType(Timeout.Type.SANDCLOCK, Timeout.Type.GENERIC,
                Timeout.Type.ACTIONS_PER_PERIOD, Timeout.Type.INCREMENTAL
        ));
    }

    @Test
    void testEmptyCompositeTimeoutList() {
        var message = "Unable to create composite timeout";

        var timeouts = new ArrayList<Timeout>();
        var thrown = assertThrows(IllegalStateException.class, () -> {
            new CompositeTimeout(timeouts);
        });

        assertEquals(message, thrown.getMessage());

        timeouts.add(null);
        timeouts.add(null);

        var thrown2 = assertThrows(IllegalStateException.class, () -> {
            new CompositeTimeout(timeouts);
        });

        assertEquals(message, thrown2.getMessage());
    }

    @ParameterizedTest(name = "{index}. testInvalidCompositeTimeControl({0})")
    @ValueSource(strings = { "-:-", "::" })
    void testInvalidCompositeTimeControl(String timeControl) {
        var optionalTimeout = PgnTimeControl.timeoutOf(timeControl);
        assertTrue(optionalTimeout.isEmpty());
    }
}