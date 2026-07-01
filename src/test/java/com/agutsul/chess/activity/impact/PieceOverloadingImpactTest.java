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
public class PieceOverloadingImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteOverloadingImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackQueen("f5")
                .withBlackRook("a8")
                .withBlackBishop("g7")
                .withBlackKnights("c6","g4")
                .withBlackPawns("a5","b7","c7","d6","g6","h7")
                .withWhiteKing("g2")
                .withWhiteQueen("d2")
                .withWhiteRooks("e3","f1")
                .withWhiteKnights("c3","g5")
                .withWhitePawns("a2","b3","c4","f4","g3","h2")
                .build();

        var impact = overloadingImpact(board, "d2");
        assertNotNull(impact);
        assertEquals(1, impact.getValue());
    }

    @Test
    void testBlackOverloadingImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackQueen("e8")
                .withBlackRooks("a8","f8")
                .withBlackBishop("d5")
                .withBlackKnight("f5")
                .withBlackPawns("a7","b7","c7","e6","f7","g7","h7")
                .withWhiteKing("g1")
                .withWhiteQueen("e1")
                .withWhiteRooks("a1","f1")
                .withWhiteBishop("d3")
                .withWhiteKnight("c3")
                .withWhitePawns("a2","b2","c2","f2","g2","h2")
                .build();

        var impact = overloadingImpact(board, "e6");
        assertNotNull(impact);
        assertEquals(-1, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackQueen("e8")
                .withBlackRooks("a8","f8")
                .withBlackBishop("d5")
                .withBlackKnight("f5")
                .withBlackPawns("a7","b7","c7","e6","f7","g7","h7")
                .withWhiteKing("g1")
                .withWhiteQueen("e1")
                .withWhiteRooks("a1","f1")
                .withWhiteBishop("d3")
                .withWhiteKnight("c3")
                .withWhitePawns("a2","b2","c2","f2","g2","h2")
                .build();

        var impact = overloadingImpact(board, "e6");
        assertNotNull(impact);
        assertEquals("OVERLOADING:e6 over f5", String.valueOf(impact));
    }

    private static PieceOverloadingImpact<?,?> overloadingImpact(Board board, String piecePosition) {
        var impacts = getImpact(board, piecePosition, Impact.Type.OVERLOADING);
        return Stream.of(impacts)
                .flatMap(Optional::stream)
                .map(impact -> (PieceOverloadingImpact<?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}