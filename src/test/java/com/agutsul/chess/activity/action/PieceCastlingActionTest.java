package com.agutsul.chess.activity.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.BoardBuilder;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;

@ExtendWith(MockitoExtension.class)
public class PieceCastlingActionTest {

    @Test
    void testWhiteKingSideCastlingAction() {
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

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        var kingTargetPosition = board.getPosition("g1").get();
        assertEquals(kingTargetPosition, king.getPosition());
        assertTrue(board.isEmpty(kingSourcePosition));

        var rookTargetPosition = board.getPosition("f1").get();
        assertEquals(rookTargetPosition, rook.getPosition());
        assertTrue(board.isEmpty(rookSourcePosition));
    }

    @Test
    void testBlackQueenSideCastlingAction() {
        var board = new BoardBuilder()
                .withBlackKing("e8")
                .withBlackRook("a8")
                .build();

        var king = board.getPiece("e8").get();
        var kingSourcePosition = king.getPosition();

        var rook = board.getPiece("a8").get();
        var rookSourcePosition = rook.getPosition();

        var actions = board.getActions(king);
        assertEquals(6, actions.size());

        var castlingAction = actions.stream()
                .filter(action -> Action.Type.CASTLING.equals(action.getType()))
                .findFirst();

        assertTrue(castlingAction.isPresent());
        assertEquals("O-O-O", castlingAction.get().getCode());

        castlingAction.get().execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.BLACK));

        var kingTargetPosition = board.getPosition("c8").get();
        assertEquals(kingTargetPosition, king.getPosition());
        assertTrue(board.isEmpty(kingSourcePosition));

        var rookTargetPosition = board.getPosition("d8").get();
        assertEquals(rookTargetPosition, rook.getPosition());
        assertTrue(board.isEmpty(rookSourcePosition));
    }
}