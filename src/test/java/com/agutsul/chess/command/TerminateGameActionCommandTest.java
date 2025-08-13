package com.agutsul.chess.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.event.ActionTerminatedEvent;
import com.agutsul.chess.activity.action.event.ActionTerminationEvent;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.event.GameTerminationEvent.Type;
import com.agutsul.chess.player.UserPlayer;

@ExtendWith(MockitoExtension.class)
public class TerminateGameActionCommandTest {

    @Mock
    AbstractPlayableGame game;

    @Test
    void testTerminateGameCommand() {
        var board = new LabeledBoardBuilder().build();
        when(game.getBoard())
            .thenReturn(board);

        var player = new UserPlayer("test", Colors.WHITE);

        var command = new TerminateGameActionCommand(game, player, Type.DEFEAT);
        command.execute();

        var boardState = board.getState();

        assertEquals(BoardState.Type.AGREED_DEFEAT, boardState.getType());
        assertEquals(Colors.WHITE, boardState.getColor());

        verify(game, times(1)).notifyObservers(any(ActionTerminationEvent.class));
        verify(game, times(1)).notifyObservers(any(ActionTerminatedEvent.class));
    }

    @Test
    void testTerminateGameCommandWithException() {
        var board = mock(Board.class);
        doThrow(new RuntimeException("test"))
            .when(board).setState(any());

        when(game.getBoard())
            .thenReturn(board);

        var player = new UserPlayer("test", Colors.WHITE);

        var command = new TerminateGameActionCommand(game, player, Type.DEFEAT);
        var thrown = assertThrows(
                IllegalActionException.class,
                () -> command.execute()
        );

        assertEquals("test", thrown.getMessage());
        verify(game, times(1)).notifyObservers(any(ActionTerminationEvent.class));
    }
}