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
public class PieceIsolationImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteIsolationImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withWhiteKing("e1")
                .withWhitePawns("b4","b5","c4","e4","g4","h5")
                .build();

        var impact = isolationImpact(board, "e4");
        assertNotNull(impact);
        assertEquals(-1, impact.getValue());
    }

    @Test
    void testBlackIsolationImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawns("a7","c7","d6","g7","h6")
                .withWhiteKing("e1")
                .build();

        var impact = isolationImpact(board, "a7");
        assertNotNull(impact);
        assertEquals(1, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withWhiteKing("e1")
                .withWhitePawns("b4","b5","c4","e4","g4","h5")
                .build();

        var impact = isolationImpact(board, "e4");
        assertNotNull(impact);
        assertEquals("ISOLATION:|e4|", String.valueOf(impact));
    }

    private static PieceIsolationImpact<?,?> isolationImpact(Board board, String piecePosition) {
        var impacts = getImpact(board, piecePosition, Impact.Type.ISOLATION);
        return Stream.of(impacts)
                .flatMap(Optional::stream)
                .map(impact -> (PieceIsolationImpact<?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}