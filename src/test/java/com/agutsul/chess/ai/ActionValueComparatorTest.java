package com.agutsul.chess.ai;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.Action.Type;
import com.agutsul.chess.activity.action.PieceBigMoveAction;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceCastlingAction;
import com.agutsul.chess.activity.action.PieceCastlingAction.CastlingMoveAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.piece.PawnPiece;

@ExtendWith(MockitoExtension.class)
public class ActionValueComparatorTest {

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testActionValuePieceMoveActionEqualValueSorting() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e2")
                .withWhiteKing("e1")
                .build();

        var pawn = (PawnPiece<?>) board.getPiece("e2").get();

        var action1 = new PieceMoveAction(pawn, positionOf("e3"));
        var action2 = new PieceBigMoveAction(pawn, positionOf("e4"));

        var values = new ArrayList<ActionSimulationResult>();

        values.add(mockSimulationResult(action1, 1));
        values.add(mockSimulationResult(action2, 1));

        values.sort(new ActionValueComparator());

        assertEquals(2, values.size());
        assertEquals(action2, values.getFirst().getAction());
        assertEquals(action1, values.getLast().getAction());
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testActionValuePieceCaptureActionTargetTypeValueSorting() {
        var board = new LabeledBoardBuilder()
                .withWhitePawns("e2", "a4")
                .withBlackPawn("f3")
                .withBlackKnight("b5")
                .withWhiteKing("e1")
                .withBlackKing("e8")
                .build();

        var pawn1 = board.getPiece("e2").get();
        var pawn2 = board.getPiece("a4").get();

        var knight = board.getPiece("b5").get();
        var pawn = board.getPiece("f3").get();

        var action1 = new PieceCaptureAction(pawn1, pawn);
        var action2 = new PieceCaptureAction(pawn2, knight);

        var values = new ArrayList<ActionSimulationResult>();

        values.add(mockSimulationResult(action1, 1));
        values.add(mockSimulationResult(action2, 1));

        values.sort(new ActionValueComparator());

        assertEquals(2, values.size());
        assertEquals(action2, values.getFirst().getAction());
        assertEquals(action1, values.getLast().getAction());
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testActionValuePiecePromoteActionSorting() {
        var board = new LabeledBoardBuilder()
                .withWhitePawns("e7", "a7")
                .withBlackKnight("b8")
                .withWhiteKing("e1")
                .build();

        var pawn1 = board.getPiece("e7").get();
        var pawn2 = board.getPiece("a7").get();

        var knight = board.getPiece("b8").get();

        var moveAction = new PieceMoveAction(pawn1, positionOf("e8"));
        var captureAction = new PieceCaptureAction(pawn2, knight);

        var observable = mock(Observable.class);

        var promoteAction1 = new PiecePromoteAction(moveAction, observable);
        var promoteAction2 = new PiecePromoteAction(captureAction, observable);

        var values = new ArrayList<ActionSimulationResult>();

        values.add(mockSimulationResult(promoteAction1, 1));
        values.add(mockSimulationResult(promoteAction2, 1));

        values.sort(new ActionValueComparator());

        assertEquals(2, values.size());
        assertEquals(promoteAction2, values.getFirst().getAction());
        assertEquals(promoteAction1, values.getLast().getAction());
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testActionValuePieceCastlingActionSorting() {
        var board = new LabeledBoardBuilder()
                .withWhiteRooks("a1", "h1")
                .withWhiteKing("e1")
                .build();

        var rook1 = board.getPiece("a1").get();
        var rook2 = board.getPiece("h1").get();

        var king = board.getPiece("e1").get();

        var castlingAction1 = new PieceCastlingAction(
                Castlingable.Side.KING,
                new CastlingMoveAction(rook2, positionOf("f1")),
                new CastlingMoveAction(king,  positionOf("g1"))
        );

        var castlingAction2 = new PieceCastlingAction(
                Castlingable.Side.QUEEN,
                new CastlingMoveAction(rook1, positionOf("d1")),
                new CastlingMoveAction(king,  positionOf("c1"))
        );

        var values = new ArrayList<ActionSimulationResult>();

        values.add(mockSimulationResult(castlingAction1, 1));
        values.add(mockSimulationResult(castlingAction2, 1));

        values.sort(new ActionValueComparator());

        assertEquals(2, values.size());
        assertEquals(castlingAction2, values.getFirst().getAction());
        assertEquals(castlingAction1, values.getLast().getAction());
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testActionValuePieceEnPassantActionSorting() {
        var board = new LabeledBoardBuilder()
                .withWhitePawns("a2", "h2")
                .withBlackPawns("b4", "g4")
                .build();

        var whitePawn1 = board.getPiece("a2").get();
        var whitePawn2 = board.getPiece("h2").get();

        var moveAction1 = new PieceBigMoveAction((PawnPiece<?>) whitePawn1, positionOf("a4"));
        moveAction1.execute();

        var moveAction2 = new PieceBigMoveAction((PawnPiece<?>) whitePawn2, positionOf("h4"));
        moveAction2.execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));
        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.BLACK));

        var blackPawn1 = board.getPiece("b4").get();
        var blackPawn2 = board.getPiece("g4").get();

        var enpassantAction1 = new ArrayList<>(board.getActions(blackPawn1, Type.EN_PASSANT));
        var enpassantAction2 = new ArrayList<>(board.getActions(blackPawn2, Type.EN_PASSANT));

        var values = new ArrayList<ActionSimulationResult>();

        values.add(mockSimulationResult(enpassantAction1.get(0), 1));
        values.add(mockSimulationResult(enpassantAction2.get(0), 1));

        values.sort(new ActionValueComparator());

        assertEquals(2, values.size());
        assertEquals(enpassantAction2.get(0), values.getFirst().getAction());
        assertEquals(enpassantAction1.get(0), values.getLast().getAction());
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testActionValueSorting() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e5")
                .withBlackPawn("d6")
                .withWhiteKing("e1")
                .build();

        var whitePawn = board.getPiece("e5").get();
        var blackPawn = board.getPiece("d6").get();

        Action<?> action1 = new PieceMoveAction(whitePawn, positionOf("e6"));
        Action<?> action2 = new PieceCaptureAction(whitePawn, blackPawn);

        var values = new ArrayList<ActionSimulationResult>();

        values.add(mockSimulationResult(action1, 1));
        values.add(mockSimulationResult(action2, 1));

        values.sort(new ActionValueComparator());

        assertEquals(2, values.size());
        assertEquals(action2, values.getFirst().getAction());
        assertEquals(action1, values.getLast().getAction());
    }

    @SuppressWarnings("unchecked")
    private static ActionSimulationResult mockSimulationResult(Action<?> action, int value) {
        return new ActionSimulationResult(
                mock(Board.class), mock(Journal.class), action, mock(Color.class), 0
        );
    }
}