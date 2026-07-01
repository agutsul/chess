package com.agutsul.chess.activity.impact;

import static com.agutsul.chess.board.state.BoardStateFactory.defaultBoardState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.event.ClearCachedDataEvent;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;

@ExtendWith(MockitoExtension.class)
public class PositionHoleImpactTest extends AbstractImpactTest {

    @Test
    void testWhiteRelativeHoleImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withWhiteKing("h1")
                .withWhitePawns("a2","b2","c2")
                .build();

        // simulate pawn action
        var targetPosition = board.getPosition("b3").get();

        var whitePawn = board.getPiece("b2").get();
        var whiteMoveAction = Stream.of(board.getActions(whitePawn, Action.Type.MOVE))
                .flatMap(Collection::stream)
                .map(action -> (PieceMoveAction<?,?>) action)
                .filter(action -> Objects.equals(action.getPosition(), targetPosition))
                .findFirst()
                .get();

        whiteMoveAction.execute();

        // clear cached data
        ((Observable) board).notifyObservers(new ClearCachedDataEvent(Colors.BLACK));

        // simulate board state initialization
        board.setState(defaultBoardState(board, Colors.WHITE));

        var impact = holeImpact(board, Colors.WHITE, "c3");
        assertNotNull(impact);
        assertEquals(-1, impact.getValue());
    }

    @Test
    void testBlackRelativeHoleImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h8")
                .withBlackPawns("a7","b7","c7")
                .withWhiteKing("e1")
                .build();

        // simulate pawn action
        var targetPosition = board.getPosition("b6").get();

        var blackPawn = board.getPiece("b7").get();
        var blackMoveAction = Stream.of(board.getActions(blackPawn, Action.Type.MOVE))
                .flatMap(Collection::stream)
                .map(action -> (PieceMoveAction<?,?>) action)
                .filter(action -> Objects.equals(action.getPosition(), targetPosition))
                .findFirst()
                .get();

        blackMoveAction.execute();

        // clear cached data
        ((Observable) board).notifyObservers(new ClearCachedDataEvent(Colors.WHITE));

        // simulate board state initialization
        board.setState(defaultBoardState(board, Colors.BLACK));

        var impact = holeImpact(board, Colors.BLACK, "c6");
        assertNotNull(impact);
        assertEquals(1, impact.getValue());
    }

    @Test
    void testWhiteAbsoluteHoleImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withWhiteKing("b1")
                .withWhitePawns("a2","b2","c2")
                .build();

        // simulate pawn action
        var targetPosition = board.getPosition("b3").get();

        var whitePawn = board.getPiece("b2").get();
        var whiteMoveAction = Stream.of(board.getActions(whitePawn, Action.Type.MOVE))
                .flatMap(Collection::stream)
                .map(action -> (PieceMoveAction<?,?>) action)
                .filter(action -> Objects.equals(action.getPosition(), targetPosition))
                .findFirst()
                .get();

        whiteMoveAction.execute();

        // clear cached data
        ((Observable) board).notifyObservers(new ClearCachedDataEvent(Colors.BLACK));

        // simulate board state initialization
        board.setState(defaultBoardState(board, Colors.WHITE));

        var impact = holeImpact(board, Colors.WHITE, "c3");
        assertNotNull(impact);
        assertEquals(-400, impact.getValue());
    }

    @Test
    void testBlackAbsoluteHoleImpact() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("b8")
                .withBlackPawns("a7","b7","c7")
                .withWhiteKing("e1")
                .build();

        // simulate pawn action
        var targetPosition = board.getPosition("b6").get();

        var blackPawn = board.getPiece("b7").get();
        var blackMoveAction = Stream.of(board.getActions(blackPawn, Action.Type.MOVE))
                .flatMap(Collection::stream)
                .map(action -> (PieceMoveAction<?,?>) action)
                .filter(action -> Objects.equals(action.getPosition(), targetPosition))
                .findFirst()
                .get();

        blackMoveAction.execute();

        // clear cached data
        ((Observable) board).notifyObservers(new ClearCachedDataEvent(Colors.WHITE));

        // simulate board state initialization
        board.setState(defaultBoardState(board, Colors.BLACK));

        var impact = holeImpact(board, Colors.BLACK, "c6");
        assertNotNull(impact);
        assertEquals(400, impact.getValue());
    }

    @Test
    void testToString() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("e8")
                .withWhiteKing("b1")
                .withWhitePawns("a2","b2","c2")
                .build();

        // simulate pawn action
        var targetPosition = board.getPosition("b3").get();

        var whitePawn = board.getPiece("b2").get();
        var whiteMoveAction = Stream.of(board.getActions(whitePawn, Action.Type.MOVE))
                .flatMap(Collection::stream)
                .map(action -> (PieceMoveAction<?,?>) action)
                .filter(action -> Objects.equals(action.getPosition(), targetPosition))
                .findFirst()
                .get();

        whiteMoveAction.execute();

        // clear cached data
        ((Observable) board).notifyObservers(new ClearCachedDataEvent(Colors.BLACK));

        // simulate board state initialization
        board.setState(defaultBoardState(board, Colors.WHITE));

        var impact = holeImpact(board, Colors.WHITE, "c3");
        assertNotNull(impact);
        assertEquals("HOLE:ABSOLUTE:[WHITE:c3]", String.valueOf(impact));
    }

    private static PositionHoleImpact<?> holeImpact(Board board, Color color, String position) {
        return Stream.of(getImpact(board, color, position, Impact.Type.HOLE))
                .flatMap(Optional::stream)
                .map(impact -> (PositionHoleImpact<?>) impact)
                .findFirst()
                .orElse(null);
    }
}