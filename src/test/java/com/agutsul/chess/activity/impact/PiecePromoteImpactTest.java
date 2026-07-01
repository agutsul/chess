package com.agutsul.chess.activity.impact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.Strings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.LabeledBoardBuilder;

@ExtendWith(MockitoExtension.class)
public class PiecePromoteImpactTest extends AbstractImpactTest {

    @Test
    void testWhitePromoteViaMoveImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withWhiteKing("e1")
                .withWhitePawn("a7")
                .build();

        var impact = promoteImpact(board, "a7");
        assertNotNull(impact);
        assertEquals(9, impact.getValue());
    }

    @Test
    void testBlackPromoteViaMoveImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawn("a2")
                .withWhiteKing("e1")
                .build();

        var impact = promoteImpact(board, "a2");
        assertNotNull(impact);
        assertEquals(-9, impact.getValue());
    }

    @Test
    void testWhitePromoteViaAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackRook("a8")
                .withBlackKnight("b8")
                .withWhiteKing("e1")
                .withWhitePawn("b7")
                .build();

        var impact = promoteImpact(board, "b7");
        assertNotNull(impact);
        assertEquals(17, impact.getValue());
    }

    @Test
    void testBlackPromoteViaAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawn("b2")
                .withWhiteKing("e1")
                .withWhiteKnight("b1")
                .withWhiteRook("a1")
                .build();

        var impact = promoteImpact(board, "b2");
        assertNotNull(impact);
        assertEquals(-17, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withWhiteKing("e1")
                .withWhitePawn("a7")
                .build();

        var impact = promoteImpact(board, "a7");
        assertNotNull(impact);
        assertTrue(Strings.CI.startsWith(String.valueOf(impact), "PROMOTE:(MOTION:a7->a8)="));
    }

    private static PiecePromoteImpact<?,?> promoteImpact(Board board, String piecePosition) {
        return Stream.of(getImpact(board, piecePosition, Impact.Type.PROMOTE))
                .flatMap(Optional::stream)
                .map(impact -> (PiecePromoteImpact<?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}