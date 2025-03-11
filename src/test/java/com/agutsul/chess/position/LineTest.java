package com.agutsul.chess.position;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineTest {

    @Test
    void testCreateCombinedLine() {
        var line1 = new Line(List.of(positionOf("a1"), positionOf("b2")));
        var line2 = new Line(List.of(positionOf("a8"), positionOf("b7")));

        var line3 = new Line(line1, line2);
        var positions = line3.toString();

        assertTrue(contains(positions, "a1"));
        assertTrue(contains(positions, "b2"));
        assertTrue(contains(positions, "a8"));
        assertTrue(contains(positions, "b7"));
    }
}