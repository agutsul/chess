package com.agutsul.chess.board.state;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.BoardBuilder;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

@ExtendWith(MockitoExtension.class)
// https://en.wikipedia.org/wiki/Checkmate_pattern
public class CheckMateTest {

    @Test
    void testAnastasiaMate() {
        var board = new BoardBuilder()
                .withBlackKing("h7")
                .withBlackPawn("g7")
                .withWhiteKnight("e7")
                .withWhiteRook("h5")
                .build();

        assertCheckMate(board.getPiece("h7"));
    }

    @Test
    void testAnderssenMate() {
        var board = new BoardBuilder()
                .withBlackKing("g8")
                .withWhiteRook("h8")
                .withWhitePawn("g7")
                .withWhiteKing("f6")
                .build();

        assertCheckMate(board.getPiece("g8"));
    }

    @Test
    void testArabianMate() {
        var board = new BoardBuilder()
                .withBlackKing("h8")
                .withWhiteRook("h7")
                .withWhiteKnight("f6")
                .build();

        assertCheckMate(board.getPiece("h8"));
    }

    @Test
    void testBackRankMate() {
        var board = new BoardBuilder()
                .withBlackKing("g8")
                .withBlackPawns("f7", "g7", "h7")
                .withWhiteRook("d8")
                .build();

        assertCheckMate(board.getPiece("g8"));
    }

    @Test
    void testBalestraMate() {
        var board = new BoardBuilder()
                .withBlackKing("g8")
                .withWhiteQueen("h6")
                .withWhiteBishop("e6")
                .build();

        assertCheckMate(board.getPiece("g8"));
    }

    @Test
    void testBishopAndKnightMate() {
        var board = new BoardBuilder()
                .withBlackKing("h8")
                .withWhiteKnight("h6")
                .withWhiteBishop("f6")
                .withWhiteKing("g6")
                .build();

        assertCheckMate(board.getPiece("h8"));
    }

    @Test
    void testBlackburneMate1() {
        var board = new BoardBuilder()
                .withBlackKing("g8")
                .withBlackRook("f8")
                .withWhiteBishops("h7", "b2")
                .withWhiteKnight("g5")
                .build();

        assertCheckMate(board.getPiece("g8"));
    }

    @Test
    void testBlackburneMate2() {
        var board = new BoardBuilder()
                .withBlackKing("h8")
                .withWhiteBishops("f7", "b2")
                .withWhiteKnight("g5")
                .build();

        assertCheckMate(board.getPiece("h8"));
    }

    @Test
    void testBlindSwineMate() {
        var board = new BoardBuilder()
                .withBlackKing("g8")
                .withBlackRook("f8")
                .withWhiteRooks("g7", "h7")
                .build();

        assertCheckMate(board.getPiece("g8"));
    }

    @Test
    void testBodenMate() {
        var board = new BoardBuilder()
                .withBlackKing("c8")
                .withBlackRook("d8")
                .withBlackPawn("d7")
                .withWhiteBishops("a6", "f4")
                .build();

        assertCheckMate(board.getPiece("c8"));
    }

    @Test
    void testCornerMate() {
        var board = new BoardBuilder()
                .withBlackKing("h8")
                .withBlackPawn("h7")
                .withWhiteKnight("f7")
                .withWhiteRook("g1")
                .build();

        assertCheckMate(board.getPiece("h8"));
    }

    @Test
    void testDamianoBishopMate() {
        var board = new BoardBuilder()
                .withBlackKing("f8")
                .withWhiteQueen("f7")
                .withWhiteBishop("g6")
                .build();

        assertCheckMate(board.getPiece("f8"));
    }

    @Test
    void testDamianoMate() {
        var board = new BoardBuilder()
                .withBlackKing("g8")
                .withBlackRook("f8")
                .withBlackPawn("g7")
                .withWhiteQueen("h7")
                .withWhitePawn("g6")
                .build();

        assertCheckMate(board.getPiece("g8"));
    }

    @Test
    void testDoubleBishopMate1() {
        var board = new BoardBuilder()
                .withBlackKing("h8")
                .withBlackPawn("h7")
                .withWhiteBishops("c3", "d5")
                .build();

        assertCheckMate(board.getPiece("h8"));
    }

    @Test
    void testDoubleBishopMate2() {
        var board = new BoardBuilder()
                .withBlackKing("e5")
                .withBlackPawn("f6")
                .withWhiteBishops("c7", "e4")
                .withWhiteKing("d3")
                .withWhitePawn("f5")
                .build();

        assertCheckMate(board.getPiece("e5"));
    }

