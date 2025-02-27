package com.agutsul.chess.piece.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.board.StringBoardBuilder;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.Piece.Type;
import com.agutsul.chess.piece.impl.AbstractPieceTest;

@ExtendWith(MockitoExtension.class)
public class BishopPieceImplTest extends AbstractPieceTest {

    private static final Type BISHOP_TYPE = Piece.Type.BISHOP;

    @Test
    void testDefaultBishopActionsOnStandardBoard() {
        var expectedPositions = List.of("c1", "f1", "c8", "f8");

        var board = new StandardBoard();
        var pieces = board.getPieces(BISHOP_TYPE);
        assertEquals(pieces.size(), expectedPositions.size());

        var positions = pieces.stream()
                .map(Piece::getPosition)
                .map(String::valueOf)
                .toList();

        assertTrue(positions.containsAll(expectedPositions));

        assertPieceActions(board, Colors.WHITE, BISHOP_TYPE, expectedPositions.get(0));
        assertPieceActions(board, Colors.WHITE, BISHOP_TYPE, expectedPositions.get(1));

        assertPieceActions(board, Colors.BLACK, BISHOP_TYPE, expectedPositions.get(2));
        assertPieceActions(board, Colors.BLACK, BISHOP_TYPE, expectedPositions.get(3));
    }

    @Test
    void testDefaultBishopActionsOnEmptyBoard() {
        var board1 = new StringBoardBuilder().withWhiteBishop("c1").build();
        assertPieceActions(board1, Colors.WHITE, BISHOP_TYPE, "c1",
                List.of("b2", "a3", "d2", "e3", "f4", "g5", "h6"));

        var board2 = new StringBoardBuilder().withWhiteBishop("f1").build();
        assertPieceActions(board2, Colors.WHITE, BISHOP_TYPE, "f1",
                List.of("g2", "h3", "e2", "d3", "c4", "b5", "a6"));

        var board3 = new StringBoardBuilder().withBlackBishop("c8").build();
        assertPieceActions(board3, Colors.BLACK, BISHOP_TYPE, "c8",
                List.of("b7", "a6", "d7", "e6", "f5", "g4", "h3"));

        var board4 = new StringBoardBuilder().withBlackBishop("f8").build();
        assertPieceActions(board4, Colors.BLACK, BISHOP_TYPE, "f8",
                List.of("g7", "h6", "e7", "d6", "c5", "b4", "a3"));
    }

    @Test
    void testRandomBishopActionsOnEmptyBoard() {
        var board1 = new StringBoardBuilder().withWhiteBishop("d4").build();
        assertPieceActions(board1, Colors.WHITE, BISHOP_TYPE, "d4",
                List.of("c3", "b2", "a1", "e5", "f6", "g7", "h8", "e3", "f2", "g1", "c5", "b6", "a7"));

        var board2 = new StringBoardBuilder().withBlackBishop("d5").build();
        assertPieceActions(board2, Colors.BLACK, BISHOP_TYPE, "d5",
                List.of("e4", "f3", "g2", "h1", "c6", "b7", "a8", "e6", "f7", "g8", "c4", "b3", "a2"));
    }

    @Test
    void testBishopCaptureActionOnEmptyBoard() {
        var board1 = new StringBoardBuilder()
                .withBlackPawn("a3")
                .withWhiteBishop("c1")
                .build();

        assertPieceActions(board1, Colors.WHITE, BISHOP_TYPE, "c1",
                List.of("b2", "d2", "e3", "f4", "g5", "h6"), List.of("a3"));

        var board2 = new StringBoardBuilder()
                .withWhitePawn("h6")
                .withBlackBishop("f8")
                .build();

        assertPieceActions(board2, Colors.BLACK, BISHOP_TYPE, "f8",
                List.of("g7", "e7", "d6", "c5", "b4", "a3"), List.of("h6"));
    }

    @Test
    void testBishopActionAfterDisposing() {
        var board1 = new StringBoardBuilder()
                .withWhiteBishop("c1")
                .withWhitePawn("b2")
                .build();

        var whiteBishop = board1.getPiece("c1").get();
        assertFalse(board1.getActions(whiteBishop).isEmpty());
        assertFalse(board1.getImpacts(whiteBishop).isEmpty());

        ((BishopPiece<Color>) whiteBishop).dispose(null);

        assertTrue(board1.getActions(whiteBishop).isEmpty());
        assertTrue(board1.getImpacts(whiteBishop).isEmpty());

        var board2 = new StringBoardBuilder()
                .withBlackBishop("c8")
                .withBlackPawn("b7")
                .build();

        var blackBishop = board2.getPiece("c8").get();
        assertFalse(board2.getActions(blackBishop).isEmpty());
        assertFalse(board2.getImpacts(blackBishop).isEmpty());

        ((BishopPiece<Color>) blackBishop).dispose(null);

        assertTrue(board2.getActions(blackBishop).isEmpty());
        assertTrue(board2.getImpacts(blackBishop).isEmpty());
    }

    @Test
    void testBishopZeroActionForPinnedPiece() {
        var board = new StringBoardBuilder()
                .withWhiteBishop("e4")
                .withWhiteKing("e3")
                .withBlackRook("e8")
                .withBlackKing("c7")
                .build();

        var whiteBishop = board.getPiece("e4").get();
        var bishopActions = board.getActions(whiteBishop);
        assertTrue(bishopActions.isEmpty());
    }
}