package com.agutsul.chess.activity.impact;

import static com.agutsul.chess.activity.impact.PieceImpendingAttackImpact.isAbsolute;
import static com.agutsul.chess.activity.impact.PieceImpendingAttackImpact.isRelative;
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
import com.agutsul.chess.board.event.ClearCachedDataEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.piece.PawnPiece;

@ExtendWith(MockitoExtension.class)
public class PieceImpendingAttackImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteRelativeEnPassantImpendingAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawns("a7","b7")
                .withWhiteKing("e1")
                .withWhitePawn("c5")
                .build();

        var blackPawn = (PawnPiece<Color>) board.getPiece("b7").get();
        blackPawn.move(board.getPosition("b5").get());

        ((Observable) board).notifyObservers(new ClearCachedDataEvent(Colors.WHITE));

        var impact = impendingAttackImpact(board, "c5");
        assertNotNull(impact);
        assertTrue(isRelative(impact));
        assertEquals(2, impact.getValue());
    }

    @Test
    void testBlackRelativeEnPassantImpendingAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawn("c4")
                .withWhiteKing("e1")
                .withWhitePawns("a2","b2")
                .build();

        var whitePawn = (PawnPiece<Color>) board.getPiece("b2").get();
        whitePawn.move(board.getPosition("b4").get());

        ((Observable) board).notifyObservers(new ClearCachedDataEvent(Colors.BLACK));

        var impact = impendingAttackImpact(board, "c4");
        assertNotNull(impact);
        assertTrue(isRelative(impact));
        assertEquals(-2, impact.getValue());
    }

    @Test
    void testWhiteAbsolutePromoteAttackImpendingAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackBishop("a8")
                .withBlackKnight("b8")
                .withWhiteKing("e1")
                .withWhitePawn("a7")
                .build();

        var impact = impendingAttackImpact(board, "a7");
        assertNotNull(impact);
        assertTrue(isAbsolute(impact));
        assertEquals(636, impact.getValue());
    }

    @Test
    void testBlackAbsolutePromoteAttackImpendingAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawn("h2")
                .withWhiteKing("e1")
                .withWhiteBishop("h1")
                .withWhiteKnight("g1")
                .build();

        var impact = impendingAttackImpact(board, "h2");
        assertNotNull(impact);
        assertTrue(isAbsolute(impact));
        assertEquals(-636, impact.getValue());
    }

    @Test
    void testWhiteAbsolutePromoteMoveImpendingAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withWhiteKing("e1")
                .withWhitePawn("a7")
                .build();

        var impact = impendingAttackImpact(board, "a7");
        assertNotNull(impact);
        assertTrue(isAbsolute(impact));
        assertEquals(632, impact.getValue());
    }

    @Test
    void testBlackAbsolutePromoteMoveImpendingAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawn("h2")
                .withWhiteKing("e1")
                .build();

        var impact = impendingAttackImpact(board, "h2");
        assertNotNull(impact);
        assertTrue(isAbsolute(impact));
        assertEquals(-632, impact.getValue());
    }

    @Test
    void testWhiteRelativeKingCastlingImpendingAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackKnight("d2")
                .withWhiteKing("e1")
                .withWhiteRook("a1")
                .build();

        var impact = impendingAttackImpact(board, "e1");
        assertNotNull(impact);
        assertTrue(isRelative(impact));
        assertEquals(401, impact.getValue());
    }

    @Test
    void testBlackRelativeKingCastlingImpendingAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackRook("a8")
                .withWhiteKing("e1")
                .withWhiteKnight("d7")
                .build();

        var impact = impendingAttackImpact(board, "e8");
        assertNotNull(impact);
        assertTrue(isRelative(impact));
        assertEquals(-401, impact.getValue());
    }

    @Test
    void testWhiteAbsoluteRookCastlingImpendingAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("f8")
                .withWhiteKing("e1")
                .withWhiteRook("h1")
                .build();

        var impact = impendingAttackImpact(board, "h1");
        assertNotNull(impact);
        assertTrue(isAbsolute(impact));
        assertEquals(796, impact.getValue());
    }

    @Test
    void testBlackAbsoluteRookCastlingImpendingAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackRook("h8")
                .withWhiteKing("f1")
                .build();

        var impact = impendingAttackImpact(board, "h8");
        assertNotNull(impact);
        assertTrue(isAbsolute(impact));
        assertEquals(-796, impact.getValue());
    }

    @Test
    void testWhiteRelativeLineImpendingAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackRook("h8")
                .withWhiteKing("e1")
                .withWhiteBishop("c1")
                .build();

        var impact = impendingAttackImpact(board, "c1");
        assertNotNull(impact);
        assertTrue(isRelative(impact));
        assertEquals(8, impact.getValue());
    }

    @Test
    void testBlackRelativeLineImpendingAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackBishop("f8")
                .withWhiteKing("d1")
                .withWhiteRook("a1")
                .build();

        var impact = impendingAttackImpact(board, "f8");
        assertNotNull(impact);
        assertTrue(isRelative(impact));
        assertEquals(-8, impact.getValue());
    }

    @Test
    void testWhiteAbsolutePositionImpendingAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h8")
                .withWhiteKing("e1")
                .withWhiteKnight("e5")
                .build();

        var impact = impendingAttackImpact(board, "e5");
        assertNotNull(impact);
        assertTrue(isAbsolute(impact));
        assertEquals(598, impact.getValue());
    }

    @Test
    void testBlackAbsolutePositionImpendingAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackKnight("c5")
                .withWhiteKing("a1")
                .build();

        var impact = impendingAttackImpact(board, "c5");
        assertNotNull(impact);
        assertTrue(isAbsolute(impact));
        assertEquals(-598, impact.getValue());
    }

    @Test
    void testWhiteRelativePositionImpendingAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawn("e5")
                .withWhiteKing("e1")
                .withWhitePawn("d2")
                .build();

        var impact = impendingAttackImpact(board, "d2");
        assertNotNull(impact);
        assertTrue(isRelative(impact));
        assertEquals(2, impact.getValue());
    }

    @Test
    void testBlackRelativePositionImpendingAttackImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawn("e7")
                .withWhiteKing("e1")
                .withWhitePawn("d4")
                .build();

        var impact = impendingAttackImpact(board, "e7");
        assertNotNull(impact);
        assertTrue(isRelative(impact));
        assertEquals(-2, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawn("e5")
                .withWhiteKing("e1")
                .withWhitePawn("d2")
                .build();

        var impact = impendingAttackImpact(board, "d2");
        assertNotNull(impact);
        assertEquals("IMPENDING_ATTACK:RELATIVE:(MOTION:d2->d4)xe5", String.valueOf(impact));
    }

    private static PieceImpendingAttackImpact<?,?,?,?> impendingAttackImpact(Board board, String piecePosition) {
        return Stream.of(getImpact(board, piecePosition, Impact.Type.IMPENDING_ATTACK))
                .flatMap(Optional::stream)
                .map(impact -> (PieceImpendingAttackImpact<?,?,?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}