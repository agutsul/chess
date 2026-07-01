package com.agutsul.chess.activity.impact;

import static com.agutsul.chess.activity.impact.PieceXRayImpact.isAbsolute;
import static com.agutsul.chess.activity.impact.PieceXRayImpact.isRelative;
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
public class PieceXRayImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteAbsoluteXRayProtectImpact() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("g1")
                .withWhiteRook("a1")
                .withBlackBishop("c1")
                .build();

        var impact = xRayImpact(board, "a1");
        assertNotNull(impact);

        assertTrue(isAbsolute(impact));
        assertEquals(403, impact.getValue());

        var pieces = impact.getPieces();
        assertEquals(1, pieces.size());
        assertTrue(pieces.contains(board.getPiece("c1").get()));
    }

    @Test
    void testBlackAbsoluteXRayProtectImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackRook("a8")
                .withWhiteBishop("c8")
                .build();

        var impact = xRayImpact(board, "a8");
        assertNotNull(impact);

        assertTrue(isAbsolute(impact));
        assertEquals(-403, impact.getValue());

        var pieces = impact.getPieces();
        assertEquals(1, pieces.size());
        assertTrue(pieces.contains(board.getPiece("c8").get()));
    }

    @Test
    void testWhiteRelativeXRayProtectImpact() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("e1")
                .withWhiteRook("c1")
                .withWhiteBishop("c5")
                .withBlackPawn("c4")
                .build();

        var impact = xRayImpact(board, "c1");
        assertNotNull(impact);

        assertTrue(isRelative(impact));
        assertEquals(4, impact.getValue());

        var pieces = impact.getPieces();
        assertEquals(1, pieces.size());
        assertTrue(pieces.contains(board.getPiece("c4").get()));
    }

    @Test
    void testBlackRelativeXRayProtectImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackRook("c8")
                .withBlackBishop("c5")
                .withWhitePawn("c6")
                .build();

        var impact = xRayImpact(board, "c8");
        assertNotNull(impact);

        assertTrue(isRelative(impact));
        assertEquals(-4, impact.getValue());

        var pieces = impact.getPieces();
        assertEquals(1, pieces.size());
        assertTrue(pieces.contains(board.getPiece("c6").get()));
    }

    @Test
    void testWhiteAbsoluteXRayAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("c8")
                .withWhiteKing("e1")
                .withWhiteRook("c1")
                .withWhiteKnight("c4")
                .build();

        var impact = xRayImpact(board, "c1");
        assertNotNull(impact);

        assertTrue(isAbsolute(impact));
        assertEquals(592, impact.getValue());

        var pieces = impact.getPieces();
        assertEquals(1, pieces.size());
        assertTrue(pieces.contains(board.getPiece("c4").get()));
    }

    @Test
    void testBlackAbsoluteXRayAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackRook("c8")
                .withWhiteKing("c1")
                .withBlackKnight("c5")
                .build();

        var impact = xRayImpact(board, "c8");
        assertNotNull(impact);

        assertTrue(isAbsolute(impact));
        assertEquals(-592, impact.getValue());

        var pieces = impact.getPieces();
        assertEquals(1, pieces.size());
        assertTrue(pieces.contains(board.getPiece("c5").get()));
    }

    @Test
    void testWhiteRelativeXRayAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackQueen("c8")
                .withWhiteKing("e1")
                .withWhiteRook("c1")
                .withWhitePawn("c4")
                .build();

        var impact = xRayImpact(board, "c1");
        assertNotNull(impact);

        assertTrue(isRelative(impact));
        assertEquals(12, impact.getValue());

        var pieces = impact.getPieces();
        assertEquals(1, pieces.size());
        assertTrue(pieces.contains(board.getPiece("c4").get()));
    }

    @Test
    void testBlackRelativeXRayAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackRook("c8")
                .withBlackPawn("c5")
                .withWhiteKing("e1")
                .withWhiteQueen("c1")
                .build();

        var impact = xRayImpact(board, "c8");
        assertNotNull(impact);

        assertTrue(isRelative(impact));
        assertEquals(-12, impact.getValue());

        var pieces = impact.getPieces();
        assertEquals(1, pieces.size());
        assertTrue(pieces.contains(board.getPiece("c5").get()));
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackQueen("c8")
                .withWhiteKing("e1")
                .withWhiteRook("c1")
                .withWhitePawn("c4")
                .build();

        var impact = xRayImpact(board, "c1");
        assertNotNull(impact);

        assertEquals("XRAY:RELATIVE:ATTACK:Rc1xQc8", String.valueOf(impact));
    }

    private static PieceXRayImpact<?,?,?,?> xRayImpact(Board board, String piecePosition) {
        return Stream.of(getImpact(board, piecePosition, Impact.Type.XRAY))
                .flatMap(Optional::stream)
                .map(impact -> (PieceXRayImpact<?,?,?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}