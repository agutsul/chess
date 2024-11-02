package com.agutsul.chess.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.BoardBuilder;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;

@ExtendWith(MockitoExtension.class)
public class CancelEnPassantActionTest {

    @Test
    void testCancelEnPassantAction() {
        var board = new BoardBuilder()
                .withBlackPawn("a7")
                .withWhitePawn("b5")
                .build();

        var blackPawn = (PawnPiece<Color>) board.getPiece("a7").get();
        blackPawn.move(board.getPosition("a5").get());

        var whitePawn = (PawnPiece<Color>) board.getPiece("b5").get();

        var sourcePosition = whitePawn.getPosition();
        var targetPosition = board.getPosition("a6").get();

        assertTrue(board.isEmpty(targetPosition));

        var actions = board.getActions(whitePawn);
        assertFalse(actions.isEmpty());

        var enPassantAction = actions.stream()
                .filter(action -> Action.Type.EN_PASSANT.equals(action.getType()))
                .findFirst();

        assertTrue(enPassantAction.isPresent());
        assertEquals("b5xa6 e.p.", enPassantAction.get().getCode());

        enPassantAction.get().execute();

        assertEquals(targetPosition, whitePawn.getPosition());
        assertFalse(blackPawn.isActive());

        @SuppressWarnings({ "unchecked", "rawtypes" })
        var cancelAction = new CancelEnPassantAction(whitePawn, blackPawn);
        cancelAction.execute();

        assertEquals(sourcePosition, whitePawn.getPosition());
        assertTrue(blackPawn.isActive());
        assertEquals("a5", String.valueOf(blackPawn.getPosition()));
    }
}