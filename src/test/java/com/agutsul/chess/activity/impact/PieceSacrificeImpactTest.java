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
public class PieceSacrificeImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteSacrificeAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawn("e7")
                .withWhiteKing("e1")
                .withWhitePawn("d6")
                .build();

        var impact = sacrificeImpact(board, "d6");
        assertNotNull(impact);
        assertEquals(-399, impact.getValue());
    }

    @Test
    void testBlackSacrificeAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawn("d3")
                .withWhiteKing("e1")
                .withWhitePawn("e2")
                .build();

        var impact = sacrificeImpact(board, "d3");
        assertNotNull(impact);
        assertEquals(399, impact.getValue());
    }

    @Test
    void testWhiteSacrificeMoveImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withWhiteKing("e1")
                .withWhitePawn("e6")
                .build();

        var impact = sacrificeImpact(board, "e6");
        assertNotNull(impact);
        assertEquals(-399, impact.getValue());
    }

    @Test
    void testBlackSacrificeMoveImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawn("e3")
                .withWhiteKing("e1")
                .build();

        var impact = sacrificeImpact(board, "e3");
        assertNotNull(impact);
        assertEquals(399, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withWhiteKing("e1")
                .withWhitePawn("e6")
                .build();

        var impact = sacrificeImpact(board, "e6");
        assertNotNull(impact);
        assertEquals("SACRIFICE:Ke8x(e6 e7)", String.valueOf(impact));
    }

    private static PieceSacrificeImpact<?,?,?,?> sacrificeImpact(Board board, String piecePosition) {
        return Stream.of(getImpact(board, piecePosition, Impact.Type.SACRIFICE))
                .flatMap(Optional::stream)
                .map(impact -> (PieceSacrificeImpact<?,?,?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}