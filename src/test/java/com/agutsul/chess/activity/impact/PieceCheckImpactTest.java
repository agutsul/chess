package com.agutsul.chess.activity.impact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.piece.KingPiece;

@ExtendWith(MockitoExtension.class)
public class PieceCheckImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteKnightAndRookChecks() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h5")
                .withBlackRook("c8")
                .withWhiteKing("f5")
                .withWhiteRook("g5")   // g4
                .withWhiteKnight("f6") // e4
                .withWhitePawn("h3")
                .build();

        var blackKing = (KingPiece<?>) board.getPiece("h5").get();

        var knightImpact = checkImpact(board, "f6");
        assertNotNull(knightImpact);
        assertEquals(blackKing, knightImpact.getTarget());

        var rookImpact = checkImpact(board, "g5");
        assertNotNull(rookImpact);
        assertEquals(blackKing, rookImpact.getTarget());

        assertTrue(knightImpact.getValue() > rookImpact.getValue()); // 797 vs 795
    }

    @Test
    void testWhiteKingCheckByProtectedPiece() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackRook("d8")
                .withBlackPawn("d2")
                .withWhiteKing("e1")
                .build();

        var impact = checkImpact(board, "d2");

        assertNotNull(impact);
        assertEquals(-799, impact.getValue());

        var whiteKing = (KingPiece<?>) board.getPiece("e1").get();
        assertEquals(whiteKing, impact.getTarget());
    }

    @Test
    void testBlackKingCheckByProtectedPiece() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withWhiteKing("e1")
                .withWhiteRook("d1")
                .withWhitePawn("d7")
                .build();

        var impact = checkImpact(board, "d7");

        assertNotNull(impact);
        assertEquals(799, impact.getValue());

        var blackKing = (KingPiece<?>) board.getPiece("e8").get();
        assertEquals(blackKing, impact.getTarget());
    }

    @Test
    void testWhiteKingCheck() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawn("d2")
                .withWhiteKing("e1")
                .build();

        var impact = checkImpact(board, "d2");

        assertNotNull(impact);
        assertEquals(-599, impact.getValue());

        var whiteKing = (KingPiece<?>) board.getPiece("e1").get();
        assertEquals(whiteKing, impact.getTarget());
    }

    @Test
    void testBlackKingCheck() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withWhiteKing("e1")
                .withWhitePawn("d7")
                .build();

        var impact = checkImpact(board, "d7");

        assertNotNull(impact);
        assertEquals(599, impact.getValue());

        var blackKing = (KingPiece<?>) board.getPiece("e8").get();
        assertEquals(blackKing, impact.getTarget());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withWhiteKing("e1")
                .withWhitePawn("d7")
                .build();

        var impact = checkImpact(board, "d7");
        assertNotNull(impact);

        var blackKing = (KingPiece<?>) board.getPiece("e8").get();
        assertEquals(blackKing, impact.getTarget());

        assertEquals("CHECK:d7xKe8!", String.valueOf(impact));
    }

    private static PieceCheckImpact<?,?,?,?> checkImpact(Board board, String piecePosition) {
        return Stream.of(getImpact(board, piecePosition, Impact.Type.CHECK))
                .flatMap(Optional::stream)
                .map(impact -> (PieceCheckImpact<?,?,?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}