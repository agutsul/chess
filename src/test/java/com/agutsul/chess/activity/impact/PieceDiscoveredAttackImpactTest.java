package com.agutsul.chess.activity.impact;

import static com.agutsul.chess.activity.impact.PieceDiscoveredAttackImpact.isAbsolute;
import static com.agutsul.chess.activity.impact.PieceDiscoveredAttackImpact.isRelative;
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

@ExtendWith(MockitoExtension.class)
public class PieceDiscoveredAttackImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteRelativeDiscoveredAttack() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackQueen("c8")
                .withBlackPawn("b5")
                .withWhiteKing("e1")
                .withWhiteRook("c1")
                .withWhiteBishop("c4")
                .build();

        var impact = discoveredAttackImpact(board, "c4");
        assertNotNull(impact);

        assertTrue(isRelative(impact));
        assertEquals(14, impact.getValue());

        var blackQueen = board.getPiece("c8").get();
        assertEquals(blackQueen, impact.getAttacked());

        var whiteRook = board.getPiece("c1").get();
        assertEquals(whiteRook, impact.getAttacker());
    }

    @Test
    void testBlackRelativeDiscoveredAttack() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackRook("c8")
                .withBlackBishop("c4")
                .withWhiteKing("e1")
                .withWhiteQueen("c1")
                .withWhitePawn("b3")
                .build();

        var impact = discoveredAttackImpact(board, "c4");
        assertNotNull(impact);

        assertTrue(isRelative(impact));
        assertEquals(-14, impact.getValue());

        var blackRook = board.getPiece("c8").get();
        assertEquals(blackRook, impact.getAttacker());

        var whiteQueen = board.getPiece("c1").get();
        assertEquals(whiteQueen, impact.getAttacked());
    }

    @Test
    void testWhiteAbsoluteDiscoveredAttack() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("c8")
                .withWhiteKing("e1")
                .withWhiteKnight("c4")
                .withWhiteRook("c1")
                .build();

        var impact = discoveredAttackImpact(board, "c4");
        assertNotNull(impact);

        assertTrue(isAbsolute(impact));
        assertEquals(596, impact.getValue());

        var blackKing = board.getPiece("c8").get();
        assertEquals(blackKing, impact.getAttacked());

        var whiteRook = board.getPiece("c1").get();
        assertEquals(whiteRook, impact.getAttacker());
    }

    @Test
    void testBlackAbsoluteDiscoveredAttack() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("c8")
                .withBlackRook("e8")
                .withBlackKnight("e4")
                .withWhiteKing("e1")
                .build();

        var impact = discoveredAttackImpact(board, "e4");
        assertNotNull(impact);

        assertTrue(isAbsolute(impact));
        assertEquals(-596, impact.getValue());

        var whiteKing = board.getPiece("e1").get();
        assertEquals(whiteKing, impact.getAttacked());

        var blackRook = board.getPiece("e8").get();
        assertEquals(blackRook, impact.getAttacker());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("c8")
                .withWhiteKing("e1")
                .withWhiteKnight("c4")
                .withWhiteRook("c1")
                .build();

        var impact = discoveredAttackImpact(board, "c4");
        assertNotNull(impact);

        assertEquals("DISCOVERED_ATTACK:ABSOLUTE:MOTION:Nc4->b6 CHECK:Rc1xKc8!", String.valueOf(impact));
    }

    private static PieceDiscoveredAttackImpact<?,?,?,?,?>
            discoveredAttackImpact(Board board, String piecePosition) {

        return Stream.of(getImpact(board, piecePosition, Impact.Type.DISCOVERED_ATTACK))
                .flatMap(Optional::stream)
                .map(impact -> (PieceDiscoveredAttackImpact<?,?,?,?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}