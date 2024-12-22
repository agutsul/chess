package com.agutsul.chess.activity.action;

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
public class PieceEnPassantActionTest {

    @Test
    void testWhitePawnEnPassantAction() {
        var board = new BoardBuilder()
                .withBlackPawn("a7")
                .withWhitePawn("b5")
                .build();

        var blackPawn = (PawnPiece<Color>) board.getPiece("a7").get();
        blackPawn.move(board.getPosition("a5").get());

        var whitePawn = (PawnPiece<Color>) board.getPiece("b5").get();
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
    }

    @Test
    void testBlackPawnEnPassantAction() {
        var board = new BoardBuilder()
                .withBlackPawn("b4")
                .withWhitePawn("a2")
                .build();

        var whitePawn = (PawnPiece<Color>) board.getPiece("a2").get();
        whitePawn.move(board.getPosition("a4").get());

        var blackPawn = (PawnPiece<Color>) board.getPiece("b4").get();
        var targetPosition = board.getPosition("a3").get();
        assertTrue(board.isEmpty(targetPosition));

        var actions = board.getActions(blackPawn);
        assertFalse(actions.isEmpty());

        var enPassantAction = actions.stream()
                .filter(action -> Action.Type.EN_PASSANT.equals(action.getType()))
                .findFirst();

        assertTrue(enPassantAction.isPresent());
        assertEquals("b4xa3 e.p.", enPassantAction.get().getCode());

        enPassantAction.get().execute();

        assertEquals(targetPosition, blackPawn.getPosition());
        assertFalse(whitePawn.isActive());
    }
}