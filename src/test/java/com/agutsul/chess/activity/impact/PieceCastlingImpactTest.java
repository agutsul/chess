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
public class PieceCastlingImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteCastlingImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withWhiteKing("e1")
                .withWhiteRook("h1")
                .build();

        var impact = castlingImpact(board, "e1");

        assertNotNull(impact);
        assertEquals(512, impact.getValue());
    }

    @Test
    void testBlackCastlingImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackRook("h8")
                .withWhiteKing("e1")
                .build();

        var impact = castlingImpact(board, "e8");

        assertNotNull(impact);
        assertEquals(-512, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackRook("a8")
                .withWhiteKing("e1")
                .build();

        var impact = castlingImpact(board, "e8");

        assertNotNull(impact);
        assertEquals("CASTLING:QUEEN:[MOTION:Ke8->c8] [MOTION:Ra8->d8]", String.valueOf(impact));
    }

    private static PieceCastlingImpact<?,?,?> castlingImpact(Board board, String piecePosition) {
        return Stream.of(getImpact(board, piecePosition, Impact.Type.CASTLING))
                .flatMap(Optional::stream)
                .map(impact -> (PieceCastlingImpact<?,?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}