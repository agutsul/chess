package com.agutsul.chess.position;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PositionComparatorTest {

    @Test
    void testEqualPositions() {
        var position1 = positionOf("c3");
        var position2 = positionOf(2, 2);

        var comparator = new PositionComparator();

        assertEquals(0, comparator.compare(position1, position2));
        assertEquals(0, comparator.compare(position2, position1));
    }

    @Test
    void testNotEqualPositions() {
        var position1 = positionOf("c3");
        var position2 = positionOf("b1");

        var comparator = new PositionComparator();

        assertEquals(1,  comparator.compare(position1, position2));
        assertEquals(-1, comparator.compare(position2, position1));

        var position3 = positionOf("b2");

        assertEquals(1,  comparator.compare(position3, position2));
        assertEquals(-1, comparator.compare(position2, position3));
    }
}