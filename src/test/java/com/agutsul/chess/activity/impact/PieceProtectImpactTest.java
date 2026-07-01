package com.agutsul.chess.activity.impact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.LabeledBoardBuilder;

@ExtendWith(MockitoExtension.class)
public class PieceProtectImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteKingProtectsPawnImpact() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("e1")
                .withWhitePawn("e2")
                .build();

        var impact = protectImpact(board, "e1");
        assertNotNull(impact);
        assertEquals(1, impact.getValue());
    }

    @Test
    void testWhitePawnProtectsKingImpact() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("d3")
                .withWhitePawn("e2")
                .build();

        var impact = protectImpact(board, "e2");
        assertNotNull(impact);
        assertEquals(400, impact.getValue());
    }

    @Test
    void testWhiteBishopProtectsKnightImpact() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("f1")
                .withWhiteKnight("c3")
                .withWhiteBishop("d2")
                .build();

        var impact = protectImpact(board, "d2");
        assertNotNull(impact);
        assertEquals(3, impact.getValue());
    }

    @Test
    void testBlackKingProtectsPawnImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawn("e7")
                .build();

        var impact = protectImpact(board, "e8");
        assertNotNull(impact);
        assertEquals(-1, impact.getValue());
    }

    @Test
    void testBlackPawnProtectsKingImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("d6")
                .withBlackPawn("e7")
                .build();

        var impact = protectImpact(board, "e7");
        assertNotNull(impact);
        assertEquals(-400, impact.getValue());
    }

    @Test
    void testBlackBishopProtectsKnightImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("f8")
                .withBlackKnight("c6")
                .withBlackBishop("d7")
                .build();

        var impact = protectImpact(board, "d7");
        assertNotNull(impact);
        assertEquals(-3, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("f8")
                .withBlackKnight("c6")
                .withBlackBishop("d7")
                .build();

        var impact = protectImpact(board, "d7");
        assertNotNull(impact);
        assertEquals("PROTECT:Bd7(Nc6)", String.valueOf(impact));
    }

    private static PieceProtectImpact<?,?,?> protectImpact(Board board, String piecePosition) {
        return Stream.of(getImpact(board, piecePosition, Impact.Type.PROTECT))
                .flatMap(Optional::stream)
                .map(impact -> (PieceProtectImpact<?,?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}