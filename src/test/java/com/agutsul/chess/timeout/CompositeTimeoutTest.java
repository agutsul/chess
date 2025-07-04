package com.agutsul.chess.timeout;

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
        var actionsGameTimeout = new ActionsGameTimeoutImpl<>(30000, 10);
        var incrementalTimeout = new IncrementalTimeoutImpl(actionsGameTimeout, 6000);

        var gameTimeout = new GameTimeoutImpl(3000);
        var compositeTimeout = new CompositeTimeout(
                gameTimeout,
                actionsGameTimeout,
                new IncrementalTimeoutImpl(gameTimeout, 6000),
                incrementalTimeout
        );

        assertEquals("3:10/30:3+6:10/30+6", compositeTimeout.toString());

        var actionTimeout = new ActionTimeoutImpl(1000);
        var compositeTimeout2 = new CompositeTimeout(
                actionTimeout,
                new IncrementalTimeoutImpl(actionTimeout, 6000),
                actionsGameTimeout,
                incrementalTimeout
        );

        assertEquals("*1:*1+6:10/30:10/30+6", compositeTimeout2.toString());
    }

    @Test
    void testCompositeTimeoutIsAnyType() {
        var actionTimeout = new ActionTimeoutImpl(1000);

        var compositeTimeout = new CompositeTimeout(
                actionTimeout,
                new IncrementalTimeoutImpl(actionTimeout, 600),
                new UnknownTimeoutImpl()
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
        var actionTimeout = new ActionTimeoutImpl(1000);

        var compositeTimeout = new CompositeTimeout(
                actionTimeout,
                new IncrementalTimeoutImpl(actionTimeout, 600),
                new UnknownTimeoutImpl()
        );

        assertTrue(compositeTimeout.isType(Timeout.Type.SANDCLOCK));
        assertTrue(compositeTimeout.isType(Timeout.Type.INCREMENTAL));
        assertTrue(compositeTimeout.isType(Timeout.Type.UNKNOWN));

        assertFalse(compositeTimeout.isType(Timeout.Type.GENERIC));
        assertFalse(compositeTimeout.isType(Timeout.Type.ACTIONS_PER_PERIOD));
    }

    @Test
    void testCompositeTimeoutDurationEmpty() {
        var actionTimeout = new ActionTimeoutImpl(1000);

        var compositeTimeout = new CompositeTimeout(
                actionTimeout,
                new IncrementalTimeoutImpl(actionTimeout, 600),
                new UnknownTimeoutImpl()
        );

        var duration = compositeTimeout.getDuration();
        assertTrue(duration.isEmpty());
    }

    @Test
    void testCompositeTimeoutDuration() {
        var gameTimeout = new GameTimeoutImpl(3000);
        var actionTimeout = new ActionTimeoutImpl(1000);

        var compositeTimeout = new CompositeTimeout(
                gameTimeout,
                new ActionsGameTimeoutImpl<>(3000, 20),
                actionTimeout,
                new IncrementalTimeoutImpl(actionTimeout, 600),
                new IncrementalTimeoutImpl(gameTimeout, 600),
                new UnknownTimeoutImpl()
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