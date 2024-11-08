package com.agutsul.chess.action.memento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.CancelCaptureAction;
import com.agutsul.chess.action.CancelCastlingAction;
import com.agutsul.chess.action.CancelEnPassantAction;
import com.agutsul.chess.action.CancelMoveAction;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceCastlingAction;
import com.agutsul.chess.action.PieceEnPassantAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.action.PiecePromoteAction;
import com.agutsul.chess.board.BoardBuilder;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.mock.PieceTypeRequestObserverMock;
import com.agutsul.chess.piece.PawnPiece;

@ExtendWith(MockitoExtension.class)
public class CancelActionMementoFactoryTest {

    @Test
    void testCancelMoveActionCreation() {
        var board = new BoardBuilder()
                .withWhitePawn("e2")
                .build();

        var targetPosition = board.getPosition("e3").get();
        var sourcePosition = board.getPosition("e2").get();

        var pawn = board.getPiece(sourcePosition).get();
        var moveAction = board.getActions(pawn, PieceMoveAction.class).stream()
                .filter(action -> Objects.equals(action.getTarget(), targetPosition))
                .findFirst()
                .get();

        var memento = ActionMementoFactory.createMemento(moveAction);

        moveAction.execute();

        var cancelAction = CancelActionMementoFactory.createAction(board, memento);

        assertEquals(Action.Type.MOVE, cancelAction.getType());
        assertEquals(sourcePosition, cancelAction.getPosition());
        assertEquals(targetPosition, ((PawnPiece<?>) cancelAction.getSource()).getPosition());
    }

    @Test
    void testCancelCaptureActionCreation() {
        var board = new BoardBuilder()
                .withBlackPawn("d3")
                .withWhitePawn("e2")
                .build();

        var targetPosition = board.getPosition("d3").get();
        var sourcePosition = board.getPosition("e2").get();

        var predator = board.getPiece(sourcePosition).get();
        var victim = board.getPiece(targetPosition).get();

        var captureAction = board.getActions(predator, PieceCaptureAction.class).stream()
                .filter(action -> Objects.equals(action.getTarget(), victim))
                .findFirst()
                .get();

        var memento = ActionMementoFactory.createMemento(captureAction);

        captureAction.execute();

        var cancelAction = CancelActionMementoFactory.createAction(board, memento);

        assertEquals(Action.Type.CAPTURE, cancelAction.getType());
        assertEquals(targetPosition, cancelAction.getPosition());
        assertEquals(targetPosition, ((PawnPiece<?>) cancelAction.getSource()).getPosition());
    }

    @Test
    void testCancelPromoteActionBasedOnMove() {
        var board = new BoardBuilder()
                .withWhitePawn("e7")
                .build();

        ((Observable) board).addObserver(new PieceTypeRequestObserverMock());

        var targetPosition = board.getPosition("e8").get();
        var sourcePosition = board.getPosition("e7").get();

        var pawn = board.getPiece(sourcePosition).get();

        var promoteAction = board.getActions(pawn, PiecePromoteAction.class).stream()
                .findFirst()
                .get();

        var memento = ActionMementoFactory.createMemento(promoteAction);

        promoteAction.execute();

        var queen = board.getPiece(targetPosition).get();

        var cancelAction = CancelActionMementoFactory.createAction(board, memento);

        assertEquals(Action.Type.PROMOTE, cancelAction.getType());
        assertEquals(sourcePosition, cancelAction.getPosition());
        assertEquals(Action.Type.MOVE, ((Action<?>) cancelAction.getSource()).getType());

        var originAction = (CancelMoveAction<?,?>) cancelAction.getSource();
        assertEquals(queen, originAction.getSource());
        assertEquals(targetPosition, queen.getPosition());
    }

    @Test
    void testCancelPromoteActionBasedOnCapture() {
        var board = new BoardBuilder()
                .withWhitePawn("e7")
                .withBlackRook("d8")
                .build();

        ((Observable) board).addObserver(new PieceTypeRequestObserverMock());

        var targetPosition = board.getPosition("d8").get();
        var sourcePosition = board.getPosition("e7").get();

        var pawn = board.getPiece(sourcePosition).get();
        var rook = board.getPiece(targetPosition).get();
        assertTrue(rook.isActive());

        var promoteAction = board.getActions(pawn, PiecePromoteAction.class).stream()
                .findFirst()
                .get();

        var memento = ActionMementoFactory.createMemento(promoteAction);

        promoteAction.execute();
        assertFalse(rook.isActive());

        var queen = board.getPiece(targetPosition).get();
        assertTrue(queen.isActive());

        var cancelAction = CancelActionMementoFactory.createAction(board, memento);

        assertEquals(Action.Type.PROMOTE, cancelAction.getType());
        assertEquals(Action.Type.CAPTURE, ((Action<?>) cancelAction.getSource()).getType());

        var originAction = (CancelCaptureAction<?,?,?,?>) cancelAction.getSource();
        assertEquals(queen, originAction.getSource());
        assertEquals(targetPosition, queen.getPosition());
    }

    @Test
    void testCancelCastlingAction() {
        var board = new BoardBuilder()
                .withWhiteKing("e1")
                .withWhiteRook("h1")
                .build();

        var kingPosition = board.getPosition("e1").get();
        var king = board.getPiece(kingPosition).get();

        var castlingAction = board.getActions(king, PieceCastlingAction.class).stream()
                .findFirst()
                .get();

        var memento = ActionMementoFactory.createMemento(castlingAction);

        castlingAction.execute();

        var cancelAction = CancelActionMementoFactory.createAction(board, memento);

        assertEquals(Action.Type.CASTLING, cancelAction.getType());
        assertEquals("O-O", cancelAction.getCode());
        assertEquals(kingPosition, cancelAction.getPosition());

        var action = (CancelCastlingAction<?,?,?>) cancelAction;
        assertEquals("Kg1->e1", String.valueOf(action.getSource()));
        assertEquals("Rf1->h1", String.valueOf(action.getTarget()));
    }

    @Test
    void testCancelEnPassantAction() {
        var board = new BoardBuilder()
                .withBlackPawn("a7")
                .withWhitePawn("b5")
                .build();

        var blackPawn = (PawnPiece<Color>) board.getPiece("a7").get();
        blackPawn.move(board.getPosition("a5").get());

        var whitePawn = (PawnPiece<Color>) board.getPiece("b5").get();

        var enPassantAction = board.getActions(whitePawn, PieceEnPassantAction.class).stream()
                .findFirst()
                .get();

        var memento = ActionMementoFactory.createMemento(enPassantAction);

        enPassantAction.execute();

        var cancelAction = CancelActionMementoFactory.createAction(board, memento);

        assertEquals(Action.Type.EN_PASSANT, cancelAction.getType());

        var action = (CancelEnPassantAction<?,?,?,?>) cancelAction;
        assertEquals(whitePawn, action.getSource());
        assertEquals(blackPawn, action.getTarget());
    }
}