package com.agutsul.chess.rule.board;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;

@ExtendWith(MockitoExtension.class)
// https://en.wikipedia.org/wiki/Checkmate_pattern
public class CheckMatedBoardStateEvaluatorTest {

    @Test
    void testIsCheckMated() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h8")
                .withBlackBishop("g8")
                .withWhiteBishop("e5")
                .withWhiteKing("g6")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testAnastasiaMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h7")
                .withBlackPawn("g7")
                .withWhiteKnight("e7")
                .withWhiteRook("h5")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testAnderssenMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withWhiteRook("h8")
                .withWhitePawn("g7")
                .withWhiteKing("f6")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testArabianMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h8")
                .withWhiteRook("h7")
                .withWhiteKnight("f6")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testBackRankMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackPawns("f7", "g7", "h7")
                .withWhiteRook("d8")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testBalestraMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withWhiteQueen("h6")
                .withWhiteBishop("e6")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testBishopAndKnightMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h8")
                .withWhiteKnight("h6")
                .withWhiteBishop("f6")
                .withWhiteKing("g6")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testBlackburneMate1() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackRook("f8")
                .withWhiteBishops("h7", "b2")
                .withWhiteKnight("g5")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testBlackburneMate2() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h8")
                .withWhiteBishops("f7", "b2")
                .withWhiteKnight("g5")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testBlindSwineMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackRook("f8")
                .withWhiteRooks("g7", "h7")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testBodenMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("c8")
                .withBlackRook("d8")
                .withBlackPawn("d7")
                .withWhiteBishops("a6", "f4")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testCornerMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h8")
                .withBlackPawn("h7")
                .withWhiteKnight("f7")
                .withWhiteRook("g1")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testDamianoBishopMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("f8")
                .withWhiteQueen("f7")
                .withWhiteBishop("g6")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testDamianoMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackRook("f8")
                .withBlackPawn("g7")
                .withWhiteQueen("h7")
                .withWhitePawn("g6")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testDoubleBishopMate1() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h8")
                .withBlackPawn("h7")
                .withWhiteBishops("c3", "d5")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testDoubleBishopMate2() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e5")
                .withBlackPawn("f6")
                .withWhiteBishops("c7", "e4")
                .withWhiteKing("d3")
                .withWhitePawn("f5")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testDoubleKnightMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("a8")
                .withBlackPawns("a7", "b7")
                .withBlackKnight("c8")
                .withWhiteKnights("c7", "d7")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testDovetailMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g3")
                .withBlackPawn("g4")
                .withBlackQueen("f3")
                .withWhiteQueen("h2")
                .withWhiteKing("g1")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testEpauletteMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackRooks("h8", "f8")
                .withWhiteQueen("g6")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testGrecoMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h8")
                .withBlackPawn("g7")
                .withWhiteQueen("h5")
                .withWhiteBishop("c4")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testHookMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e7")
                .withBlackPawn("f7")
                .withWhiteRook("e8")
                .withWhitePawn("e5")
                .withWhiteKnight("f6")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testKillBoxMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("a5")
                .withWhiteRook("a6")
                .withWhiteQueen("c4")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testKingWithTwoBishopsMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h8")
                .withWhiteKing("h6")
                .withWhiteBishops("f6", "e6")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testKingWithTwoKnightsMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h8")
                .withWhiteKing("h6")
                .withWhiteKnights("f6", "g6")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testLadderMate1() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withWhiteRooks("a8", "b7")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testLadderMate2() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withWhiteQueen("a8")
                .withWhiteRook("b7")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testLegalMate1() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e7")
                .withBlackQueen("d8")
                .withBlackBishop("f8")
                .withBlackPawn("d6")
                .withWhiteKnights("d5", "e5")
                .withWhiteBishop("f7")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testLegalMate2() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e7")
                .withBlackQueen("d8")
                .withBlackBishop("f8")
                .withBlackPawn("d6")
                .withWhiteKnight("e5")
                .withWhiteBishops("f7", "g5")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testLolliMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackPawns("f7", "g6")
                .withWhitePawn("f6")
                .withWhiteQueen("g7")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testMaxLangeMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h7")
                .withBlackPawns("h6", "g7")
                .withWhiteBishop("f7")
                .withWhiteQueen("g8")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testMayetMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackPawn("f7")
                .withWhiteBishop("b2")
                .withWhiteRook("h8")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testMorphyMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h8")
                .withBlackPawn("h7")
                .withWhiteBishop("f6")
                .withWhiteRook("g1")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testOperaMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withBlackPawn("f7")
                .withWhiteBishop("g5")
                .withWhiteRook("d8")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testPawnMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("b4")
                .withBlackPawns("a4", "c4")
                .withBlackKnight("b3")
                .withWhiteRook("h5")
                .withWhitePawns("a3", "b2")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testPillsburyMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackRook("f8")
                .withBlackPawns("f7", "h7")
                .withWhiteRook("g1")
                .withWhiteBishop("f6")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testQueenMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("d8")
                .withWhiteQueen("d7")
                .withWhiteKing("d6")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testRetiMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("c7")
                .withBlackPawns("c6", "b7")
                .withBlackBishop("c8")
                .withBlackKnight("b8")
                .withWhiteRook("d1")
                .withWhiteBishop("d8")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testRookMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("d8")
                .withWhiteRook("a8")
                .withWhiteKing("d6")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testSmotheredMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h8")
                .withBlackRook("g8")
                .withBlackPawns("h7", "g7")
                .withWhiteKnight("f7")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testSuffocationMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("g8")
                .withBlackRook("f8")
                .withBlackPawns("h7", "f7")
                .withWhiteKnight("e7")
                .withWhiteBishop("c3")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testSwallowTailMate1() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e7")
                .withBlackRooks("d8", "f8")
                .withWhiteRook("a6")
                .withWhiteQueen("e6")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testSwallowTailMate2() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("c7")
                .withBlackQueen("d8")
                .withBlackBishop("b8")
                .withWhiteBishop("f3")
                .withWhiteQueen("c6")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testTriangleMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e7")
                .withBlackPawn("f7")
                .withWhiteRook("d8")
                .withWhiteQueen("d6")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testVukovicMate() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withWhiteRook("e7")
                .withWhiteKnight("e6")
                .withWhiteKing("f6")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    // https://www.chessgames.com/perl/chesscollection?cid=1039049
    void testDoubleCheckMate1() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h8")
                .withBlackQueen("c8")
                .withBlackRooks("a8","f8")
                .withBlackBishop("e7")
                .withBlackKnight("e4")
                .withBlackPawns("a7","b6","e6","h7")
                .withWhiteKing("c1")
                .withWhiteQueen("h5")
                .withWhiteRook("g1")
                .withWhiteBishop("d4")
                .withWhiteKnight("f7")
                .withWhitePawns("a2","b2","c2","f2","h2")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    @Test
    void testDoubleCheckMate2() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("a6")
                .withBlackQueen("d8")
                .withBlackRooks("a8","h8")
                .withBlackKnights("c6","g8")
                .withBlackBishop("c8")
                .withBlackPawns("a5","b7","d7","e5","g5","h5")
                .withWhiteKing("e1")
                .withWhiteQueen("d6")
                .withWhiteRooks("a1","h1")
                .withWhiteBishops("c4","e3")
                .withWhiteKnights("c7","g1")
                .withWhitePawns("a3","c2","d3","f2","f5","g2","h2")
                .build();

        assertCheckMate(board, Colors.BLACK);
    }

    private static void assertCheckMate(Board board, Color color) {
        var checkMateEvaluator = new CheckMatedBoardStateEvaluator(board);
        var boardState = checkMateEvaluator.evaluate(color);

        assertTrue(boardState.isPresent());
        assertTrue(boardState.get().isType(BoardState.Type.CHECK_MATED));
    }
}