package com.agutsul.chess.activity.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.BoardBuilder;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;

@ExtendWith(MockitoExtension.class)
public class CancelCaptureActionTest {

    @Test
    void testWhiteBishopCancelCaptureAction() {
        var board = new BoardBuilder()
                .withWhiteBishop("f1")
                .withBlackPawn("h3")
                .build();

        var whiteBishop = board.getPiece("f1").get();
        var blackPawn = board.getPiece("h3").get();

        var sourcePosition = whiteBishop.getPosition();
        var targetPosition = blackPawn.getPosition();

        var captureAction = board.getActions(whiteBishop).stream()
                .filter(action -> Action.Type.CAPTURE.equals(action.getType()))
                .findFirst();

        assertTrue(captureAction.isPresent());

        captureAction.get().execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        assertFalse(blackPawn.isActive());
        assertEquals(targetPosition, whiteBishop.getPosition());

        @SuppressWarnings({ "rawtypes", "unchecked" })
        var cancelAction = new CancelCaptureAction(whiteBishop, blackPawn);
        cancelAction.execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        assertEquals(sourcePosition, whiteBishop.getPosition());
        assertEquals(targetPosition, blackPawn.getPosition());
        assertTrue(blackPawn.isActive());
    }
}