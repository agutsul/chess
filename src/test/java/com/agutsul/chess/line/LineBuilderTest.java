package com.agutsul.chess.line;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineBuilderTest {

    @Test
    void testLineCreationWithPosition() {
        var builder = new LineBuilder();

        builder.append(positionOf("b1"));
        builder.append(positionOf("a2"));

        var line = builder.build();

        assertFalse(line.isEmpty());
        assertEquals(2, line.size());

        assertEquals("b1", String.valueOf(line.getFirst()));
        assertEquals("a2", String.valueOf(line.getLast()));
    }

    @Test
    void testLineCreationWithPositions() {
        var builder = new LineBuilder();
        builder.append(List.of(positionOf("b1"), positionOf("a2")));

        var line = builder.build();

        assertFalse(line.isEmpty());
        assertEquals(2, line.size());

        assertEquals("b1", String.valueOf(line.getFirst()));
        assertEquals("a2", String.valueOf(line.getLast()));
    }

    @Test
    void testLineCreationWithCombo() {
        var builder = new LineBuilder();
        builder.append(positionOf("c1"));
        builder.append(List.of(positionOf("b2"), positionOf("a3")));

        var line = builder.build();

        assertFalse(line.isEmpty());
        assertEquals(3, line.size());

        assertEquals("c1", String.valueOf(line.getFirst()));
        assertEquals("b2", String.valueOf(line.get(1)));
        assertEquals("a3", String.valueOf(line.getLast()));
    }

    @Test
    void testLineCreationWithComboAndSorting() {
        var builder = new LineBuilder();
        builder.append(positionOf("c1"));
        builder.append(List.of(positionOf("b2"), positionOf("a3")));

        var line = builder.sort().build();

        assertFalse(line.isEmpty());
        assertEquals(3, line.size());

        assertEquals("a3", String.valueOf(line.getFirst()));
        assertEquals("b2", String.valueOf(line.get(1)));
        assertEquals("c1", String.valueOf(line.getLast()));
    }

    @Test
    void testLineCreationWithEmptyPositions() {
        var builder = new LineBuilder();
        builder.append(emptyList());

        var line = builder.build();

        assertNotNull(line);
        assertTrue(line.isEmpty());

        var positions = List.of(positionOf("b2"), positionOf("a3"));

        assertFalse(line.containsAny(positions));
        assertTrue(line.intersection(positions).isEmpty());
    }
}