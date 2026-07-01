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
public class PieceUnderminingImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteUnderminingImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("f8")
                .withBlackBishop("d5")
                .withBlackKnight("e7")
                .withBlackPawn("a6")
                .withWhiteKing("d4")
                .withWhiteRook("a7")
                .withWhitePawns("a3","b2","c3")
                .build();

        var impact = underminingImpact(board, "a7");
        assertNotNull(impact);
        assertEquals(-4, impact.getValue());
    }

    @Test
    void testBlackUnderminingImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("d5")
                .withBlackRook("a2")
                .withBlackPawns("a6","b7","c6")
                .withWhiteKing("f1")
                .withWhiteBishop("d4")
                .withWhiteKnight("e2")
                .withWhitePawn("a3")
                .build();

        var impact = underminingImpact(board, "a2");
        assertNotNull(impact);
        assertEquals(4, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e7")
                .withBlackRook("b6")
                .withBlackBishop("g5")
                .withBlackKnight("a4")
                .withBlackPawns("b5","d6","e5","f7","g6","h5")
                .withWhiteKing("b1")
                .withWhiteBishop("e2")
                .withWhiteRooks("a3","h1")
                .withWhitePawns("a2","b2","f3","g2","h2")
                .build();

        var impact = underminingImpact(board, "e2");
        assertNotNull(impact);
        assertEquals("UNDERMINING:Be2_X_b5", String.valueOf(impact));
    }

    private static PieceUnderminingImpact<?,?,?,?> underminingImpact(Board board, String piecePosition) {
        return Stream.of(getImpact(board, piecePosition, Impact.Type.UNDERMINING))
                .flatMap(Optional::stream)
                .map(impact -> (PieceUnderminingImpact<?,?,?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}