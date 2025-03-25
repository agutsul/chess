package com.agutsul.chess.activity.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.StringBoardBuilder;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.piece.PawnPiece;

@ExtendWith(MockitoExtension.class)
public class CancelEnPassantActionTest {

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

        var sourcePosition = whitePawn.getPosition();
        var targetPosition = board.getPosition("a6").get();

        assertTrue(board.isEmpty(targetPosition));

        var actions = board.getActions(whitePawn);
        assertFalse(actions.isEmpty());

        var enPassantAction = actions.stream()
                .filter(Action::isEnPassant)
                .findFirst();

        assertTrue(enPassantAction.isPresent());
        assertEquals("b5xa6 e.p.", enPassantAction.get().getCode());

        enPassantAction.get().execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        assertEquals(targetPosition, whitePawn.getPosition());
        assertFalse(blackPawn.isActive());

        @SuppressWarnings({ "unchecked", "rawtypes" })
        var cancelAction = new CancelEnPassantAction(whitePawn, blackPawn);
        cancelAction.execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        assertEquals(sourcePosition, whitePawn.getPosition());
        assertTrue(blackPawn.isActive());
        assertEquals("a5", String.valueOf(blackPawn.getPosition()));
    }
}