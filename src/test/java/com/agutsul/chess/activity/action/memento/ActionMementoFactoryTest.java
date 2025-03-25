package com.agutsul.chess.activity.action.memento;

import static com.agutsul.chess.activity.action.memento.ActionMementoFactory.createMemento;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.StringBoardBuilder;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.piece.PawnPiece;

@ExtendWith(MockitoExtension.class)
public class ActionMementoFactoryTest {

    @Test
    void testMoveActionMemento() {
        var board = new StringBoardBuilder()
                .withWhitePawn("e2")
                .build();

        var pawn = board.getPiece("e2");
        var actions = board.getActions(pawn.get(), Action.Type.MOVE);

        var memento = createMemento(board, actions.iterator().next());

        assertEquals(Action.Type.MOVE, memento.getActionType());
        assertEquals("MOVE PAWN(e2 e3)", String.valueOf(memento));
    }

    @Test
    void testCaptureActionMemento() {
        var board = new StringBoardBuilder()
                .withWhitePawn("e2")
                .withBlackPawn("d3")
                .build();

        var pawn = board.getPiece("e2");
        var actions = board.getActions(pawn.get(), Action.Type.CAPTURE);

        var memento = createMemento(board, actions.iterator().next());

        assertEquals(Action.Type.CAPTURE, memento.getActionType());
        assertEquals("CAPTURE PAWN(e2 d3)", String.valueOf(memento));
    }

    @Test
    void testCastlingActionMemento() {
        var board = new StringBoardBuilder()
                .withWhiteKing("e1")
                .withWhiteRook("h1")
                .build();

        var king = board.getPiece("e1");
        var actions = board.getActions(king.get(), Action.Type.CASTLING);

        var memento = createMemento(board, actions.iterator().next());

        assertEquals(Action.Type.CASTLING, memento.getActionType());
        assertEquals("CASTLING(MOVE KING(e1 g1) MOVE ROOK(h1 f1))", String.valueOf(memento));
    }

    @Test
    void testPromoteActionMemento() {
        var board = new StringBoardBuilder()
                .withWhitePawn("e7")
                .build();

        var pawn = board.getPiece("e7");
        var actions = board.getActions(pawn.get(), Action.Type.PROMOTE);

        var action = spy(actions.iterator().next());
        var memento = createMemento(board, action);

        assertEquals(Action.Type.PROMOTE, memento.getActionType());
        assertEquals("PROMOTE(MOVE PAWN(e7 e8) ?)", String.valueOf(memento));
    }

    @Test
    void testEnPassantActionMemento() {
        var board = new StringBoardBuilder()
                .withBlackPawn("a7")
                .withWhitePawn("b5")
                .build();

        var blackPawn = (PawnPiece<Color>) board.getPiece("a7").get();
        blackPawn.move(board.getPosition("a5").get());

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        var whitePawn = (PawnPiece<Color>) board.getPiece("b5").get();
        var actions = board.getActions(whitePawn, Action.Type.EN_PASSANT);

        var memento = createMemento(board, actions.iterator().next());

        assertEquals(Action.Type.EN_PASSANT, memento.getActionType());
        assertEquals("EN_PASSANT(CAPTURE PAWN(b5 a5) a6)", String.valueOf(memento));
    }
}