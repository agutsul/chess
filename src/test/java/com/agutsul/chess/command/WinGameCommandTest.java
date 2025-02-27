package com.agutsul.chess.command;

import static com.agutsul.chess.board.state.BoardStateFactory.defaultBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.fiveFoldRepetitionBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.insufficientMaterialBoardState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.event.WinExecutionEvent;
import com.agutsul.chess.activity.action.event.WinPerformedEvent;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.StringBoardBuilder;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.player.UserPlayer;

@ExtendWith(MockitoExtension.class)
public class WinGameCommandTest {

    @Test
    void testWinGameCommand() {
        var board = spy(new StringBoardBuilder().build());

        var player1BoardState = insufficientMaterialBoardState(board, Colors.WHITE);
        var player2BoardState = defaultBoardState(board, Colors.BLACK);

        when(board.getStates())
            .thenReturn(List.of(player1BoardState, player2BoardState));

        doCallRealMethod()
            .when(board).setState(any());

        var game = mock(AbstractPlayableGame.class);
        when(game.getBoard())
            .thenReturn(board);

        var command = new WinGameCommand(game, new UserPlayer("test", Colors.BLACK));
        command.execute();

        var boardState = board.getState();
        verify(board, times(1)).setState(any());

        assertEquals(BoardState.Type.AGREED_WIN, boardState.getType());
        assertEquals(Colors.BLACK, boardState.getColor());

        verify(game, times(1)).notifyObservers(any(WinExecutionEvent.class));
        verify(game, times(1)).notifyObservers(any(WinPerformedEvent.class));
    }

    @Test
    void testWinGameCommandWithUnknownOpponentBoardStateException() {
        var game = mock(AbstractPlayableGame.class);
        when(game.getBoard())
            .thenReturn(new StringBoardBuilder().build());

        var command = new WinGameCommand(game, new UserPlayer("test", Colors.WHITE));
        var thrown = assertThrows(
                IllegalActionException.class,
                () -> command.execute()
        );

        assertEquals(
                "WHITE: Unable to win with unknown opponent's board state",
                thrown.getMessage()
        );

        verify(game, times(1)).notifyObservers(any(WinExecutionEvent.class));
    }

    @Test
    void testWinGameCommandDuringInsufficientMaterialBoardStateException() {
        var board = mock(Board.class);
        when(board.getState())
            .thenReturn(insufficientMaterialBoardState(board, Colors.WHITE));

        var game = mock(AbstractPlayableGame.class);
        when(game.getBoard())
            .thenReturn(board);

        var command = new WinGameCommand(game, new UserPlayer("test", Colors.WHITE));
        var thrown = assertThrows(
                IllegalActionException.class,
                () -> command.execute()
        );

        assertEquals(
                "WHITE: Unable to win while being in 'INSUFFICIENT_MATERIAL' board state",
                thrown.getMessage()
        );

        verify(game, times(1)).notifyObservers(any(WinExecutionEvent.class));
    }

    @Test
    void testWinGameCommandDuringTerminalBoardStateException() {
        var board = mock(Board.class);
        when(board.getState())
            .thenReturn(fiveFoldRepetitionBoardState(board, Colors.WHITE));

        var game = mock(AbstractPlayableGame.class);
        when(game.getBoard())
            .thenReturn(board);

        var command = new WinGameCommand(game, new UserPlayer("test", Colors.WHITE));
        var thrown = assertThrows(
                IllegalActionException.class,
                () -> command.execute()
        );

        assertEquals(
                "WHITE: Unable to win while being in 'FIVE_FOLD_REPETITION' board state",
                thrown.getMessage()
        );

        verify(game, times(1)).notifyObservers(any(WinExecutionEvent.class));
    }

    @Test
    void testWinGameCommandDuringOpponentDefaultBoardStateException() {
        var board = mock(Board.class);

        var player1BoardState = defaultBoardState(board, Colors.WHITE);
        var player2BoardState = defaultBoardState(board, Colors.BLACK);

        when(board.getState())
            .thenReturn(player2BoardState);

        when(board.getStates())
            .thenReturn(List.of(player1BoardState, player2BoardState));

        var game = mock(AbstractPlayableGame.class);
        when(game.getBoard())
            .thenReturn(board);

        var command = new WinGameCommand(game, new UserPlayer("test", Colors.BLACK));
        var thrown = assertThrows(
                IllegalActionException.class,
                () -> command.execute()
        );

        assertEquals(
                "BLACK: Unable to win with 'DEFAULT' opponent's board state",
                thrown.getMessage()
        );

        verify(game, times(1)).notifyObservers(any(WinExecutionEvent.class));
    }

    @Test
    void testWinGameCommandWithException() {
        var board = spy(new StringBoardBuilder().build());

        var player1BoardState = insufficientMaterialBoardState(board, Colors.WHITE);
        var player2BoardState = defaultBoardState(board, Colors.BLACK);

        when(board.getStates())
            .thenReturn(List.of(player1BoardState, player2BoardState));

        doThrow(new RuntimeException("test"))
            .when(board).setState(any());

        var game = mock(AbstractPlayableGame.class);
        when(game.getBoard())
            .thenReturn(board);

        var command = new WinGameCommand(game, new UserPlayer("test", Colors.BLACK));
        var thrown = assertThrows(
                IllegalActionException.class,
                () -> command.execute()
        );

        assertEquals("test", thrown.getMessage());
        verify(game, times(1)).notifyObservers(any(WinExecutionEvent.class));
    }
}