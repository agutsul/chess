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
public class PieceEnPassantActionTest {

    @Test
    void testWhitePawnEnPassantAction() {
        var board = new StringBoardBuilder()
                .withBlackPawn("a7")
                .withWhitePawn("b5")
                .build();

        var blackPawn = (PawnPiece<Color>) board.getPiece("a7").get();
        blackPawn.move(board.getPosition("a5").get());

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.BLACK));

        var whitePawn = (PawnPiece<Color>) board.getPiece("b5").get();
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
    }

    @Test
    void testBlackPawnEnPassantAction() {
        var board = new StringBoardBuilder()
                .withBlackPawn("b4")
                .withWhitePawn("a2")
                .build();

        var whitePawn = (PawnPiece<Color>) board.getPiece("a2").get();
        whitePawn.move(board.getPosition("a4").get());

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        var blackPawn = (PawnPiece<Color>) board.getPiece("b4").get();
        var targetPosition = board.getPosition("a3").get();
        assertTrue(board.isEmpty(targetPosition));

        var actions = board.getActions(blackPawn);
        assertFalse(actions.isEmpty());

        var enPassantAction = actions.stream()
                .filter(Action::isEnPassant)
                .findFirst();

        assertTrue(enPassantAction.isPresent());
        assertEquals("b4xa3 e.p.", enPassantAction.get().getCode());

        enPassantAction.get().execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        assertEquals(targetPosition, blackPawn.getPosition());
        assertFalse(whitePawn.isActive());
    }
}