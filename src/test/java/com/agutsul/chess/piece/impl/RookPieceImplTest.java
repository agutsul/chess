package com.agutsul.chess.piece.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.Piece.Type;

@ExtendWith(MockitoExtension.class)
public class RookPieceImplTest extends AbstractPieceTest {

    private static final Type ROOK_TYPE = Piece.Type.ROOK;

    @Test
    void testDefaultRookActionsOnStandardBoard() {
        var expectedPositions = List.of("a1", "h1", "a8", "h8");

        var board = new StandardBoard();
        var pieces = board.getPieces(ROOK_TYPE);
        assertEquals(pieces.size(), expectedPositions.size());

        var positions = pieces.stream()
                .map(Piece::getPosition)
                .map(String::valueOf)
                .toList();

        assertTrue(positions.containsAll(expectedPositions));

        assertPieceActions(board, Colors.WHITE, ROOK_TYPE, expectedPositions.get(0));
        assertPieceActions(board, Colors.WHITE, ROOK_TYPE, expectedPositions.get(1));

        assertPieceActions(board, Colors.BLACK, ROOK_TYPE, expectedPositions.get(2));
        assertPieceActions(board, Colors.BLACK, ROOK_TYPE, expectedPositions.get(3));
    }

    @Test
    void testDefaultRookActionsOnEmptyBoard() {
        var board1 = new LabeledBoardBuilder().withWhiteRook("a1").build();
        assertPieceActions(board1, Colors.WHITE, ROOK_TYPE, "a1", List.of(
                "a2", "a3", "a4", "a5", "a6", "a7", "a8",
                "b1", "c1", "d1", "e1", "f1", "g1", "h1"
            ));

        var board2 = new LabeledBoardBuilder().withWhiteRook("h1").build();
        assertPieceActions(board2, Colors.WHITE, ROOK_TYPE, "h1", List.of(
                "h2", "h3", "h4", "h5", "h6", "h7", "h8",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1"
            ));

        var board3 = new LabeledBoardBuilder().withBlackRook("a8").build();
        assertPieceActions(board3, Colors.BLACK, ROOK_TYPE, "a8", List.of(
                "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a1", "a2", "a3", "a4", "a5", "a6", "a7"
            ));

        var board4 = new LabeledBoardBuilder().withBlackRook("h8").build();
        assertPieceActions(board4, Colors.BLACK, ROOK_TYPE, "h8", List.of(
                "a8", "b8", "c8", "d8", "e8", "f8", "g8",
                "h1", "h2", "h3", "h4", "h5", "h6", "h7"
            ));
    }

    @Test
    void testRandomRookActionsOnEmptyBoard() {
        var board1 = new LabeledBoardBuilder().withWhiteRook("d4").build();
        assertPieceActions(board1, Colors.WHITE, ROOK_TYPE, "d4", List.of(
                "d5", "d6", "d7", "d8", "d3", "d2", "d1",
                "a4", "b4", "c4", "e4", "f4", "g4", "h4"
            ));

        var board2 = new LabeledBoardBuilder().withBlackRook("d5").build();
        assertPieceActions(board2, Colors.BLACK, ROOK_TYPE, "d5", List.of(
                "d6", "d7", "d8", "d4", "d3", "d2", "d1",
                "a5", "b5", "c5", "e5", "f5", "g5", "h5"
            ));
    }

    @Test
    void testRookCaptureActionOnEmptyBoard() {
        var board1 = new LabeledBoardBuilder()
                .withBlackPawn("a3")
                .withWhiteRook("a1")
                .build();

        assertPieceActions(board1, Colors.WHITE, ROOK_TYPE, "a1",
                List.of("b1", "c1", "d1", "e1", "f1", "g1", "h1", "a2"), List.of("a3"));

        var board2 = new LabeledBoardBuilder()
                .withWhitePawn("h6")
                .withBlackRook("h8")
                .build();

        assertPieceActions(board2, Colors.BLACK, ROOK_TYPE, "h8",
                List.of("g8", "e8", "f8", "d8", "b8", "c8", "a8", "h7"), List.of("h6"));
    }

    @Test
    void testRookCastlingActionsOnEmptyBoard() {
        var board1 = new LabeledBoardBuilder()
                .withWhiteRook("a1")
                .withWhiteKing("e1")
                .build();

        assertPieceActions(board1, Colors.WHITE, ROOK_TYPE, "a1",
                List.of(
                        "a2", "a3", "a4", "a5", "a6", "a7", "a8",
                        "b1", "c1", "d1"
                    ),
                List.of(),
                List.of("O-O-O")
        );

        var board2 = new LabeledBoardBuilder()
                .withWhiteRook("h1")
                .withWhiteKing("e1")
                .build();

        assertPieceActions(board2, Colors.WHITE, ROOK_TYPE, "h1",
                List.of(
                        "h2", "h3", "h4", "h5", "h6", "h7", "h8",
                        "f1", "g1"
                    ),
                List.of(),
                List.of("O-O")
        );

        var board3 = new LabeledBoardBuilder()
                .withBlackRook("a8")
                .withBlackKing("e8")
                .build();

        assertPieceActions(board3, Colors.BLACK, ROOK_TYPE, "a8",
                List.of(
                        "b8", "c8", "d8",
                        "a1", "a2", "a3", "a4", "a5", "a6", "a7"
                    ),
                List.of(),
                List.of("O-O-O")
        );

        var board4 = new LabeledBoardBuilder()
                .withBlackRook("h8")
                .withBlackKing("e8")
                .build();

        assertPieceActions(board4, Colors.BLACK, ROOK_TYPE, "h8",
                List.of(
                        "f8", "g8",
                        "h1", "h2", "h3", "h4", "h5", "h6", "h7"
                    ),
                List.of(),
                List.of("O-O")
        );
    }

    @Test
    void testRookCastlingAction() {
        var board = new LabeledBoardBuilder()
                .withWhiteRook("h1")
                .withWhiteKing("e1")
                .build();

        var rook = board.getPiece("h1").get();
        var position = board.getPosition("f1").get();

        ((Castlingable) rook).castling(position);

        assertEquals(rook.getPosition(), position);
    }

    @Test
    void testRookCastlingActionValidation() {
        var board = new LabeledBoardBuilder()
                .withWhiteRook("h1")
                .withWhiteKing("e1")
                .build();

        var rook = board.getPiece("h1").get();
        var position = board.getPosition("f2").get();

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> ((Castlingable) rook).castling(position)
        );

        assertEquals(thrown.getMessage(), "Rh1 invalid castling to f2");
    }

    @Test
    void testRookActionAfterDisposing() {
        var board1 = new LabeledBoardBuilder()
                .withWhiteRook("a1")
                .withWhitePawn("a3")
                .build();

        var whiteRook = board1.getPiece("a1").get();
        assertFalse(board1.getActions(whiteRook).isEmpty());
        assertFalse(board1.getImpacts(whiteRook).isEmpty());

        ((RookPiece<Color>) whiteRook).dispose(null);

        assertTrue(board1.getActions(whiteRook).isEmpty());
        assertTrue(board1.getImpacts(whiteRook).isEmpty());

        var board2 = new LabeledBoardBuilder()
                .withBlackRook("a8")
                .withBlackPawn("a7")
                .build();

        var blackRook = board2.getPiece("a8").get();
        assertFalse(board2.getActions(blackRook).isEmpty());
        assertFalse(board2.getImpacts(blackRook).isEmpty());

        ((RookPiece<Color>) blackRook).dispose(null);

        assertTrue(board2.getActions(blackRook).isEmpty());
        assertTrue(board2.getImpacts(blackRook).isEmpty());
    }
}