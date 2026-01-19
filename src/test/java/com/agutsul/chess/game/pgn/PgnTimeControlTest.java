package com.agutsul.chess.game.pgn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.timeout.ActionTimeout;
import com.agutsul.chess.timeout.CompositeTimeout;
import com.agutsul.chess.timeout.GameTimeout;
import com.agutsul.chess.timeout.IncrementalTimeout;
import com.agutsul.chess.timeout.MixedTimeout;
import com.agutsul.chess.timeout.Timeout;
import com.agutsul.chess.timeout.UnknownTimeout;

@ExtendWith(MockitoExtension.class)
public class PgnTimeControlTest {

    @DisplayName("testBlankPgnTimeControl")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { "", " ", "  " })
    void testBlankTimeControl(String timeControl) {
        assertNull(PgnTimeControl.timeoutOf(timeControl));
    }

    @Test
    void testNullTimeControl() {
        assertNull(PgnTimeControl.timeoutOf(null));
    }

    @DisplayName("testUnknownTimeControl")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { "?", "?:-", "?:-:" })
    void testUnknownTimeControl(String timeControl) {
        var parsedTimeout = PgnTimeControl.timeoutOf(timeControl);
        assertNotNull(parsedTimeout);

        assertTrue(parsedTimeout.isType(Timeout.Type.UNKNOWN));
        assertTrue(parsedTimeout instanceof UnknownTimeout);
        assertEquals(Optional.empty(), parsedTimeout.getDuration());
    }

    @DisplayName("testInvalidUnknownTimeControl")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { "??", "?!", "!?" })
    void testInvalidUnknownTimeControl(String timeControl) {
        assertNull(PgnTimeControl.timeoutOf(timeControl));
    }

    @DisplayName("testNoTimeoutTimeControl")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { "-", "--", "=-" })
    void testNoTimeoutTimeControl(String timeControl) {
        assertNull(PgnTimeControl.timeoutOf(timeControl));
    }

    @Test
    void testGeneralTimeControl() {
        var parsedTimeout = PgnTimeControl.timeoutOf("60");
        assertNotNull(parsedTimeout);

        assertTrue(parsedTimeout.isType(Timeout.Type.GENERIC));
        assertTrue(parsedTimeout instanceof GameTimeout);

        var duration = parsedTimeout.getDuration();
        assertTrue(duration.isPresent());
        assertEquals(60000L, duration.get().toMillis());
    }

    @DisplayName("testInvalidGeneralTimeControl")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { "12 3", "-123", "12.3" })
    void testInvalidGeneralTimeControl(String timeControl) {
        assertNull(PgnTimeControl.timeoutOf(timeControl));
    }

    @DisplayName("testIncrementalTimeControl")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { "*180+60", "40/9000+60", "4500+60" })
    void testIncrementalTimeControl(String timeControl) {
        var parsedTimeout = PgnTimeControl.timeoutOf(timeControl);
        assertNotNull(parsedTimeout);
        assertTrue(parsedTimeout.getDuration().isPresent());

        assertTrue(parsedTimeout.isType(Timeout.Type.INCREMENTAL));
        assertTrue(parsedTimeout instanceof IncrementalTimeout);

        var incrementalTimeout = (IncrementalTimeout<?>) parsedTimeout;
        assertEquals(60000L, incrementalTimeout.getExtraDuration().toMillis());

        var originTimeout = incrementalTimeout.getTimeout();
        assertTrue(originTimeout instanceof ActionTimeout || originTimeout instanceof GameTimeout);

        assertTrue(originTimeout.isAnyType(Timeout.Type.SANDCLOCK,
                Timeout.Type.GENERIC, Timeout.Type.ACTIONS_PER_PERIOD
        ));
    }

    @DisplayName("testInvalidIncrementalTimeControl")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { "*180++60", "40/9000+60+", "+4500+60" })
    void testInvalidIncrementalTimeControl(String timeControl) {
        assertNull(PgnTimeControl.timeoutOf(timeControl));
    }

    @Test
    void testSandclockTimeControl() {
        var parsedTimeout = PgnTimeControl.timeoutOf("*60");
        assertNotNull(parsedTimeout);

        assertTrue(parsedTimeout.isType(Timeout.Type.SANDCLOCK));
        assertTrue(parsedTimeout instanceof ActionTimeout);

        var duration = parsedTimeout.getDuration();
        assertTrue(duration.isPresent());
        assertEquals(60000L, duration.get().toMillis());
    }

    @DisplayName("testInvalidSandclockTimeControl")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { "**180", "*180*", "180*" })
    void testInvalidSandclockTimeControl(String timeControl) {
        assertNull(PgnTimeControl.timeoutOf(timeControl));
    }

    @Test
    void testActionsPerPeriodTimeControl() {
        var parsedTimeout = PgnTimeControl.timeoutOf("10/300");
        assertNotNull(parsedTimeout);

        var duration = parsedTimeout.getDuration();
        assertTrue(duration.isPresent());
        assertEquals(300000L, duration.get().toMillis());

        assertTrue(parsedTimeout.isType(Timeout.Type.SANDCLOCK));
        assertTrue(parsedTimeout instanceof ActionTimeout);

        var actionTimeout = (ActionTimeout) parsedTimeout;
        assertEquals(15000L, actionTimeout.getActionDuration().toMillis());

        assertTrue(parsedTimeout.isType(Timeout.Type.GENERIC));
        assertTrue(parsedTimeout instanceof GameTimeout);

        var gameTimeout = (GameTimeout) parsedTimeout;
        assertEquals(300000L, gameTimeout.getGameDuration().toMillis());

        assertTrue(parsedTimeout.isType(Timeout.Type.ACTIONS_PER_PERIOD));
        assertTrue(parsedTimeout instanceof MixedTimeout);

        var actionsGameTimeout = (MixedTimeout) parsedTimeout;
        assertEquals(10, actionsGameTimeout.getActionsCounter());
    }

    @DisplayName("testInvalidActionsPerPeriodTimeControl")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { "10//300", "/10", "10/", "ab/cd", "ab/10", "10/ab" })
    void testInvalidActionsPerPeriodTimeControl(String timeControl) {
        assertNull(PgnTimeControl.timeoutOf(timeControl));
    }

    @DisplayName("testCompositeTimeControl")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { "40/5400+30:1800+30", "40/5400:*300", "5400:*300", "5400+30:*300", "5400:*300+30" })
    void testCompositeTimeControl(String timeControl) {
        var timeout = PgnTimeControl.timeoutOf(timeControl);
        assertNotNull(timeout);

        assertTrue(timeout instanceof CompositeTimeout);
        assertTrue(timeout.isAnyType(Timeout.Type.SANDCLOCK, Timeout.Type.GENERIC,
                Timeout.Type.ACTIONS_PER_PERIOD, Timeout.Type.INCREMENTAL
        ));
    }

    @DisplayName("testInvalidCompositeTimeControl")
    @ParameterizedTest(name = "({index}) => (''{0}'')")
    @ValueSource(strings = { "-:-", "::" })
    void testInvalidCompositeTimeControl(String timeControl) {
        assertNull(PgnTimeControl.timeoutOf(timeControl));
    }
}