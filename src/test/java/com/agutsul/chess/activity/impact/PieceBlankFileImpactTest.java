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
public class PieceBlankFileImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteRelativeBlankFileImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g7")
                .withBlackPawns("a7","b7","d5","e6","f7","g6","h7")
                .withWhiteKing("g2")
                .withWhiteRook("c1")
                .withWhitePawns("a2","b2","c3","d4","e3","f2","g3","h2")
                .build();

        var impact = blankFileImpact(board, "c1");

        assertNotNull(impact);
        assertEquals(1, impact.getValue());
    }

    @Test
    void testBlackRelativeBlankFileImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g7")
                .withBlackRook("c8")
                .withBlackPawns("a7","b7","c5","d5","e6","f7","g6","h7")
                .withWhiteKing("g2")
                .withWhitePawns("a2","b2","d4","e3","f2","g3","h2")
                .build();

        var impact = blankFileImpact(board, "c8");

        assertNotNull(impact);
        assertEquals(-1, impact.getValue());
    }

    @Test
    void testWhiteAbsoluteBlankFileImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g7")
                .withBlackPawns("a7","b7","d5","e6","f7","g6","h7")
                .withWhiteKing("g2")
                .withWhiteRook("c1")
                .withWhitePawns("a2","b2","d4","e3","f2","g3","h2")
                .build();

        var impact = blankFileImpact(board, "c1");

        assertNotNull(impact);
        assertEquals(12, impact.getValue());
    }

    @Test
    void testBlackAbsoluteBlankFileImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g7")
                .withBlackRook("c8")
                .withBlackPawns("a7","b7","d5","e6","f7","g6","h7")
                .withWhiteKing("g2")
                .withWhitePawns("a2","b2","d4","e3","f2","g3","h2")
                .build();

        var impact = blankFileImpact(board, "c8");

        assertNotNull(impact);
        assertEquals(-12, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g7")
                .withBlackPawns("a7","b7","d5","e6","f7","g6","h7")
                .withWhiteKing("g2")
                .withWhiteRook("c1")
                .withWhitePawns("a2","b2","d4","e3","f2","g3","h2")
                .build();

        var impact = blankFileImpact(board, "c1");

        assertNotNull(impact);
        assertEquals("BLANK_FILE:ABSOLUTE:Rc1 [c2,c3,c4,c5,c6,c7,c8]", String.valueOf(impact));
    }

    private static PieceBlankFileImpact<?,?> blankFileImpact(Board board, String piecePosition) {
        return Stream.of(getImpact(board, piecePosition, Impact.Type.BLANK_FILE))
                .flatMap(Optional::stream)
                .map(impact -> (PieceBlankFileImpact<?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}