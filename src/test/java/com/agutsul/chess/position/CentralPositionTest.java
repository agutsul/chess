package com.agutsul.chess.position;

import static com.agutsul.chess.position.Position.isCentral;
import static com.agutsul.chess.position.PositionFactory.positionOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CentralPositionTest {

    @Test
    void testCentralPosition() {
        var position = positionOf(3, 3);
        assertEquals("d4", position.getCode());
        assertEquals(3, position.x());
        assertEquals(3, position.y());
        assertTrue(isCentral(position));
    }
}