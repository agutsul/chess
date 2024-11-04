package com.agutsul.chess.position;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.exception.IllegalPositionException;

@ExtendWith(MockitoExtension.class)
public class PositionImplTest {

    @Test
    void testInvalildPositionCreation() {
        var thrown = assertThrows(
                IllegalPositionException.class,
                () -> new PositionImpl(-1, -1)
            );

        assertEquals("Invalid position [-1,-1]", thrown.getMessage());
    }

    @Test
    void testValidPositionCreation() {
        var position = new PositionImpl(0,0);
        assertEquals("a1", position.getCode());
        assertEquals(0, position.x());
        assertEquals(0, position.y());
    }
}
