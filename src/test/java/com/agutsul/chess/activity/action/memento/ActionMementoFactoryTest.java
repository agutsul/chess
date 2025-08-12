package com.agutsul.chess.activity.action.memento;

import static com.agutsul.chess.activity.action.Action.isCapture;
import static com.agutsul.chess.activity.action.Action.isCastling;
import static com.agutsul.chess.activity.action.Action.isEnPassant;
import static com.agutsul.chess.activity.action.Action.isMove;
import static com.agutsul.chess.activity.action.Action.isPromote;
import static com.agutsul.chess.activity.action.memento.ActionMementoFactory.createMemento;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.PawnPiece;

@ExtendWith(MockitoExtension.class)
public class ActionMementoFactoryTest {

    @Test
    void testMoveActionMemento() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e2")
                .build();

        var pawn = (PawnPiece<Color>) board.getPiece("e2").get();
        var actions = board.getActions(pawn, Action.Type.MOVE);

        var memento = createMemento(board, actions.iterator().next());

        assertTrue(isMove(memento.getActionType()));
        assertEquals("MOVE PAWN(e2 e3)", String.valueOf(memento));
    }

    @Test
    void testCaptureActionMemento() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e2")
                .withBlackPawn("d3")
                .build();

        var pawn = (PawnPiece<Color>) board.getPiece("e2").get();
        var actions = board.getActions(pawn, Action.Type.CAPTURE);

        var memento = createMemento(board, actions.iterator().next());

        assertTrue(isCapture(memento.getActionType()));
        assertEquals("CAPTURE PAWN(e2 d3)", String.valueOf(memento));
    }

    @Test
    void testCastlingActionMemento() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("e1")
                .withWhiteRook("h1")
                .build();

        var king = (KingPiece<Color>) board.getPiece("e1").get();
        var actions = board.getActions(king, Action.Type.CASTLING);

        var memento = createMemento(board, actions.iterator().next());

        assertTrue(isCastling(memento.getActionType()));
        assertEquals("CASTLING(MOVE KING(e1 g1) MOVE ROOK(h1 f1))", String.valueOf(memento));
    }

    @Test
    void testPromoteActionMemento() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e7")
                .build();

        var pawn = (PawnPiece<Color>) board.getPiece("e7").get();
        var actions = board.getActions(pawn, Action.Type.PROMOTE);

        var action = spy(actions.iterator().next());
        var memento = createMemento(board, action);

        assertTrue(isPromote(memento.getActionType()));
        assertEquals("PROMOTE(MOVE PAWN(e7 e8) ?)", String.valueOf(memento));
    }

    @Test
    void testEnPassantActionMemento() {
        var board = new LabeledBoardBuilder()
                .withBlackPawn("a7")
                .withWhitePawn("b5")
                .build();

        var blackPawn = (PawnPiece<Color>) board.getPiece("a7").get();
        blackPawn.move(board.getPosition("a5").get());

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        var whitePawn = (PawnPiece<Color>) board.getPiece("b5").get();
        var actions = board.getActions(whitePawn, Action.Type.EN_PASSANT);

        var memento = createMemento(board, actions.iterator().next());

        assertTrue(isEnPassant(memento.getActionType()));
        assertEquals("EN_PASSANT(CAPTURE PAWN(b5 a5) a6)", String.valueOf(memento));
    }
}