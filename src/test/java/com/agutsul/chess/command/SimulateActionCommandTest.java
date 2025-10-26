package com.agutsul.chess.command;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.exception.CommandException;
import com.agutsul.chess.piece.PawnPiece;

@ExtendWith(MockitoExtension.class)
public class SimulateActionCommandTest {

    @Test
    void testPreExecute() throws CommandException {
        var pawn = mock(PawnPiece.class);
        when(pawn.getPosition())
            .thenReturn(positionOf("e2"));

        var action = new PieceMoveAction<>(pawn, positionOf("e4"));
        var actionCommand = spy(new PerformActionCommand(null, null, null));

        var command = new SimulateActionCommand(actionCommand, action);

        doAnswer(inv -> {
            assertEquals("e2", inv.getArgument(0));
            return null;
        }).when(actionCommand).setSource(anyString());

        doAnswer(inv -> {
            assertEquals("e4", inv.getArgument(0));
            return null;
        }).when(actionCommand).setTarget(anyString());

        command.preExecute();

        verify(actionCommand, times(1)).setSource(anyString());
        verify(actionCommand, times(1)).setTarget(anyString());
    }

    @Test
    void testInvalidCommandPreExecute() throws CommandException {
        var command = spy(new SimulateActionCommand(
                mock(PerformActionCommand.class),
                mock(PieceMoveAction.class)
        ));

        doReturn("e2 ")
            .when(command).adapt(any());

        var thrown = assertThrows(
                IllegalStateException.class,
                () -> command.preExecute()
        );

        assertEquals("Unsupported command format: 'e2 '", thrown.getMessage());

        verify(command, times(1)).adapt(any());
        verify(command, times(1)).preExecute();
    }

    @Test
    void testExecuteInternal() throws CommandException {
        var actionCommand = mock(PerformActionCommand.class);
        var command = spy(new SimulateActionCommand(
                actionCommand,
                mock(PieceMoveAction.class)
        ));

        command.executeInternal();

        verify(actionCommand, times(1)).execute();
    }
}