package com.agutsul.chess.position;

import static com.agutsul.chess.position.Position.isCentral;
import static com.agutsul.chess.position.PositionFactory.positionOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ValuablePositionTest {

    @Test
    void testValuablePosition() {
        var position = new ValuablePosition<Integer>(positionOf(0, 0), 10);
        assertEquals(10, position.getValue());
        assertEquals("a1:10", String.valueOf(position));
        assertEquals("a1", position.getCode());
        assertEquals(0, position.x());
        assertEquals(0, position.y());
        assertFalse(isCentral(position));
    }
}