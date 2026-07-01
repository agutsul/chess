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
public class PieceControlImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteKingControlImpact() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("e1")
                .build();

        var impact = controlImpact(board, "e1");

        assertNotNull(impact);
        assertEquals(1, impact.getValue());
    }

    @Test
    void testBlackKingControlImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .build();

        var impact = controlImpact(board, "e8");

        assertNotNull(impact);
        assertEquals(-1, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("a3")
                .build();

        var impact = controlImpact(board, "a3");

        assertNotNull(impact);
        assertEquals("CONTROL:a3Xb4", String.valueOf(impact));
    }

    private static PieceControlImpact<?,?> controlImpact(Board board, String piecePosition) {
        return Stream.of(getImpact(board, piecePosition, Impact.Type.CONTROL))
                .flatMap(Optional::stream)
                .map(impact -> (PieceControlImpact<?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}