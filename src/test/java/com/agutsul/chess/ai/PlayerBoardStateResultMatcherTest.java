package com.agutsul.chess.ai;

import static com.agutsul.chess.board.state.BoardStateFactory.checkMatedBoardState;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.journal.Journal;

@ExtendWith(MockitoExtension.class)
public class PlayerBoardStateResultMatcherTest {

    @Mock
    Board board;
    @Mock
    Journal<ActionMemento<?,?>> journal;
    @Mock
    ActionMemento<?,?> memento;
    @Mock
    Action<?> action;

    @Test
    void testResultMatch() {
        when(board.getState())
            .thenReturn(checkMatedBoardState(board, Colors.BLACK));

        when(memento.getColor())
            .thenReturn(Colors.BLACK);

        when(journal.getLast())
            .thenAnswer(inv -> memento);

        var whiteResult = new ActionSimulationResult<>(board, journal, action, Colors.WHITE,  1);
        var blackResult = new ActionSimulationResult<>(board, journal, action, Colors.BLACK, -1);

        whiteResult.setOpponentResult(blackResult);

        var matcher = new PlayerBoardStateResultMatcher<>(Colors.BLACK, BoardState.Type.CHECK_MATED);
        assertTrue(matcher.match(whiteResult));
    }
}