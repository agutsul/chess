package com.agutsul.chess.piece;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;

abstract class AbstractPieceTest {

    static void assertPieceActions(
            Board board,
            Color color,
            Piece.Type type,
            String sourcePosition) {

        assertPieceActions(board, color, type, sourcePosition,
                List.of(), List.of(), List.of());
    }

    static void assertPieceActions(
            Board board,
            Color color,
            Piece.Type type,
            String sourcePosition,
            List<String> expectedMovePositions) {

        assertPieceActions(board, color, type, sourcePosition,
                expectedMovePositions, List.of(), List.of());
    }

    static void assertPieceActions(
            Board board,
            Color color,
            Piece.Type type,
            String sourcePosition,
            List<String> expectedMovePositions,
            List<String> expectedCapturePositions) {

        assertPieceActions(board, color, type, sourcePosition,
                expectedMovePositions, expectedCapturePositions, List.of());
    }

    static void assertPieceActions(
            Board board,
            Color color,
            Piece.Type type,
            String sourcePosition,
            List<String> expectedMovePositions,
            List<String> expectedCapturePositions,
            List<String> expectedCastlingPositions) {

        var optionalPiece = board.getPiece(sourcePosition);
        assertTrue(optionalPiece.isPresent());

        var piece = optionalPiece.get();
        assertEquals(String.valueOf(piece.getPosition()), sourcePosition);
        assertEquals(piece.getColor(), color);
        assertEquals(piece.getType(), type);

        var actions = board.getActions(piece);

        assertEquals(actions.size(),
                expectedMovePositions.size()
                + expectedCapturePositions.size()
                + expectedCastlingPositions.size());

        if (!expectedMovePositions.isEmpty()) {
            var movePositions = actions.stream()
                    .filter(action -> Action.Type.MOVE.equals(action.getType()))
                    .map(action -> (PieceMoveAction<?,?>) action)
                    .map(PieceMoveAction::getTarget)
                    .map(String::valueOf)
                    .toList();

            assertTrue(movePositions.containsAll(expectedMovePositions));
        }

        if (!expectedCapturePositions.isEmpty()) {
            var capturePositions = actions.stream()
                    .filter(action -> Action.Type.CAPTURE.equals(action.getType()))
                    .map(action -> (PieceCaptureAction<?,?,?,?>) action)
                    .map(PieceCaptureAction::getTarget)
                    .map(Piece::getPosition)
                    .map(String::valueOf)
                    .toList();

            assertTrue(capturePositions.containsAll(expectedCapturePositions));
        }

        if (!expectedCastlingPositions.isEmpty()) {
            var castlingPositions = actions.stream()
                    .filter(action -> Action.Type.CASTLING.equals(action.getType()))
                    .map(String::valueOf)
                    .toList();

            assertTrue(castlingPositions.containsAll(expectedCastlingPositions));
        }
    }
}
