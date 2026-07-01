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
public class PieceInterferenceImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteInterferenceProtectImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackQueen("c3")
                .withBlackRooks("a8","e8")
                .withBlackBishop("a5")
                .withBlackPawns("a7","b7","c6","f7","g6","h7")
                .withWhiteKing("g1")
                .withWhiteQueen("f6")
                .withWhiteRooks("d1","f1")
                .withWhiteBishop("g2")
                .withWhitePawns("a2","b3","f2","g3","h6")
                .build();

        var impact = interferenceImpact(board, "b3");
        assertNotNull(impact);
        assertEquals(10, impact.getValue());
    }

    @Test
    void testBlackInterferenceProtectImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackQueen("b2")
                .withBlackRook("b8")
                .withBlackBishop("f5")
                .withBlackPawns("h6","g7","f7","e6","a7")
                .withWhiteKing("g1")
                .withWhiteQueen("d1")
                .withWhiteRook("a1")
                .withWhiteBishop("e2")
                .withWhitePawns("h3","g2","f2","e3","d4","a2")
                .build();

        var impact = interferenceImpact(board, "f5");
        assertNotNull(impact);
        assertEquals(-12, impact.getValue());
    }

    @Test
    void testToString() {
        // https://chessfox.com/tactical-patterns-interference/
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackQueen("c3")
                .withBlackRooks("a8","e8")
                .withBlackBishop("a5")
                .withBlackPawns("a7","b7","c6","f7","g6","h7")
                .withWhiteKing("g1")
                .withWhiteQueen("f6")
                .withWhiteRooks("d1","f1")
                .withWhiteBishop("g2")
                .withWhitePawns("a2","b3","f2","g3","h6")
                .build();

        var impact = interferenceImpact(board, "b3");
        assertNotNull(impact);
        assertEquals("INTERFERENCE:Ba5 b3 Qc3", String.valueOf(impact));
    }

    private static PieceInterferenceImpact<?,?,?,?,?> interferenceImpact(Board board, String piecePosition) {
        return Stream.of(getImpact(board, piecePosition, Impact.Type.INTERFERENCE))
                .flatMap(Optional::stream)
                .map(impact -> (PieceInterferenceImpact<?,?,?,?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}