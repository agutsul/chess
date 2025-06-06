package com.agutsul.chess.board.state;

import static com.agutsul.chess.board.state.BoardStateFactory.threeFoldRepetitionBoardState;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.color.Colors;

@ExtendWith(MockitoExtension.class)
public class ThreeFoldRepetitionBoardStateTest {

    @Test
    void testGetActions() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("a2")
                .build();

        var actionMemento = mock(ActionMemento.class);
        when(actionMemento.getColor())
            .thenReturn(Colors.WHITE);

        board.setState(threeFoldRepetitionBoardState(board, actionMemento));

        var whitePawn = board.getPiece("a2").get();
        assertFalse(board.getActions(whitePawn).isEmpty());
    }

    @Test
    void testGetImpacts() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("a2")
                .build();

        var actionMemento = mock(ActionMemento.class);
        when(actionMemento.getColor())
            .thenReturn(Colors.WHITE);

        board.setState(threeFoldRepetitionBoardState(board, actionMemento));

        var whitePawn = board.getPiece("a2").get();
        assertFalse(board.getImpacts(whitePawn).isEmpty());
    }
}