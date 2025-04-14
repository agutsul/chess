package com.agutsul.chess.rule.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.concurrent.ForkJoinPool;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.journal.JournalImpl;

@ExtendWith(MockitoExtension.class)
public class InsufficientMaterialBoardStateEvaluatorTest {

    @Test
    // https://en.wikipedia.org/wiki/Draw_(chess)
    void testVidmarVsMaroczy1932() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("g4")
                .withBlackKing("f7")
                .withBlackBishop("c7")
                .build();

        assertInsufficientMaterial(board, Colors.BLACK);
    }

    // https://chess.fandom.com/wiki/Dead_Position

    @Test
    void testKingVsKing() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("e3")
                .withBlackKing("e5")
                .build();

        assertInsufficientMaterial(board, Colors.WHITE);
    }

    @Test
    void testKingAndBishopVsKing() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("f7")
                .withWhiteKing("e5")
                .withWhiteBishop("c3")
                .build();

        assertInsufficientMaterial(board, Colors.WHITE);
    }

    @Test
    void testKingAndKnightVsKing() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("f7")
                .withWhiteKing("e5")
                .withWhiteKnight("c3")
                .build();

        assertInsufficientMaterial(board, Colors.WHITE);
    }

    @Test
    void testKingAndBishopsVsKing() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("f6")
                .withBlackKing("c3")
                .withBlackBishops("c2","c4")
                .build();

        assertInsufficientMaterial(board, Colors.BLACK);
    }

    @Test
    void testKingAndBishopVsKingAndBishop() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("g1")
                .withWhiteBishop("h2")
                .withBlackKing("f3")
                .withBlackBishop("e4")
                .build();

        assertInsufficientMaterial(board, Colors.WHITE);
    }

    // https://support.chess.com/en/articles/8705277-what-does-insufficient-mating-material-mean

    @Test
    void testKingAndBishopVsKingAndKnight() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("d6")
                .withWhiteKnight("c5")
                .withBlackKing("b6")
                .withBlackBishop("a8")
                .build();

        assertInsufficientMaterial(board, Colors.WHITE);
    }

    @Test
    void testKingAndDoubleKnightsVsKing() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("g3")
                .withWhiteKnights("f4","f5")
                .withBlackKing("c1")
                .build();

        assertInsufficientMaterial(board, Colors.WHITE);
    }

    @Test
    @Disabled
    // https://lichess.org/forum/lichess-feedback/if-your-only-legal-move-is-checkmate-but-you-run-out-of-time-you-still-lose
    void testNoLegalActionsLeadToCheckmate() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("a8")
                .withWhitePawns("a7","b6","c7","d6","d5")
                .withBlackKing("c8")
                .withBlackQueen("a1")
                .withBlackPawn("d7")
                .build();

//      System.out.println(board);

// -Xms5120m -Xmx5120m
        assertInsufficientMaterial(board, Colors.BLACK);
    }

    private static void assertInsufficientMaterial(Board board, Color color) {
        try (var pool = new ForkJoinPool()) {
            var evaluator = new InsufficientMaterialBoardStateEvaluator(board, new JournalImpl(), pool);
            assertBoardState(evaluator.evaluate(color));
        }
    }

    private static void assertBoardState(Optional<BoardState> boardState) {
        assertTrue(boardState.isPresent());
        assertEquals(BoardState.Type.INSUFFICIENT_MATERIAL, boardState.get().getType());
    }
}