package com.agutsul.chess.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.BoardBuilder;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.exception.IllegalPositionException;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.position.Position;

@ExtendWith(MockitoExtension.class)
public class PerformActionCommandTest {

    @Mock
    Observable observable;

    @Test
    void testValidationOfMissedSourcePiece() {
        var player = mock(Player.class);

        var board = new BoardBuilder().build();
        var command = new PerformActionCommand(player, board, observable);

        var thrown = assertThrows(
                IllegalPositionException.class,
                () -> command.setSource("e2")
            );

        assertEquals("Missed piece on position: e2", thrown.getMessage());
    }

    @Test
    void testValidationOfSourcePosition() {
        var player = mock(Player.class);

        var board = new BoardBuilder().build();
        var command = new PerformActionCommand(player, board, observable);

        var thrown = assertThrows(
                IllegalPositionException.class,
                () -> command.setSource("e9")
            );

        assertEquals("Missed piece on position: e9", thrown.getMessage());
    }

    @Test
    void testValidationOfTargetPosition() {
        var player = mock(Player.class);

        var board = new BoardBuilder().build();
        var command = new PerformActionCommand(player, board, observable);

        var thrown = assertThrows(
                IllegalPositionException.class,
                () -> command.setTarget("e9")
            );

        assertEquals("Missed position: e9", thrown.getMessage());
    }

    @Test
    void testInvalidAction() {
        var player = mock(Player.class);
        when(player.getColor())
            .thenReturn(Colors.WHITE);

        var board = new BoardBuilder()
                .withWhitePawn("e2")
                .build();

        var command = new PerformActionCommand(player, board, observable);
        command.setSource("e2");
        command.setTarget("c3");

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> command.execute()
            );

        assertEquals("Invalid action for pawn at 'e2' and position 'c3'", thrown.getMessage());
    }


    @Test
    @SuppressWarnings("unchecked")
    void testActionCommandException() {
        var player = mock(Player.class);
        when(player.getColor())
            .thenReturn(Colors.WHITE);

        var board = mock(Board.class);

        var sourcePosition = mock(Position.class);
        var piece = mock(PawnPiece.class);
        when(piece.getColor())
            .thenReturn(Colors.WHITE);
        when(piece.getPosition())
            .thenReturn(sourcePosition);

        when(board.getPiece(anyString()))
            .thenReturn(Optional.of(piece));

        var targetPosition = mock(Position.class);
        when(board.getPosition(anyString()))
            .thenReturn(Optional.of(targetPosition));

        var targetAction = mock(PieceMoveAction.class);
        when(targetAction.getType())
            .thenReturn(Action.Type.MOVE);
        when(targetAction.getPosition())
            .thenReturn(targetPosition);
        when(targetAction.getSource())
            .thenReturn(piece);

        var errorMessage = "test";
        doThrow(new IllegalActionException("test"))
            .when(targetAction)
            .execute();

        when(board.getActions(any()))
            .thenReturn(List.of(targetAction));

        var command = new PerformActionCommand(player, board, observable);
        command.setSource("e2");
        command.setTarget("e3");

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> command.execute()
        );

        assertEquals(errorMessage, thrown.getMessage());
        verify(observable, times(1)).notifyObservers(any());
    }

    @Test
    void testPerformAction() {
        var player = mock(Player.class);
        when(player.getColor())
            .thenReturn(Colors.WHITE);

        var board = new StandardBoard();

        var command = new PerformActionCommand(player, board, observable);
        command.setSource("e2");
        command.setTarget("e4");

        var sourcePosition = board.getPosition("e2").get();
        assertFalse(board.isEmpty(sourcePosition));

        var targetPosition = board.getPosition("e4").get();
        assertTrue(board.isEmpty(targetPosition));

        command.execute();

        assertTrue(board.isEmpty(sourcePosition));
        assertFalse(board.isEmpty(targetPosition));

        var piece = board.getPiece("e4").get();
        var positions = piece.getPositions();

        assertEquals(2, positions.size());
        assertTrue(positions.stream()
                .map(position -> String.valueOf(position))
                .collect(Collectors.toList())
                .containsAll(List.of("e2","e4"))
            );

        verify(observable, times(2)).notifyObservers(any());
    }

    @Test
    void testPerformActionByOpponentPiece() {
        var player = mock(Player.class);
        when(player.getColor())
            .thenReturn(Colors.BLACK);

        var board = new StandardBoard();

        var command = new PerformActionCommand(player, board, observable);

        var sourcePosition = board.getPosition("e2").get();
        assertFalse(board.isEmpty(sourcePosition));

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> command.setSource("e2")
        );

        assertEquals("Unable to use opponent piece: e2", thrown.getMessage());
    }
}