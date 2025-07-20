package com.agutsul.chess.timeout;

import static com.agutsul.chess.timeout.TimeoutFactory.createMixedTimeout;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.timeout.Timeout.Type;

@ExtendWith(MockitoExtension.class)
public class MixedTimeoutImplTest {

    @Test
    void testInvalidActionCounter() {
        var thrown = assertThrows(
                IllegalArgumentException.class,
                () -> createMixedTimeout(100L, 0)
        );

        assertEquals("Invalid actions counter: 0", thrown.getMessage());
    }

    @Test
    void testIsAnyType() {
        var timeout = createMixedTimeout(100L, 2);

        assertTrue(timeout.isAnyType(Type.ACTIONS_PER_PERIOD));
        assertTrue(timeout.isAnyType(Type.GENERIC, Type.INCREMENTAL));
        assertTrue(timeout.isAnyType(Type.INCREMENTAL, Type.SANDCLOCK));

        assertFalse(timeout.isAnyType(Type.INCREMENTAL, Type.UNKNOWN));
        assertFalse(timeout.isAnyType(Type.UNKNOWN));
    }
}