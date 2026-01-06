package com.agutsul.chess.line;

import static com.agutsul.chess.line.LineFactory.lineOf;
import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.Strings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.position.PositionFactory;

@ExtendWith(MockitoExtension.class)
public class LineTest {

    @Test
    void testCreateCombinedLine() {
        var line = lineOf(
                lineOf(List.of(positionOf("a1"), positionOf("b2"))), // subline1
                lineOf(List.of(positionOf("a8"), positionOf("b7")))  // subline2
        );

        assertLine(line, "a1", "b2", "a8", "b7");
    }

    @Test
    void testLineSplit() {
        var line = lineOf(Stream.of("a1","a2","a3","a4","a5","a6","a7","a8")
                .map(PositionFactory::positionOf)
                .toList()
        );

        var subLines = new ArrayList<>(line.split(positionOf("a4")));

        assertFalse(subLines.isEmpty());
        assertEquals(2, subLines.size());

        assertLine(subLines.getFirst(), "a1","a2","a3","a4");
        assertLine(subLines.getLast(),  "a4","a5","a6","a7","a8");
    }

    @Test
    void testLineSplitByUnknownPosition() {
        var line = lineOf(List.of(positionOf("a8"), positionOf("b7")));

        var subLines = line.split(positionOf("a4"));
        assertTrue(subLines.isEmpty());
    }

    @Test
    void testEmptyLineSplit() {
        var line = lineOf(emptyList());

        var subLines = line.split(positionOf("a4"));
        assertTrue(subLines.isEmpty());
    }

    @Test
    void testLineSubLine() {
        var line = lineOf(Stream.of("a1","a2","a3","a4","a5","a6","a7","a8")
                .map(PositionFactory::positionOf)
                .toList()
        );

        var subLine = line.subLine(positionOf("a2"), positionOf("a4"));

        assertFalse(subLine.isEmpty());
        assertEquals(3, subLine.size());

        assertLine(subLine, "a2","a3","a4");
    }

    @Test
    void testLineSubLineReverse() {
        var line = lineOf(Stream.of("a1","a2","a3","a4","a5","a6","a7","a8")
                .map(PositionFactory::positionOf)
                .toList()
        );

        var subLine = line.subLine(positionOf("a4"), positionOf("a2"));

        assertFalse(subLine.isEmpty());
        assertEquals(3, subLine.size());

        assertLine(subLine, "a4","a3","a2");
    }

    @Test
    void testLineSubLineForUnknownPositions() {
        var line = lineOf(Stream.of("a1","a2","a3","a4","a5","a6","a7","a8")
                .map(PositionFactory::positionOf)
                .toList()
        );

        var subLine1 = line.subLine(positionOf("b2"), positionOf("b4"));
        assertTrue(subLine1.isEmpty());

        var subLine2 = line.subLine(positionOf("a2"), positionOf("b3"));
        assertTrue(subLine2.isEmpty());

        var subLine3 = line.subLine(positionOf("b2"), positionOf("a2"));
        assertTrue(subLine3.isEmpty());
    }

    private static void assertLine(Line line, String position, String... positions) {
        var lineStr = String.valueOf(line);

        Stream.of(List.of(position), List.of(positions))
            .flatMap(Collection::stream)
            .forEach(pos -> assertTrue(Strings.CI.contains(lineStr, pos)));
    }
}