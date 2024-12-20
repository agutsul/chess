package com.agutsul.chess.activity.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.CancelCastlingAction;
import com.agutsul.chess.activity.action.CancelCastlingAction.UncastlingMoveAction;
import com.agutsul.chess.board.BoardBuilder;

@ExtendWith(MockitoExtension.class)
public class CancelCastlingActionTest {

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testCancelCastlingAction() {
        var board = new BoardBuilder()
                .withWhiteKing("e1")
                .withWhiteRook("h1")
                .build();

        var king = board.getPiece("e1").get();
        var kingSourcePosition = king.getPosition();

        var rook = board.getPiece("h1").get();
        var rookSourcePosition = rook.getPosition();

        var actions = board.getActions(king);
        assertEquals(6, actions.size());

        var castlingAction = actions.stream()
                .filter(action -> Action.Type.CASTLING.equals(action.getType()))
                .findFirst();

        assertTrue(castlingAction.isPresent());
        assertEquals("O-O", castlingAction.get().getCode());

        castlingAction.get().execute();

        var kingTargetPosition = board.getPosition("g1").get();
        assertEquals(kingTargetPosition, king.getPosition());
        assertTrue(board.isEmpty(kingSourcePosition));

        var rookTargetPosition = board.getPosition("f1").get();
        assertEquals(rookTargetPosition, rook.getPosition());
        assertTrue(board.isEmpty(rookSourcePosition));

        var kingAction = new UncastlingMoveAction(king, kingSourcePosition);
        var rookAction = new UncastlingMoveAction(rook, rookSourcePosition);

        var cancelAction = new CancelCastlingAction("O-O", kingAction, rookAction);
        cancelAction.execute();

        assertEquals(rookSourcePosition, rook.getPosition());
        assertEquals(kingSourcePosition, king.getPosition());
    }
}