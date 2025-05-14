package com.agutsul.chess.board.state;

import static com.agutsul.chess.board.state.BoardStateFactory.fiveFoldRepetitionBoardState;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.color.Colors;

@ExtendWith(MockitoExtension.class)
public class FiveFoldRepetitionBoardStateTest {

    @Test
    void testGetActions() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("a2")
                .build();

        var actionMemento = mock(ActionMemento.class);
        when(actionMemento.getColor())
            .thenReturn(Colors.WHITE);

        board.setState(fiveFoldRepetitionBoardState(board, actionMemento));

        var whitePawn = board.getPiece("a2").get();
        assertTrue(board.getActions(whitePawn).isEmpty());
    }

    @Test
    void testGetImpacts() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("a2")
                .build();

        var actionMemento = mock(ActionMemento.class);
        when(actionMemento.getColor())
            .thenReturn(Colors.WHITE);

        board.setState(fiveFoldRepetitionBoardState(board, actionMemento));

        var whitePawn = board.getPiece("a2").get();
        assertTrue(board.getImpacts(whitePawn).isEmpty());
    }
}
