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
public class PieceBlockadeImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteBlockadeImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withWhiteKing("e1")
                .withWhitePawn("e2")
                .build();

        var impact = blockadeImpact(board, "e2");
        assertNotNull(impact);
        assertEquals(-1, impact.getValue());
    }

    @Test
    void testBlackBlockadeImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawn("e7")
                .withWhiteKing("e1")
                .build();

        var impact = blockadeImpact(board, "e7");
        assertNotNull(impact);
        assertEquals(1, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withWhiteKing("e1")
                .withWhitePawn("e2")
                .build();

        var impact = blockadeImpact(board, "e2");
        assertNotNull(impact);
        assertEquals("BLOCKADE:e2||Ke8", String.valueOf(impact));
    }

    private static PieceBlockadeImpact<?,?,?> blockadeImpact(Board board, String piecePosition) {
        return Stream.of(getImpact(board, piecePosition, Impact.Type.BLOCKADE))
                .flatMap(Optional::stream)
                .map(impact -> (PieceBlockadeImpact<?,?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}