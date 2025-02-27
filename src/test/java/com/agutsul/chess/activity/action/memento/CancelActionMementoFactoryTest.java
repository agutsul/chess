package com.agutsul.chess.activity.action.memento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.activity.action.AbstractCaptureAction;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.CancelCaptureAction;
import com.agutsul.chess.activity.action.CancelCastlingAction;
import com.agutsul.chess.activity.action.CancelEnPassantAction;
import com.agutsul.chess.activity.action.CancelMoveAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.board.StringBoardBuilder;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.mock.PieceTypeRequestObserverMock;
import com.agutsul.chess.piece.PawnPiece;

@ExtendWith(MockitoExtension.class)
public class CancelActionMementoFactoryTest {

    @Test
    void testCancelMoveActionCreation() {
        var board = new StringBoardBuilder()
                .withWhitePawn("e2")
                .build();

        var targetPosition = board.getPosition("e3").get();
        var sourcePosition = board.getPosition("e2").get();

        var pawn = board.getPiece(sourcePosition).get();
        var moveAction = board.getActions(pawn, Action.Type.MOVE).stream()
                .map(action -> (PieceMoveAction<?,?>) action)
                .filter(action -> Objects.equals(action.getTarget(), targetPosition))
                .findFirst()
                .get();

        var memento = ActionMementoFactory.createMemento(board, moveAction);

        moveAction.execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        var cancelAction = CancelActionMementoFactory.createAction(board, memento);

        assertEquals(Action.Type.MOVE, cancelAction.getType());
        assertEquals(sourcePosition, cancelAction.getPosition());
        assertEquals(targetPosition, ((PawnPiece<?>) cancelAction.getSource()).getPosition());
    }

    @Test
    void testCancelCaptureActionCreation() {
        var board = new StringBoardBuilder()
                .withBlackPawn("d3")
                .withWhitePawn("e2")
                .build();

        var targetPosition = board.getPosition("d3").get();
        var sourcePosition = board.getPosition("e2").get();

        var predator = board.getPiece(sourcePosition).get();
        var victim = board.getPiece(targetPosition).get();

        var captureAction = board.getActions(predator, Action.Type.CAPTURE).stream()
                .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                .filter(action -> Objects.equals(action.getTarget(), victim))
                .findFirst()
                .get();

        var memento = ActionMementoFactory.createMemento(board, captureAction);

        captureAction.execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.BLACK));

        var cancelAction = CancelActionMementoFactory.createAction(board, memento);

        assertEquals(Action.Type.CAPTURE, cancelAction.getType());
        assertEquals(targetPosition, cancelAction.getPosition());
        assertEquals(targetPosition, ((PawnPiece<?>) cancelAction.getSource()).getPosition());
    }

    @Test
    void testCancelPromoteActionBasedOnMove() {
        var board = new StringBoardBuilder()
                .withWhitePawn("e7")
                .build();

        ((Observable) board).addObserver(new PieceTypeRequestObserverMock());

        var targetPosition = board.getPosition("e8").get();
        var sourcePosition = board.getPosition("e7").get();

        var pawn = board.getPiece(sourcePosition).get();

        var promoteAction = board.getActions(pawn, Action.Type.PROMOTE).stream()
                .findFirst()
                .get();

        var memento = ActionMementoFactory.createMemento(board, promoteAction);

        promoteAction.execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

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
        var board = new StringBoardBuilder()
                .withWhitePawn("e7")
                .withBlackRook("d8")
                .build();

        ((Observable) board).addObserver(new PieceTypeRequestObserverMock());

        var targetPosition = board.getPosition("d8").get();
        var sourcePosition = board.getPosition("e7").get();

        var pawn = board.getPiece(sourcePosition).get();
        var rook = board.getPiece(targetPosition).get();
        assertTrue(rook.isActive());

        var promoteAction = board.getActions(pawn, Action.Type.PROMOTE).stream()
                .findFirst()
                .get();

        var memento = ActionMementoFactory.createMemento(board, promoteAction);

        promoteAction.execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.BLACK));

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
        var board = new StringBoardBuilder()
                .withWhiteKing("e1")
                .withWhiteRook("h1")
                .build();

        var kingPosition = board.getPosition("e1").get();
        var king = board.getPiece(kingPosition).get();

        var castlingAction = board.getActions(king, Action.Type.CASTLING).stream()
                .findFirst()
                .get();

        var memento = ActionMementoFactory.createMemento(board, castlingAction);

        castlingAction.execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        var cancelAction = CancelActionMementoFactory.createAction(board, memento);

        assertEquals(Action.Type.CASTLING, cancelAction.getType());
        assertEquals(Castlingable.Side.KING.name(), cancelAction.getCode());
        assertEquals(kingPosition, cancelAction.getPosition());

        var action = (CancelCastlingAction<?,?,?>) cancelAction;
        assertEquals("Kg1->e1", String.valueOf(action.getSource()));
        assertEquals("Rf1->h1", String.valueOf(action.getTarget()));
    }

    @Test
    void testCancelEnPassantAction() {
        var board = new StringBoardBuilder()
                .withBlackPawn("a7")
                .withWhitePawn("b5")
                .build();

        var blackPawn = (PawnPiece<Color>) board.getPiece("a7").get();
        blackPawn.move(board.getPosition("a5").get());

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.BLACK));

        var whitePawn = (PawnPiece<Color>) board.getPiece("b5").get();

        var enPassantAction = board.getActions(whitePawn, Action.Type.EN_PASSANT).stream()
                .findFirst()
                .get();

        var memento = ActionMementoFactory.createMemento(board, enPassantAction);

        enPassantAction.execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        var cancelAction = CancelActionMementoFactory.createAction(board, memento);

        assertEquals(Action.Type.EN_PASSANT, cancelAction.getType());

        var action = (CancelEnPassantAction<?,?,?,?>) cancelAction;
        assertEquals(whitePawn, action.getSource());
        assertEquals(blackPawn, action.getTarget());
    }
}