package com.agutsul.chess.ai;

import static com.agutsul.chess.board.state.BoardStateFactory.checkMatedBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.checkedBoardState;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.journal.Journal;

@ExtendWith(MockitoExtension.class)
public class TerminalBoardStateResultMatcherTest {

    @Test
    @SuppressWarnings("unchecked")
    void testResultTerminalMatch() {
        var board = mock(Board.class);
        when(board.getState())
            .thenReturn(checkMatedBoardState(board, Colors.WHITE));

        var result = new ActionSimulationResult<>(
                board, mock(Journal.class), mock(Action.class), Colors.WHITE,  1
        );

        var matcher = new TerminalBoardStateResultMatcher<>();
        assertTrue(matcher.match(result));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testResultNonTerminalMatch() {
        var board = mock(Board.class);
        when(board.getState())
            .thenReturn(checkedBoardState(board, Colors.BLACK));

        var result = new ActionSimulationResult<>(
                board, mock(Journal.class), mock(Action.class), Colors.BLACK, -1
        );

        var matcher = new TerminalBoardStateResultMatcher<>();
        assertFalse(matcher.match(result));
    }
}