    @Test
    void testDoubleKnightMate() {
        var board = new BoardBuilder()
                .withBlackKing("a8")
                .withBlackPawns("a7", "b7")
                .withBlackKnight("c8")
                .withWhiteKnights("c7", "d7")
                .build();

        assertCheckMate(board.getPiece("a8"));
    }

    @Test
    void testDovetailMate() {
        var board = new BoardBuilder()
                .withBlackKing("g3")
                .withBlackPawn("g4")
                .withBlackQueen("f3")
                .withWhiteQueen("h2")
                .withWhiteKing("g1")
                .build();

        assertCheckMate(board.getPiece("g3"));
    }

    @Test
    void testEpauletteMate() {
        var board = new BoardBuilder()
                .withBlackKing("g8")
                .withBlackRooks("h8", "f8")
                .withWhiteQueen("g6")
                .build();

        assertCheckMate(board.getPiece("g8"));
    }

    @Test
    void testGrecoMate() {
        var board = new BoardBuilder()
                .withBlackKing("h8")
                .withBlackPawn("g7")
                .withWhiteQueen("h5")
                .withWhiteBishop("c4")
                .build();

        assertCheckMate(board.getPiece("h8"));
    }

    @Test
    void testHookMate() {
        var board = new BoardBuilder()
                .withBlackKing("e7")
                .withBlackPawn("f7")
                .withWhiteRook("e8")
                .withWhitePawn("e5")
                .withWhiteKnight("f6")
                .build();

        assertCheckMate(board.getPiece("e7"));
    }

    @Test
    void testKillBoxMate() {
        var board = new BoardBuilder()
                .withBlackKing("a5")
                .withWhiteRook("a6")
                .withWhiteQueen("c4")
                .build();

        assertCheckMate(board.getPiece("a5"));
    }

    @Test
    void testKingWithTwoBishopsMate() {
        var board = new BoardBuilder()
                .withBlackKing("h8")
                .withWhiteKing("h6")
                .withWhiteBishops("f6", "e6")
                .build();

        assertCheckMate(board.getPiece("h8"));
    }

    @Test
    void testKingWithTwoKnightsMate() {
        var board = new BoardBuilder()
                .withBlackKing("h8")
                .withWhiteKing("h6")
                .withWhiteKnights("f6", "g6")
                .build();

        assertCheckMate(board.getPiece("h8"));
    }

    @Test
    void testLadderMate1() {
        var board = new BoardBuilder()
                .withBlackKing("g8")
                .withWhiteRooks("a8", "b7")
                .build();

        assertCheckMate(board.getPiece("g8"));
    }

    @Test
    void testLadderMate2() {
        var board = new BoardBuilder()
                .withBlackKing("g8")
                .withWhiteQueen("a8")
                .withWhiteRook("b7")
                .build();

        assertCheckMate(board.getPiece("g8"));
    }

    @Test
    void testLegalMate1() {
        var board = new BoardBuilder()
                .withBlackKing("e7")
                .withBlackQueen("d8")
                .withBlackBishop("f8")
                .withBlackPawn("d6")
                .withWhiteKnights("d5", "e5")
                .withWhiteBishop("f7")
                .build();

        assertCheckMate(board.getPiece("e7"));
    }

    @Test
    void testLegalMate2() {
        var board = new BoardBuilder()
                .withBlackKing("e7")
                .withBlackQueen("d8")
                .withBlackBishop("f8")
                .withBlackPawn("d6")
                .withWhiteKnight("e5")
                .withWhiteBishops("f7", "g5")
                .build();

        assertCheckMate(board.getPiece("e7"));
    }

    @Test
    void testLolliMate() {
        var board = new BoardBuilder()
                .withBlackKing("g8")
                .withBlackPawns("f7", "g6")
                .withWhitePawn("f6")
                .withWhiteQueen("g7")
                .build();

        assertCheckMate(board.getPiece("g8"));
    }

    @Test
    void testMaxLangeMate() {
        var board = new BoardBuilder()
                .withBlackKing("h7")
                .withBlackPawns("h6", "g7")
                .withWhiteBishop("f7")
                .withWhiteQueen("g8")
                .build();

        assertCheckMate(board.getPiece("h7"));
    }

