package com.agutsul.chess.action.memento;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceCastlingAction;
import com.agutsul.chess.action.PieceEnPassantAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.action.PiecePromoteAction;
import com.agutsul.chess.board.BoardBuilder;
import com.agutsul.chess.piece.PawnPiece;

@ExtendWith(MockitoExtension.class)
public class ActionMementoFactoryTest {

    @Test
    void testMoveActionMemento() {
        var board = new BoardBuilder()
                .withWhitePawn("e2")
                .build();

        var pawn = board.getPiece("e2");
        var actions = board.getActions(pawn.get(), PieceMoveAction.class);

        var memento = ActionMementoFactory.INSTANCE.create(actions.iterator().next());

        assertEquals(Action.Type.MOVE, memento.getActionType());
        assertEquals("MOVE(e2 e3)", String.valueOf(memento));
    }

    @Test
    void testCaptureActionMemento() {
        var board = new BoardBuilder()
                .withWhitePawn("e2")
                .withBlackPawn("d3")
                .build();

        var pawn = board.getPiece("e2");
        var actions = board.getActions(pawn.get(), PieceCaptureAction.class);

        var memento = ActionMementoFactory.INSTANCE.create(actions.iterator().next());

        assertEquals(Action.Type.CAPTURE, memento.getActionType());
        assertEquals("CAPTURE(e2 d3)", String.valueOf(memento));
    }

    @Test
    void testCastlingActionMemento() {
        var board = new BoardBuilder()
                .withWhiteKing("e1")
                .withWhiteRook("h1")
                .build();

        var king = board.getPiece("e1");
        var actions = board.getActions(king.get(), PieceCastlingAction.class);

        var memento = ActionMementoFactory.INSTANCE.create(actions.iterator().next());

        assertEquals(Action.Type.CASTLING, memento.getActionType());
        assertEquals("CASTLING(MOVE(e1 g1) MOVE(h1 f1))", String.valueOf(memento));
    }

    @Test
    void testPromoteActionMemento() {
        var board = new BoardBuilder()
                .withWhitePawn("e7")
                .build();

        var pawn = board.getPiece("e7");
        var actions = board.getActions(pawn.get(), PiecePromoteAction.class);

        var memento = ActionMementoFactory.INSTANCE.create(actions.iterator().next());

        assertEquals(Action.Type.PROMOTE, memento.getActionType());
        assertEquals("PROMOTE(e7 MOVE(e7 e8))", String.valueOf(memento));
    }

    @Test
    void testEnPassantActionMemento() {
        var board = new BoardBuilder()
                .withBlackPawn("a7")
                .withWhitePawn("b5")
                .build();

        var blackPawn = (PawnPiece<Color>) board.getPiece("a7").get();
        blackPawn.move(board.getPosition("a5").get());

        var whitePawn = (PawnPiece<Color>) board.getPiece("b5").get();
        var actions = board.getActions(whitePawn, PieceEnPassantAction.class);

        var memento = ActionMementoFactory.INSTANCE.create(actions.iterator().next());

        assertEquals(Action.Type.EN_PASSANT, memento.getActionType());
        assertEquals("EN_PASSANT(b5 EN_PASSANT(a5 a6))", String.valueOf(memento));
    }
}