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
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.PawnPiece;

@ExtendWith(MockitoExtension.class)
public class PieceAttackImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteGetValueForAttackingMoreValuablePiece() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackKnight("e5")
                .withWhiteKing("e1")
                .withWhitePawn("d4")
                .build();

        var impact = attackImpact(board, "d4");
        assertNotNull(impact);

        var blackKnight = (KnightPiece<?>) board.getPiece("e5").get();
        assertEquals(blackKnight, impact.getTarget());

        assertEquals(5, impact.getValue());
    }

    @Test
    void testWhiteGetValueForAttackingLessValuablePiece() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawn("e5")
                .withWhiteKing("e1")
                .withWhiteKnight("f3")
                .build();

        var impact = attackImpact(board, "f3");
        assertNotNull(impact);

        var blackPawn = (PawnPiece<?>) board.getPiece("e5").get();
        assertEquals(blackPawn, impact.getTarget());

        assertEquals(3, impact.getValue());
    }

    @Test
    void testWhiteGetValueAttackingSameValuePiece() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackBishop("e5")
                .withWhiteKing("e1")
                .withWhiteKnight("f3")
                .build();

        var impact = attackImpact(board, "f3");
        assertNotNull(impact);

        var blackBishop = (BishopPiece<?>) board.getPiece("e5").get();
        assertEquals(blackBishop, impact.getTarget());

        assertEquals(3, impact.getValue());
    }

    @Test
    void testWhiteKingAttack() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawn("e2")
                .withWhiteKing("e1")
                .build();

        var impact = attackImpact(board, "e1");
        assertNotNull(impact);

        var blackPawn = (PawnPiece<?>) board.getPiece("e2").get();
        assertEquals(blackPawn, impact.getTarget());

        assertEquals(400, impact.getValue());
    }

    @Test
    void testBlackGetValueForAttackingMoreValuablePiece() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawn("e5")
                .withWhiteKing("e1")
                .withWhiteKnight("d4")
                .build();

        var impact = attackImpact(board, "e5");
        assertNotNull(impact);

        var whiteKnight = (KnightPiece<?>) board.getPiece("d4").get();
        assertEquals(whiteKnight, impact.getTarget());

        assertEquals(-5, impact.getValue());
    }

    @Test
    void testBlackGetValueForAttackingLessValuablePiece() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackKnight("e5")
                .withWhiteKing("e1")
                .withWhitePawn("f3")
                .build();

        var impact = attackImpact(board, "e5");
        assertNotNull(impact);

        var whitePawn = (PawnPiece<?>) board.getPiece("f3").get();
        assertEquals(whitePawn, impact.getTarget());

        assertEquals(-3, impact.getValue());
    }

    @Test
    void testBlackGetValueAttackingSameValuePiece() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackKnight("e5")
                .withWhiteKing("e1")
                .withWhiteBishop("f3")
                .build();

        var impact = attackImpact(board, "e5");
        assertNotNull(impact);

        var whiteBishop = (BishopPiece<?>) board.getPiece("f3").get();
        assertEquals(whiteBishop, impact.getTarget());

        assertEquals(-3, impact.getValue());
    }

    @Test
    void testBlackKingAttack() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withWhiteKing("e1")
                .withWhitePawn("e7")
                .build();

        var impact = attackImpact(board, "e8");
        assertNotNull(impact);

        var whitePawn = (PawnPiece<?>) board.getPiece("e7").get();
        assertEquals(whitePawn, impact.getTarget());

        assertEquals(-400, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackKnight("e5")
                .withWhiteKing("e1")
                .withWhitePawn("d4")
                .build();

        var impact = attackImpact(board, "d4");
        assertNotNull(impact);

        var blackKnight = (KnightPiece<?>) board.getPiece("e5").get();
        assertEquals(blackKnight, impact.getTarget());

        assertEquals("ATTACK:d4xNe5", String.valueOf(impact));
    }

    private static PieceAttackImpact<?,?,?,?> attackImpact(Board board, String piecePosition) {
        return Stream.of(getImpact(board, piecePosition, Impact.Type.ATTACK))
                .flatMap(Optional::stream)
                .map(impact -> (PieceAttackImpact<?,?,?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}