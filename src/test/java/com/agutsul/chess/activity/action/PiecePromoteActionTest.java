package com.agutsul.chess.activity.action;

import static com.agutsul.chess.activity.action.Action.isCapture;
import static com.agutsul.chess.activity.action.Action.isMove;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.mock.PieceTypeRequestObserverMock;

@ExtendWith(MockitoExtension.class)
public class PiecePromoteActionTest {

    @Test
    void testWhitePawnPromotionBasedOnMove() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("a7")
                .build();

        ((Observable) board).addObserver(new PieceTypeRequestObserverMock());

        var pawn = board.getPiece("a7").get();
        var pawnSourcePosition = pawn.getPosition();

        var actions = board.getActions(pawn);

        assertFalse(actions.isEmpty());

        var promotionAction = actions.stream()
                .filter(Action::isPromote)
                .filter(action -> isMove((Action<?>) action.getSource()))
                .findFirst();

        assertTrue(promotionAction.isPresent());
        assertEquals("a7 a8?", promotionAction.get().getCode());

        var targetPosition = board.getPosition("a8").get();
        assertEquals(targetPosition, promotionAction.get().getPosition());

        promotionAction.get().execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        assertEquals(targetPosition, pawn.getPosition());
        assertTrue(board.isEmpty(pawnSourcePosition));
    }

    @Test
    void testBlackPawnPromotionBasedOnCapture() {
        var board = new LabeledBoardBuilder()
                .withBlackPawn("a2")
                .withWhiteRook("b1")
                .build();

        ((Observable) board).addObserver(new PieceTypeRequestObserverMock());

        var pawn = board.getPiece("a2").get();
        var pawnSourcePosition = pawn.getPosition();

        var rook = board.getPiece("b1").get();
        assertTrue(rook.isActive());

        var actions = board.getActions(pawn);

        assertFalse(actions.isEmpty());

        var promotionAction = actions.stream()
                .filter(Action::isPromote)
                .filter(action -> isCapture((Action<?>) action.getSource()))
                .findFirst();

        assertTrue(promotionAction.isPresent());
        assertEquals("a2xb1?", promotionAction.get().getCode());

        var targetPosition = board.getPosition("b1").get();
        assertEquals(targetPosition, promotionAction.get().getPosition());

        promotionAction.get().execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.BLACK));

        assertEquals(targetPosition, pawn.getPosition());
        assertTrue(board.isEmpty(pawnSourcePosition));
        assertFalse(rook.isActive());
    }
}