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
public class PieceBlockImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteBlockImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h8")
                .withBlackRook("e8")
                .withWhiteKing("e1")
                .withWhiteKnight("c5")
                .build();

        var impact = blockImpact(board, "c5");
        assertNotNull(impact);
        assertEquals(198, impact.getValue());
    }

    @Test
    void testBlackBlockImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackKnight("c5")
                .withWhiteKing("h1")
                .withWhiteRook("e1")
                .build();

        var impact = blockImpact(board, "c5");
        assertNotNull(impact);
        assertEquals(-198, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackKnight("c5")
                .withWhiteKing("a1")
                .withWhiteRook("e1")
                .build();

        var impact = blockImpact(board, "c5");
        assertNotNull(impact);
        assertEquals("BLOCK:Nc5 (Re1 [e4] Ke8)", String.valueOf(impact));
    }

    private static PieceBlockImpact<?,?,?,?,?> blockImpact(Board board, String piecePosition) {
        return Stream.of(getImpact(board, piecePosition, Impact.Type.BLOCK))
                .flatMap(Optional::stream)
                .map(impact -> (PieceBlockImpact<?,?,?,?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}