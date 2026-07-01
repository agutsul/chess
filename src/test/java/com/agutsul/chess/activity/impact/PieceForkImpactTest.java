package com.agutsul.chess.activity.impact;

import static com.agutsul.chess.activity.impact.PieceForkImpact.isAbsolute;
import static com.agutsul.chess.activity.impact.PieceForkImpact.isRelative;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.LabeledBoardBuilder;

@ExtendWith(MockitoExtension.class)
public class PieceForkImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteRelativeForkImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("c8")
                .withBlackBishop("a7")
                .withBlackPawn("e7")
                .withWhiteKing("e1")
                .withWhiteKnight("c6")
                .build();

        var impact = forkImpact(board, "c6");
        assertNotNull(impact);

        assertEquals(3, impact.getValue());
        assertTrue(isRelative(impact));

        var expectedAttacked = Stream.of("a7","e7")
                .map(position -> board.getPiece(position))
                .flatMap(Optional::stream)
                .toList();

        assertTrue(expectedAttacked.containsAll(impact.getAttacked()));
    }

    @Test
    void testBlackRelativeForkImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackKnight("c3")
                .withWhiteKing("c1")
                .withWhitePawn("a2")
                .withWhiteBishop("e2")
                .build();

        var impact = forkImpact(board, "c3");
        assertNotNull(impact);

        assertEquals(-3, impact.getValue());
        assertTrue(isRelative(impact));

        var expectedAttacked = Stream.of("a2","e2")
                .map(position -> board.getPiece(position))
                .flatMap(Optional::stream)
                .toList();

        assertTrue(expectedAttacked.containsAll(impact.getAttacked()));
    }

    @Test
    void testWhiteAbsoluteForkImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("a7")
                .withBlackPawn("f6")
                .withWhiteKing("e1")
                .withWhiteBishop("d4")
                .build();

        var impact = forkImpact(board, "d4");
        assertNotNull(impact);

        assertEquals(597, impact.getValue());
        assertTrue(isAbsolute(impact));

        var expectedAttacked = Stream.of("a7","f6")
                .map(position -> board.getPiece(position))
                .flatMap(Optional::stream)
                .toList();

        assertTrue(expectedAttacked.containsAll(impact.getAttacked()));
    }

    @Test
    void testBlackAbsoluteForkImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("c8")
                .withBlackBishop("e6")
                .withWhiteKing("h3")
                .withWhitePawn("c4")
                .build();

        var impact = forkImpact(board, "e6");
        assertNotNull(impact);

        assertEquals(-597, impact.getValue());
        assertTrue(isAbsolute(impact));

        var expectedAttacked = Stream.of("c4","h3")
                .map(position -> board.getPiece(position))
                .flatMap(Optional::stream)
                .toList();

        assertTrue(expectedAttacked.containsAll(impact.getAttacked()));
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("c8")
                .withBlackBishop("e6")
                .withWhiteKing("h3")
                .withWhitePawn("c4")
                .build();

        var impact = forkImpact(board, "e6");
        assertNotNull(impact);

        assertEquals("FORK:ABSOLUTE:Be6x(CHECK:Be6xKh3!,ATTACK:Be6xc4)", String.valueOf(impact));
    }

    private static PieceForkImpact<?,?,?,?> forkImpact(Board board, String piecePosition) {
        return Stream.of(getImpact(board, piecePosition, Impact.Type.FORK))
                .flatMap(Optional::stream)
                .map(impact -> (PieceForkImpact<?,?,?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}