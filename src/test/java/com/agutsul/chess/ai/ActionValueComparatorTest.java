package com.agutsul.chess.ai;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Comparator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.RookPiece;

@ExtendWith(MockitoExtension.class)
public class ActionValueComparatorTest {

    private static final Comparator<TaskResult<Action<?>,Integer>> COMPARATOR =
            new ActionValueComparator<>();

    @Mock
    Journal<ActionMemento<?,?>> journal;
    @Mock
    Observable observable;
    @Mock
    Color color;

    @Test
    void testActionValuePieceMoveActionEqualValueSorting() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e2")
                .withWhiteKing("e1")
                .build();

        var pawn = (PawnPiece<?>) board.getPiece("e2").get();

        var action1 = new PieceMoveAction<>(pawn, positionOf("e3"));
        var action2 = new PieceBigMoveAction<>(pawn, positionOf("e4"));

        var values = new ArrayList<ActionSimulationResult<Integer>>();

        values.add(mockSimulationResult(board, action1, 1));
        values.add(mockSimulationResult(board, action2, 1));

        values.sort(COMPARATOR);

        assertEquals(2, values.size());
        assertEquals(action2, values.getFirst().getAction());
        assertEquals(action1, values.getLast().getAction());
    }

    @Test
    void testActionValuePieceCaptureActionTargetTypeValueSorting() {
        var board = new LabeledBoardBuilder()
                .withWhitePawns("e2", "a4")
                .withBlackPawn("f3")
                .withBlackKnight("b5")
                .withWhiteKing("e1")
                .withBlackKing("e8")
                .build();

        var pawn1 = (PawnPiece<?>) board.getPiece("e2").get();
        var pawn2 = (PawnPiece<?>) board.getPiece("a4").get();

        var knight = (KnightPiece<?>) board.getPiece("b5").get();
        var pawn = (PawnPiece<?>) board.getPiece("f3").get();

        var action1 = new PieceCaptureAction<>(pawn1, pawn);
        var action2 = new PieceCaptureAction<>(pawn2, knight);

        var values = new ArrayList<ActionSimulationResult<Integer>>();

        values.add(mockSimulationResult(board, action1, 1));
        values.add(mockSimulationResult(board, action2, 1));

        values.sort(COMPARATOR);

        assertEquals(2, values.size());
        assertEquals(action2, values.getFirst().getAction());
        assertEquals(action1, values.getLast().getAction());
    }

    @Test
    void testActionValuePiecePromoteActionSorting() {
        var board = new LabeledBoardBuilder()
                .withWhitePawns("e7", "a7")
                .withBlackKnight("b8")
                .withWhiteKing("e1")
                .build();

        var pawn1 = (PawnPiece<?>) board.getPiece("e7").get();
        var pawn2 = (PawnPiece<?>) board.getPiece("a7").get();

        var knight = (KnightPiece<?>) board.getPiece("b8").get();

        var promoteAction1 = new PiecePromoteAction<>(
                new PieceMoveAction<>(pawn1, positionOf("e8")),
                observable
        );

        var promoteAction2 = new PiecePromoteAction<>(
                new PieceCaptureAction<>(pawn2, knight),
                observable
        );

        var values = new ArrayList<ActionSimulationResult<Integer>>();

        values.add(mockSimulationResult(board, promoteAction1, 1));
        values.add(mockSimulationResult(board, promoteAction2, 1));

        values.sort(COMPARATOR);

        assertEquals(2, values.size());
        assertEquals(promoteAction2, values.getFirst().getAction());
        assertEquals(promoteAction1, values.getLast().getAction());
    }

    @Test
    void testActionValuePieceCastlingActionSorting() {
        var board = new LabeledBoardBuilder()
                .withWhiteRooks("a1", "h1")
                .withWhiteKing("e1")
                .build();

        var rook1 = (RookPiece<Color>) board.getPiece("a1").get();
        var rook2 = (RookPiece<Color>) board.getPiece("h1").get();

        var king = (KingPiece<Color>) board.getPiece("e1").get();

        var castlingAction1 = new PieceCastlingAction<>(
                Castlingable.Side.KING,
                new CastlingMoveAction<>(rook2, positionOf("f1")),
                new CastlingMoveAction<>(king,  positionOf("g1"))
        );

        var castlingAction2 = new PieceCastlingAction<>(
                Castlingable.Side.QUEEN,
                new CastlingMoveAction<>(rook1, positionOf("d1")),
                new CastlingMoveAction<>(king,  positionOf("c1"))
        );

        var values = new ArrayList<ActionSimulationResult<Integer>>();

        values.add(mockSimulationResult(board, castlingAction1, 1));
        values.add(mockSimulationResult(board, castlingAction2, 1));

        values.sort(COMPARATOR);

        assertEquals(2, values.size());
        assertEquals(castlingAction2, values.getFirst().getAction());
        assertEquals(castlingAction1, values.getLast().getAction());
    }

    @Test
    void testActionValuePieceEnPassantActionSorting() {
        var board = new LabeledBoardBuilder()
                .withWhitePawns("a2", "h2")
                .withBlackPawns("b4", "g4")
                .build();

        var whitePawn1 = (PawnPiece<?>) board.getPiece("a2").get();
        var whitePawn2 = (PawnPiece<?>) board.getPiece("h2").get();

        var moveAction1 = new PieceBigMoveAction<>(whitePawn1, positionOf("a4"));
        moveAction1.execute();

        var moveAction2 = new PieceBigMoveAction<>(whitePawn2, positionOf("h4"));
        moveAction2.execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));
        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.BLACK));

        var blackPawn1 = (PawnPiece<?>) board.getPiece("b4").get();
        var blackPawn2 = (PawnPiece<?>) board.getPiece("g4").get();

        var enpassantAction1 = new ArrayList<>(board.getActions(blackPawn1, Type.EN_PASSANT));
        var enpassantAction2 = new ArrayList<>(board.getActions(blackPawn2, Type.EN_PASSANT));

        var values = new ArrayList<ActionSimulationResult<Integer>>();

        values.add(mockSimulationResult(board, enpassantAction1.getFirst(), 1));
        values.add(mockSimulationResult(board, enpassantAction2.getFirst(), 1));

        values.sort(COMPARATOR);

        assertEquals(2, values.size());
        assertEquals(enpassantAction2.getFirst(), values.getFirst().getAction());
        assertEquals(enpassantAction1.getFirst(), values.getLast().getAction());
    }

    @Test
    void testActionValueSorting() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e5")
                .withBlackPawn("d6")
                .withWhiteKing("e1")
                .build();

        var whitePawn = (PawnPiece<?>) board.getPiece("e5").get();
        var blackPawn = (PawnPiece<?>) board.getPiece("d6").get();

        Action<?> action1 = new PieceMoveAction<>(whitePawn, positionOf("e6"));
        Action<?> action2 = new PieceCaptureAction<>(whitePawn, blackPawn);

        var values = new ArrayList<ActionSimulationResult<Integer>>();

        values.add(mockSimulationResult(board, action1, 1));
        values.add(mockSimulationResult(board, action2, 1));

        values.sort(COMPARATOR);

        assertEquals(2, values.size());
        assertEquals(action2, values.getFirst().getAction());
        assertEquals(action1, values.getLast().getAction());
    }

    private ActionSimulationResult<Integer> mockSimulationResult(Board board, Action<?> action, int value) {
        return new ActionSimulationResult<Integer>(board, journal, action, color, value);
    }
}