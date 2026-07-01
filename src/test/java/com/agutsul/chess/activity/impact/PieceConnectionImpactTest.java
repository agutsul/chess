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
public class PieceConnectionImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteConnectionImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withWhiteKing("e1")
                .withWhitePawns("a3","b3","c2")
                .build();

        var impact = connectionImpact(board, "b3");
        assertNotNull(impact);
        assertEquals(3, impact.getValue());
    }

    @Test
    void testBlackConnectionImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawns("a6","b6","c5")
                .withWhiteKing("e1")
                .build();

        var impact = connectionImpact(board, "b6");
        assertNotNull(impact);
        assertEquals(-3, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withWhiteKing("e1")
                .withWhitePawns("a3","b3","c2")
                .build();

        var impact = connectionImpact(board, "b3");
        assertNotNull(impact);
        assertEquals("CONNECTION:(a3:b3:c2)", String.valueOf(impact));
    }

    private static PieceConnectionImpact<?,?> connectionImpact(Board board, String piecePosition) {
        var impacts = getImpact(board, piecePosition, Impact.Type.CONNECTION);
        return Stream.of(impacts)
                .flatMap(Optional::stream)
                .map(impact -> (PieceConnectionImpact<?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}