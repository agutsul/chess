package com.agutsul.chess.activity.action;

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

@ExtendWith(MockitoExtension.class)
public class PieceCaptureActionTest {

    @Test
    void testWhitePawnCaptureAction() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("a3")
                .withBlackPawn("b4")
                .build();

        var blackPawn = board.getPiece("b4").get();
        var whitePawn = board.getPiece("a3").get();

        var actions = board.getActions(whitePawn);
        assertEquals(2, actions.size());

        var captureAction = actions.stream()
                .filter(Action::isCapture)
                .findFirst();

        assertTrue(captureAction.isPresent());
        assertEquals("a3xb4", captureAction.get().getCode());

        captureAction.get().execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        var position = board.getPosition("a3").get();
        assertTrue(board.isEmpty(position));

        assertFalse(blackPawn.isActive());
        assertTrue(whitePawn.isActive());

        assertEquals(whitePawn.getPosition(), blackPawn.getPosition());
    }

    @Test
    void testBlackPawnCaptureAction() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("a3")
                .withBlackPawn("b4")
                .build();

        var blackPawn = board.getPiece("b4").get();
        var whitePawn = board.getPiece("a3").get();

        var actions = board.getActions(blackPawn);
        assertEquals(2, actions.size());

        var captureAction = actions.stream()
                .filter(Action::isCapture)
                .findFirst();

        assertTrue(captureAction.isPresent());
        assertEquals("b4xa3", captureAction.get().getCode());

        captureAction.get().execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.BLACK));

        var position = board.getPosition("b4").get();
        assertTrue(board.isEmpty(position));

        assertFalse(whitePawn.isActive());
        assertTrue(blackPawn.isActive());

        assertEquals(whitePawn.getPosition(), blackPawn.getPosition());
    }
}