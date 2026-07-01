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
public class PieceOutpostImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteOutpostImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackQueen("d8")
                .withBlackRooks("c8","f8")
                .withBlackBishop("e7")
                .withBlackKnight("f6")
                .withBlackPawns("a6","b7","d6","e5","f7","g7","h7")
                .withWhiteKing("h1")
                .withWhiteQueen("d1")
                .withWhiteRooks("a1","f1")
                .withWhiteBishop("g5")
                .withWhiteKnight("c3")
                .withWhitePawns("a2","b2","c2","e4","f5","g2","h2")
                .build();

        var impact = outpostImpact(board, "c3");
        assertNotNull(impact);
        assertEquals(3, impact.getValue());
    }

    @Test
    void testBlackOutpostImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackQueen("e7")
                .withBlackRooks("d8","f8")
                .withBlackBishops("h3","h6")
                .withBlackKnight("e6")
                .withBlackPawns("a7","b7","c5","d6","e5","f5","g6","h7")
                .withWhiteKing("g1")
                .withWhiteQueen("d1")
                .withWhiteRooks("a1","f2")
                .withWhiteBishops("c1","e2")
                .withWhiteKnight("g2")
                .withWhitePawns("a2","b3","c4","d3","e4","f3","g3","h2")
                .build();

        var impact = outpostImpact(board, "e6");
        assertNotNull(impact);
        assertEquals(-3, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackQueen("d8")
                .withBlackRooks("c8","f8")
                .withBlackBishop("e7")
                .withBlackKnight("f6")
                .withBlackPawns("a6","b7","d6","e5","f7","g7","h7")
                .withWhiteKing("h1")
                .withWhiteQueen("d1")
                .withWhiteRooks("a1","f1")
                .withWhiteBishop("g5")
                .withWhiteKnight("c3")
                .withWhitePawns("a2","b2","c2","e4","f5","g2","h2")
                .build();

        var impact = outpostImpact(board, "c3");
        assertNotNull(impact);
        assertEquals("OUTPOST:Nc3 ~ d5", String.valueOf(impact));
    }

    private static PieceOutpostImpact<?,?> outpostImpact(Board board, String piecePosition) {
        var impacts = getImpact(board, piecePosition, Impact.Type.OUTPOST);
        return Stream.of(impacts)
                .flatMap(Optional::stream)
                .map(impact -> (PieceOutpostImpact<?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}