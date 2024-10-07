package com.agutsul.chess.piece;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.Colors;
import com.agutsul.chess.board.BoardBuilder;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.piece.Piece.Type;

@ExtendWith(MockitoExtension.class)
public class KnightPieceImplTest extends AbstractPieceTest {

    private static final Type KNIGHT_TYPE = Piece.Type.KNIGHT;

    @Test
    void testDefaultKnightActionsOnStandardBoard() {
        var expectedPositions = List.of("b1", "g1", "b8", "g8");

        var board = new StandardBoard();
        var pieces = board.getPieces(KNIGHT_TYPE);
        assertEquals(pieces.size(), expectedPositions.size());

        var positions = pieces.stream()
                .map(Piece::getPosition)
                .map(String::valueOf)
                .toList();

        assertTrue(positions.containsAll(expectedPositions));

        assertPieceActions(board, Colors.WHITE, KNIGHT_TYPE, expectedPositions.get(0), List.of("a3", "c3"));
        assertPieceActions(board, Colors.WHITE, KNIGHT_TYPE, expectedPositions.get(1), List.of("f3", "h3"));

        assertPieceActions(board, Colors.BLACK, KNIGHT_TYPE, expectedPositions.get(2), List.of("a6", "c6"));
        assertPieceActions(board, Colors.BLACK, KNIGHT_TYPE, expectedPositions.get(3), List.of("f6", "h6"));
    }

    @Test
    void testDefaultKnightActionsOnEmptyBoard() {
        var board1 = new BoardBuilder().withWhiteKnight("b1").build();
        assertPieceActions(board1, Colors.WHITE, KNIGHT_TYPE, "b1", List.of("a3", "c3", "d2"));

        var board2 = new BoardBuilder().withWhiteKnight("g1").build();
        assertPieceActions(board2, Colors.WHITE, KNIGHT_TYPE, "g1", List.of("f3", "h3", "e2"));

        var board3 = new BoardBuilder().withBlackKnight("b8").build();
        assertPieceActions(board3, Colors.BLACK, KNIGHT_TYPE, "b8", List.of("a6", "c6", "d7"));

        var board4 = new BoardBuilder().withBlackKnight("g8").build();
        assertPieceActions(board4, Colors.BLACK, KNIGHT_TYPE, "g8", List.of("f6", "h6", "e7"));
    }

    @Test
    void testRandomKnightActionsOnEmptyBoard() {
        var board1 = new BoardBuilder().withWhiteKnight("d5").build();
        assertPieceActions(board1, Colors.WHITE, KNIGHT_TYPE, "d5",
                List.of("b6", "c7", "e7", "f6", "e3", "f4", "c3", "b4"));

        var board2 = new BoardBuilder().withBlackKnight("e4").build();
        assertPieceActions(board2, Colors.BLACK, KNIGHT_TYPE, "e4",
                List.of("d2", "c3", "c5", "d6", "f6", "g5", "g3", "f2"));
    }

    @Test
    void testKnightCaptureActionOnEmptyBoard() {
        var board1 = new BoardBuilder()
                .withBlackPawn("a3")
                .withWhiteKnight("b1")
                .build();

        assertPieceActions(board1, Colors.WHITE, KNIGHT_TYPE, "b1", List.of("c3", "d2"), List.of("a3"));

        var board2 = new BoardBuilder()
                .withWhitePawn("h6")
                .withBlackKnight("g8")
                .build();

        assertPieceActions(board2, Colors.BLACK, KNIGHT_TYPE, "g8", List.of("f6", "e7"), List.of("h6"));
    }
}