    @Test
    void testMayetMate() {
        var board = new BoardBuilder()
                .withBlackKing("g8")
                .withBlackPawn("f7")
                .withWhiteBishop("b2")
                .withWhiteRook("h8")
                .build();

        assertCheckMate(board.getPiece("g8"));
    }

    @Test
    void testMorphyMate() {
        var board = new BoardBuilder()
                .withBlackKing("h8")
                .withBlackPawn("h7")
                .withWhiteBishop("f6")
                .withWhiteRook("g1")
                .build();

        assertCheckMate(board.getPiece("h8"));
    }

    @Test
    void testOperaMate() {
        var board = new BoardBuilder()
                .withBlackKing("e8")
                .withBlackPawn("f7")
                .withWhiteBishop("g5")
                .withWhiteRook("d8")
                .build();

        assertCheckMate(board.getPiece("e8"));
    }

    @Test
    void testPawnMate() {
        var board = new BoardBuilder()
                .withBlackKing("b4")
                .withBlackPawns("a4", "c4")
                .withBlackKnight("b3")
                .withWhiteRook("h5")
                .withWhitePawns("a3", "b2")
                .build();

        assertCheckMate(board.getPiece("b4"));
    }

    @Test
    void testPillsburyMate() {
        var board = new BoardBuilder()
                .withBlackKing("g8")
                .withBlackRook("f8")
                .withBlackPawns("f7", "h7")
                .withWhiteRook("g1")
                .withWhiteBishop("f6")
                .build();

        assertCheckMate(board.getPiece("g8"));
    }

    @Test
    void testQueenMate() {
        var board = new BoardBuilder()
                .withBlackKing("d8")
                .withWhiteQueen("d7")
                .withWhiteKing("d6")
                .build();

        assertCheckMate(board.getPiece("d8"));
    }

    @Test
    void testRetiMate() {
        var board = new BoardBuilder()
                .withBlackKing("c7")
                .withBlackPawns("c6", "b7")
                .withBlackBishop("c8")
                .withBlackKnight("b8")
                .withWhiteRook("d1")
                .withWhiteBishop("d8")
                .build();

        assertCheckMate(board.getPiece("c7"));
    }

    @Test
    void testRookMate() {
        var board = new BoardBuilder()
                .withBlackKing("d8")
                .withWhiteRook("a8")
                .withWhiteKing("d6")
                .build();

        assertCheckMate(board.getPiece("d8"));
    }

    @Test
    void testSmotheredMate() {
        var board = new BoardBuilder()
                .withBlackKing("h8")
                .withBlackRook("g8")
                .withBlackPawns("h7", "g7")
                .withWhiteKnight("f7")
                .build();

        assertCheckMate(board.getPiece("h8"));
    }

    @Test
    void testSuffocationMate() {
        var board = new BoardBuilder()
                .withBlackKing("g8")
                .withBlackRook("f8")
                .withBlackPawns("h7", "f7")
                .withWhiteKnight("e7")
                .withWhiteBishop("c3")
                .build();

        assertCheckMate(board.getPiece("g8"));
    }

    @Test
    void testSwallowTailMate1() {
        var board = new BoardBuilder()
                .withBlackKing("e7")
                .withBlackRooks("d8", "f8")
                .withWhiteRook("a6")
                .withWhiteQueen("e6")
                .build();

        assertCheckMate(board.getPiece("e7"));
    }

    @Test
    void testSwallowTailMate2() {
        var board = new BoardBuilder()
                .withBlackKing("c7")
                .withBlackQueen("d8")
                .withBlackBishop("b8")
                .withWhiteBishop("f3")
                .withWhiteQueen("c6")
                .build();

        assertCheckMate(board.getPiece("c7"));
    }

    @Test
    void testTriangleMate() {
        var board = new BoardBuilder()
                .withBlackKing("e7")
                .withBlackPawn("f7")
                .withWhiteRook("d8")
                .withWhiteQueen("d6")
                .build();

        assertCheckMate(board.getPiece("e7"));
    }

    @Test
    void testVukovicMate() {
        var board = new BoardBuilder()
                .withBlackKing("e8")
                .withWhiteRook("e7")
                .withWhiteKnight("e6")
                .withWhiteKing("f6")
                .build();

        assertCheckMate(board.getPiece("e8"));
    }

    private void assertCheckMate(Optional<Piece<Color>> piece) {
        assertCheckMate((KingPiece<Color>) piece.get());
    }

    private void assertCheckMate(KingPiece<Color> king) {
        assertTrue(king.isCheckMated());
    }
}
