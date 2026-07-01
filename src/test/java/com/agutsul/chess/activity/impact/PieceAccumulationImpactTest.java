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
public class PieceAccumulationImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteAccumulationImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawns("b6","c7","e6","g7","h6")
                .withWhiteKing("e1")
                .withWhitePawns("b4","b5","c4","e3","e5","g4","h5")
                .build();

        var impact = accumulationImpact(board, "e3");
        assertNotNull(impact);
        assertEquals(-2, impact.getValue());
    }

    @Test
    void testBlackAccumulationImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawns("b6","c7","e6","e5","g7","h6")
                .withWhiteKing("e1")
                .withWhitePawns("b4","b5","c4","e3","g4","h5")
                .build();

        var impact = accumulationImpact(board, "e5");
        assertNotNull(impact);
        assertEquals(2, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawns("b6","c7","e6","g7","h6")
                .withWhiteKing("e1")
                .withWhitePawns("b4","b5","c4","e3","e5","g4","h5")
                .build();

        var impact = accumulationImpact(board, "b4");
        assertNotNull(impact);
        assertEquals("ACCUMULATION:[b4:b5]", String.valueOf(impact));
    }

    private static PieceAccumulationImpact<?,?> accumulationImpact(Board board, String piecePosition) {
        return Stream.of(getImpact(board, piecePosition, Impact.Type.ACCUMULATION))
                .flatMap(Optional::stream)
                .map(impact -> (PieceAccumulationImpact<?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}