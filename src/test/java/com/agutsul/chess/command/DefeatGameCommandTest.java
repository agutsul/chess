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
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.event.DefeatExecutionEvent;
import com.agutsul.chess.activity.action.event.DefeatPerformedEvent;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.player.UserPlayer;

@ExtendWith(MockitoExtension.class)
public class DefeatGameCommandTest {

    @Test
    void testDefeatGameCommand() {
        var board = new LabeledBoardBuilder().build();
        var game = mock(AbstractPlayableGame.class);
        when(game.getBoard())
            .thenReturn(board);

        var command = new DefeatGameCommand(game, new UserPlayer("test", Colors.WHITE));
        command.execute();

        var boardState = board.getState();

        assertEquals(BoardState.Type.AGREED_DEFEAT, boardState.getType());
        assertEquals(Colors.WHITE, boardState.getColor());

        verify(game, times(1)).notifyObservers(any(DefeatExecutionEvent.class));
        verify(game, times(1)).notifyObservers(any(DefeatPerformedEvent.class));
    }

    @Test
    void testDefeatGameCommandWithException() {
        var board = mock(Board.class);
        doThrow(new RuntimeException("test"))
            .when(board).setState(any());

        var game = mock(AbstractPlayableGame.class);
        when(game.getBoard())
            .thenReturn(board);

        var command = new DefeatGameCommand(game, new UserPlayer("test", Colors.WHITE));
        var thrown = assertThrows(
                IllegalActionException.class,
                () -> command.execute()
        );

        assertEquals("test", thrown.getMessage());
        verify(game, times(1)).notifyObservers(any(DefeatExecutionEvent.class));
    }
}