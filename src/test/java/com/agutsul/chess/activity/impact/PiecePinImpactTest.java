package com.agutsul.chess.activity.impact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.impact.PiecePinImpact.Mode;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.LabeledBoardBuilder;

@ExtendWith(MockitoExtension.class)
public class PiecePinImpactTest extends AbstractImpactTest {

    @Test
    void testBlackRelativePinImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackQueen("c8")
                .withBlackPawn("c4")
                .withWhiteKing("e1")
                .withWhiteRook("c1")
                .build();

        var impact = pinImpact(board, "c4");
        assertNotNull(impact);
        assertTrue(impact.isMode(Mode.RELATIVE));
        assertEquals(-12, impact.getValue());
    }

    @Test
    void testWhiteRelativePinImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackRook("c8")
                .withWhiteKing("e1")
                .withWhiteQueen("c1")
                .withWhitePawn("c4")
                .build();

        var impact = pinImpact(board, "c4");
        assertNotNull(impact);
        assertTrue(impact.isMode(Mode.RELATIVE));
        assertEquals(12, impact.getValue());
    }

    @Test
    void testWhiteAbsolutePinImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackRook("c8")
                .withWhiteKing("c1")
                .withWhitePawn("c4")
                .build();

        var impact = pinImpact(board, "c4");
        assertNotNull(impact);
        assertTrue(impact.isMode(Mode.ABSOLUTE));
        assertEquals(594, impact.getValue());
    }

    @Test
    void testBlackAbsolutePinImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("c8")
                .withBlackPawn("c4")
                .withWhiteKing("e1")
                .withWhiteRook("c1")
                .build();

        var impact = pinImpact(board, "c4");
        assertNotNull(impact);
        assertTrue(impact.isMode(Mode.ABSOLUTE));
        assertEquals(-594, impact.getValue());
    }

    @Test
    void testWhiteRelativePinImpactBySameValuePieces() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackBishop("h8")
                .withWhiteKing("e1")
                .withWhiteQueen("a1")
                .withWhiteKnight("d4")
                .build();

        var impact = pinImpact(board, "d4");
        assertNotNull(impact);
        assertTrue(impact.isMode(Mode.RELATIVE));
        assertEquals(12, impact.getValue());
    }

    @Test
    void testBlackRelativePinImpactBySameValuePieces() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackQueen("a8")
                .withBlackKnight("d5")
                .withWhiteKing("e1")
                .withWhiteBishop("h1")
                .build();

        var impact = pinImpact(board, "d5");
        assertNotNull(impact);
        assertTrue(impact.isMode(Mode.RELATIVE));
        assertEquals(-12, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackQueen("c8")
                .withBlackPawn("c4")
                .withWhiteKing("e1")
                .withWhiteRook("c1")
                .build();

        var impact = pinImpact(board, "c4");
        assertNotNull(impact);
        assertEquals("PIN:PARTIAL:PIN:RELATIVE:c4{ATTACK:Rc1xQc8}", String.valueOf(impact));
    }

    private static PiecePinImpact<?,?,?,?,?> pinImpact(Board board, String piecePosition) {
        return Stream.of(getImpact(board, piecePosition, Impact.Type.PIN))
                .flatMap(Optional::stream)
                .map(impact -> (PiecePinImpact<?,?,?,?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}