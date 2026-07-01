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
public class PieceDominationImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteDominationImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("a7")
                .withBlackQueen("b7")
                .withBlackKnight("f5")
                .withBlackPawns("a5","b6","c7","d6","h6")
                .withWhiteKing("b1")
                .withWhiteQueen("e4")
                .withWhiteBishop("f6")
                .withWhitePawns("a3","b2","c2","f4","h2")
                .build();

        var impact = dominationImpact(board, "e4");
        assertNotNull(impact);
        assertEquals(9, impact.getValue());
    }

    @Test
    void testBlackDominationImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackQueen("d3")
                .withBlackBishop("c3")
                .withBlackPawns("a7","c5","f7","g7","h6")
                .withWhiteKing("h2")
                .withWhiteQueen("g2")
                .withWhiteKnight("c4")
                .withWhitePawns("a3","e3","f2","g3","h4")
                .build();

        var impact = dominationImpact(board, "d3");
        assertNotNull(impact);
        assertEquals(8, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackQueen("d3")
                .withBlackBishop("c3")
                .withBlackPawns("a7","c5","f7","g7","h6")
                .withWhiteKing("h2")
                .withWhiteQueen("g2")
                .withWhiteKnight("c4")
                .withWhitePawns("a3","e3","f2","g3","h4")
                .build();

        var impact = dominationImpact(board, "d3");
        assertNotNull(impact);
        assertEquals("DOMINATION:ATTACK:Qd3xe3", String.valueOf(impact));
    }

    private static PieceDominationImpact<?,?,?,?> dominationImpact(Board board, String piecePosition) {
        return Stream.of(getImpact(board, piecePosition, Impact.Type.DOMINATION))
                .flatMap(Optional::stream)
                .map(impact -> (PieceDominationImpact<?,?,?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}