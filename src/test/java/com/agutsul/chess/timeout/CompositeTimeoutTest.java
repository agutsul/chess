package com.agutsul.chess.timeout;

import static com.agutsul.chess.timeout.TimeoutFactory.createActionTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createGameTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createIncrementalTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createMixedTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createUnknownTimeout;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CompositeTimeoutTest {

    @Test
    void testCompositeTimeoutToString() {
        var mixedTimeout = createMixedTimeout(30000, 10);
        var incrementalTimeout = createIncrementalTimeout(mixedTimeout, 6000);

        var gameTimeout = createGameTimeout(3000);
        var compositeTimeout = new CompositeTimeout(
                gameTimeout,
                mixedTimeout,
                createIncrementalTimeout(gameTimeout, 6000),
                incrementalTimeout
        );

        assertEquals("3:10/30:3+6:10/30+6", compositeTimeout.toString());

        var actionTimeout = createActionTimeout(1000);
        var compositeTimeout2 = new CompositeTimeout(
                actionTimeout,
                createIncrementalTimeout(actionTimeout, 6000),
                mixedTimeout,
                incrementalTimeout
        );

        assertEquals("*1:*1+6:10/30:10/30+6", compositeTimeout2.toString());
    }

    @Test
    void testCompositeTimeoutIsAnyType() {
        var actionTimeout = createActionTimeout(1000);

        var compositeTimeout = new CompositeTimeout(
                actionTimeout,
                createIncrementalTimeout(actionTimeout, 600),
                createUnknownTimeout()
        );

        assertTrue(compositeTimeout.isAnyType(
                Timeout.Type.SANDCLOCK, Timeout.Type.INCREMENTAL, Timeout.Type.UNKNOWN
        ));

        assertFalse(compositeTimeout.isAnyType(
                Timeout.Type.GENERIC, Timeout.Type.ACTIONS_PER_PERIOD
        ));
    }

    @Test
    void testCompositeTimeoutIsType() {
        var actionTimeout = createActionTimeout(1000);

        var compositeTimeout = new CompositeTimeout(
                actionTimeout,
                createIncrementalTimeout(actionTimeout, 600),
                createUnknownTimeout()
        );

        assertTrue(compositeTimeout.isType(Timeout.Type.SANDCLOCK));
        assertTrue(compositeTimeout.isType(Timeout.Type.INCREMENTAL));
        assertTrue(compositeTimeout.isType(Timeout.Type.UNKNOWN));

        assertFalse(compositeTimeout.isType(Timeout.Type.GENERIC));
        assertFalse(compositeTimeout.isType(Timeout.Type.ACTIONS_PER_PERIOD));
    }

    @Test
    void testCompositeTimeoutDurationEmpty() {
        var actionTimeout = createActionTimeout(1000);

        var compositeTimeout = new CompositeTimeout(
                actionTimeout,
                createIncrementalTimeout(actionTimeout, 600),
                createUnknownTimeout()
        );

        var duration = compositeTimeout.getDuration();
        assertTrue(duration.isEmpty());
    }

    @Test
    void testCompositeTimeoutDuration() {
        var gameTimeout   = createGameTimeout(3000);
        var actionTimeout = createActionTimeout(1000);

        var compositeTimeout = new CompositeTimeout(
                gameTimeout,
                createMixedTimeout(3000, 20),
                actionTimeout,
                createIncrementalTimeout(actionTimeout, 600),
                createIncrementalTimeout(gameTimeout, 600),
                createUnknownTimeout()
        );

        var duration = compositeTimeout.getDuration();
        assertTrue(duration.isPresent());

        assertEquals(9000L, duration.get().toMillis());
    }

    @Test
    void testEmptyCompositeTimeoutList() {
        var message = "Unable to create composite timeout";

        var timeouts = new ArrayList<Timeout>();
        var thrown = assertThrows(
                IllegalStateException.class,
                () -> new CompositeTimeout(timeouts)
        );

        assertEquals(message, thrown.getMessage());

        timeouts.add(null);
        timeouts.add(null);

        var thrown2 = assertThrows(
                IllegalStateException.class,
                () -> new CompositeTimeout(timeouts)
        );

        assertEquals(message, thrown2.getMessage());
    }
}