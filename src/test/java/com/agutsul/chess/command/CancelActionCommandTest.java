package com.agutsul.chess.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.memento.ActionMementoMock;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.event.ClearPieceDataEvent;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.piece.Piece;

@ExtendWith(MockitoExtension.class)
public class CancelActionCommandTest {

    @Test
    @SuppressWarnings("unchecked")
    void testNoActionToCancelException() {
        var journal = mock(Journal.class);
        when(journal.isEmpty())
            .thenReturn(true);

        var game = mock(AbstractPlayableGame.class);
        when(game.getJournal())
            .thenReturn(journal);

        var command = new CancelActionCommand(game, Colors.WHITE);
        var thrown = assertThrows(
                IllegalActionException.class,
                () -> command.execute()
            );

        assertEquals("No action to cancel", thrown.getMessage());
    }

    @Test
    void testWrongPlayerActionException() {
        var game = mock(AbstractPlayableGame.class);

        var journal = new JournalImpl();
        journal.add(new ActionMementoMock<>(
                Colors.BLACK,
                Action.Type.MOVE,
                Piece.Type.PAWN,
                "e7",
                "e5"
        ));

        when(game.getJournal())
            .thenReturn(journal);

        var command = new CancelActionCommand(game, Colors.WHITE);
        var thrown = assertThrows(
                IllegalActionException.class,
                () -> command.execute()
            );

        assertEquals("Unexpected player action", thrown.getMessage());
    }

    @Test
    void testCancelActionCommandWithExecutionException() {
        var game = mock(AbstractPlayableGame.class);

        var journal = new JournalImpl();
        journal.add(new ActionMementoMock<>(
                Colors.WHITE,
                Action.Type.MOVE,
                Piece.Type.PAWN,
                "e2",
                "e4"
        ));

        when(game.getJournal())
            .thenReturn(journal);

        var board = new LabeledBoardBuilder()
                .withWhitePawn("e4")
                .build();

        when(game.getBoard())
            .thenReturn(board);

        var sourcePosition = board.getPosition("e2").get();
        var targetPosition = board.getPosition("e4").get();

        assertTrue(board.isEmpty(sourcePosition));
        assertFalse(board.isEmpty(targetPosition));

        var command = new CancelActionCommand(game, Colors.WHITE);
        var thrown = assertThrows(
                IllegalActionException.class,
                () -> command.execute()
        );

        assertEquals("Unable to cancel unvisited position 'e2'", thrown.getMessage());
    }

    @Test
    void testCancelActionCommand() {
        var game = mock(AbstractPlayableGame.class);

        var journal = new JournalImpl();
        journal.add(new ActionMementoMock<>(
                Colors.WHITE,
                Action.Type.MOVE,
                Piece.Type.PAWN,
                "e2",
                "e4"
        ));

        when(game.getJournal())
            .thenReturn(journal);

        var board = new LabeledBoardBuilder()
                .withWhitePawn("e2")
                .build();

        when(game.getBoard())
            .thenReturn(board);

        var pawn = board.getPiece("e2").get();
        var sourcePosition = board.getPosition("e2").get();
        var targetPosition = board.getPosition("e4").get();

        @SuppressWarnings({ "unchecked", "rawtypes" })
        var moveAction = new PieceMoveAction(pawn, targetPosition);
        moveAction.execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        assertTrue(board.isEmpty(sourcePosition));
        assertFalse(board.isEmpty(targetPosition));

        var command = new CancelActionCommand(game, Colors.WHITE);
        command.execute();

        ((Observable) board).notifyObservers(new ClearPieceDataEvent(Colors.WHITE));

        assertTrue(board.isEmpty(targetPosition));
        assertFalse(board.isEmpty(sourcePosition));
        assertEquals(sourcePosition, pawn.getPosition());

        verify(game, times(2)).notifyObservers(any());
    }
}