package com.agutsul.chess.ai;

import static com.agutsul.chess.board.state.BoardStateFactory.checkMatedBoardState;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.journal.Journal;

@ExtendWith(MockitoExtension.class)
public class PlayerBoardStateResultMatcherTest {

    @Test
    @SuppressWarnings("unchecked")
    void testResultMatch() {
        var board = mock(Board.class);
        when(board.getState())
            .thenReturn(checkMatedBoardState(board, Colors.BLACK));

        var memento = mock(ActionMemento.class);
        when(memento.getColor())
            .thenReturn(Colors.BLACK);

        var journal = mock(Journal.class);
        when(journal.getLast())
            .thenReturn(memento);

        var whiteResult = new ActionSimulationResult<>(
                board, journal, mock(Action.class), Colors.WHITE,  1
        );

        var blackResult = new ActionSimulationResult<>(
                board, journal, mock(Action.class), Colors.BLACK, -1
        );

        whiteResult.setOpponentResult(blackResult);

        var matcher = new PlayerBoardStateResultMatcher<>(Colors.BLACK, BoardState.Type.CHECK_MATED);
        assertTrue(matcher.match(whiteResult));
    }
}