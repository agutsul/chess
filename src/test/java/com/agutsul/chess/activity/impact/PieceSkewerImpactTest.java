package com.agutsul.chess.activity.impact;

import static com.agutsul.chess.activity.impact.PieceSkewerImpact.isAbsolute;
import static com.agutsul.chess.activity.impact.PieceSkewerImpact.isRelative;
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
public class PieceSkewerImpactTest extends AbstractImpactTest {
/*
    @Test
    void testWhiteRelativeSkewerImpact2() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackQueen("d5")
                .withBlackRook("f8")
                .withBlackKnight("b7")
                .withBlackPawns("a6","b5","c7","f7","g7","h7")
                .withWhiteKing("g1")
                .withWhiteQueen("f1")
                .withWhiteRook("b1")
                .withWhiteBishop("e2")
                .withWhitePawns("a5","b4","f2","g2","h2")
                .build();

        System.out.println(board);

        for (var impact : board.getImpacts(board.getPiece("e2").get())) {
            System.out.println(impact + ":" + impact.getValue());
        }

//        var whiteBishop = board.getPiece("c4").get();
//        var impact = Stream.of(board.getImpacts(whiteBishop, Impact.Type.FORK))
//                .flatMap(Collection::stream)
//                .findFirst()
//                .get();
//
//        assertNotNull(impact);
//        assertEquals(13, impact.getValue()); // fork

//        var impact = skewerImpact(board, "f3");
//        assertNotNull(impact);

//        assertTrue(isRelative(impact));
//        assertEquals(18, impact.getValue()); // skewer
    }
*/
    @Test
    void testWhiteSkewerImpactWithProtectedPiece() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawns("a4","b5")
                .withBlackBishop("d4")
                .withWhiteKing("e1")
                .withWhiteQueen("h4")
                .build();

        var impact = skewerImpact(board, "h4");
        assertNotNull(impact);

        assertTrue(isRelative(impact));
        assertEquals(2, impact.getValue());

        var blackPawn = board.getPiece("a4").get();
        assertEquals(blackPawn, impact.getDefended());

        var blackBishop = board.getPiece("d4").get();
        assertEquals(blackBishop, impact.getAttacked());
    }

    @Test
    void testBlackSkewerImpactWithProtectedPiece() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackQueen("h8")
                .withWhiteKing("e1")
                .withWhiteKnight("e5")
                .withWhitePawns("c3","b2")
                .build();

        var impact = skewerImpact(board, "h8");
        assertNotNull(impact);

        assertTrue(isRelative(impact));
        assertEquals(-2, impact.getValue());

        var whitePawn = board.getPiece("c3").get();
        assertEquals(whitePawn, impact.getDefended());

        var whiteKnight = board.getPiece("e5").get();
        assertEquals(whiteKnight, impact.getAttacked());
    }

    @Test
    void testWhiteAbsoluteSkewerImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackPawn("a4")
                .withBlackKing("c4")
                .withWhiteKing("e1")
                .withWhiteQueen("h4")
                .build();

        var impact = skewerImpact(board, "h4");
        assertNotNull(impact);

        assertTrue(isAbsolute(impact));
        assertEquals(990, impact.getValue());

        var blackPawn = board.getPiece("a4").get();
        assertEquals(blackPawn, impact.getDefended());

        var blackKing = board.getPiece("c4").get();
        assertEquals(blackKing, impact.getAttacked());
    }

    @Test
    void testBlackAbsoluteSkewerImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackQueen("h8")
                .withWhiteKing("e5")
                .withWhitePawn("c3")
                .build();

        var impact = skewerImpact(board, "h8");
        assertNotNull(impact);

        assertTrue(isAbsolute(impact));
        assertEquals(-990, impact.getValue());

        var whitePawn = board.getPiece("c3").get();
        assertEquals(whitePawn, impact.getDefended());

        var whiteKing = board.getPiece("e5").get();
        assertEquals(whiteKing, impact.getAttacked());
    }

    @Test
    void testBlackRelativeSkewerImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackQueen("h8")
                .withWhiteKing("e1")
                .withWhiteKnight("e5")
                .withWhitePawn("c3")
                .build();

        var impact = skewerImpact(board, "h8");
        assertNotNull(impact);

        assertTrue(isRelative(impact));
        assertEquals(-11, impact.getValue());

        var whitePawn = board.getPiece("c3").get();
        assertEquals(whitePawn, impact.getDefended());

        var whiteKnight = board.getPiece("e5").get();
        assertEquals(whiteKnight, impact.getAttacked());
    }

    @Test
    void testWhiteRelativeSkewerImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawn("a4")
                .withBlackBishop("c4")
                .withWhiteKing("e1")
                .withWhiteQueen("h4")
                .build();

        var impact = skewerImpact(board, "h4");
        assertNotNull(impact);

        assertTrue(isRelative(impact));
        assertEquals(11, impact.getValue());

        var blackPawn = board.getPiece("a4").get();
        assertEquals(blackPawn, impact.getDefended());

        var blackBishop = board.getPiece("c4").get();
        assertEquals(blackBishop, impact.getAttacked());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackQueen("h8")
                .withWhiteKing("e1")
                .withWhiteKnight("e5")
                .withWhitePawn("c3")
                .build();

        var impact = skewerImpact(board, "h8");
        assertNotNull(impact);

        assertEquals("SKEWER:RELATIVE:ATTACK:Qh8xNe5 c3", String.valueOf(impact));
    }

    private static PieceSkewerImpact<?,?,?,?,?> skewerImpact(Board board, String piecePosition) {
        return Stream.of(getImpact(board, piecePosition, Impact.Type.SKEWER))
                .flatMap(Optional::stream)
                .map(impact -> (PieceSkewerImpact<?,?,?,?,?>) impact)
                .findFirst()
                .orElse(null);
    }
}