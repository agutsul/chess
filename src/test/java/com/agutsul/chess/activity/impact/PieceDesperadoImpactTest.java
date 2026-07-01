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
public class PieceDesperadoImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteQueenDesperadoImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackQueen("b6")
                .withBlackRooks("c8","d8")
                .withBlackBishop("g7")
                .withBlackKnight("c5")
                .withBlackPawns("a6","b7","d6","e7","f7","g6","h7")
                .withWhiteKing("g1")
                .withWhiteQueen("d4")
                .withWhiteRooks("a1","f1")
                .withWhiteBishop("e2")
                .withWhiteKnight("c3")
                .withWhitePawns("a5","b2","c2","e4","f2","g2","h2")
                .build();

        var impact = desperadoImpact(board, "d4");
        assertNotNull(impact);
        assertEquals(-406, impact.getValue());
    }

    @Test
    void testBlackQueenDesperadoImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackQueen("a5")
                .withBlackRooks("a8","h8")
                .withBlackKnight("f6")
                .withBlackBishops("f8","e2")
                .withBlackPawns("h7","g7","f7","e6","b7","a7")
                .withWhiteKing("g1")
                .withWhiteQueen("d1")
                .withWhiteRooks("a1","f1")
                .withWhiteKnights("c3","c6")
                .withWhiteBishop("c1")
                .withWhitePawns("a2","b2","c2","d4","b2","a2")
                .build();

        var impact = desperadoImpact(board, "a5");
        assertNotNull(impact);
        assertEquals(23, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackQueen("a5")
                .withBlackRooks("a8","h8")
                .withBlackKnight("f6")
                .withBlackBishops("f8","e2")
                .withBlackPawns("h7","g7","f7","e6","b7","a7")
                .withWhiteKing("g1")
                .withWhiteQueen("d1")
                .withWhiteRooks("a1","f1")
                .withWhiteKnights("c3","c6")
                .withWhiteBishop("c1")
                .withWhitePawns("a2","b2","c2","d4","b2","a2")
                .build();

        var impact = desperadoImpact(board, "a5");
        assertNotNull(impact);

        assertEquals("DESPERADO:ABSOLUTE:DESPERADO:ABSOLUTE:[ ATTACK:Qa5xNc3 ] => [ ATTACK:b2xQa5 ]", String.valueOf(impact));
    }

    private static PieceDesperadoImpact<?,?,?,?,?,?> desperadoImpact(Board board, String piecePosition) {
        return Stream.of(getImpact(board, piecePosition, Impact.Type.DESPERADO))
                .flatMap(Optional::stream)
                .map(impact -> (PieceDesperadoImpact<?,?,?,?,?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}