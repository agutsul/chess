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
public class PieceLuftImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteLuftImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackRook("a8")
                .withWhiteKing("h1")
                .withWhitePawns("f2","g2","h2")
                .build();

        var impact = luftImpact(board, "h2");
        assertNotNull(impact);
        assertEquals(1, impact.getValue());
    }

    @Test
    void testBlackLuftImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("a8")
                .withBlackPawns("a7","b7","c7")
                .withWhiteKing("e1")
                .withWhiteRook("h1")
                .build();

        var impact = luftImpact(board, "a7");
        assertNotNull(impact);
        assertEquals(-1, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackRook("a8")
                .withWhiteKing("h1")
                .withWhitePawns("f2","g2","h2")
                .build();

        var impact = luftImpact(board, "h2");
        assertNotNull(impact);
        assertEquals("LUFT:h2-->h3", String.valueOf(impact));
    }

    private static PieceLuftImpact<?,?> luftImpact(Board board, String piecePosition) {
        var impacts = getImpact(board, piecePosition, Impact.Type.LUFT);
        return Stream.of(impacts)
                .flatMap(Optional::stream)
                .map(impact -> (PieceLuftImpact<?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}