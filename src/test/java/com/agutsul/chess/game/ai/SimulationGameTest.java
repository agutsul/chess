package com.agutsul.chess.game.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.game.event.GameExceptionEvent;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.piece.PawnPiece;

@ExtendWith(MockitoExtension.class)
public class SimulationGameTest {

    @AutoClose
    ForkJoinPool forkJoinPool = new ForkJoinPool(2);

    @Test
    void testSimulationGameException() throws IOException {
        var board   = new StandardBoard();
        var journal = new JournalImpl();

        var pawnPiece = (PawnPiece<?>) board.getPiece("e2").get();
        var position  = board.getPosition("e4").get();

        var exception = new RuntimeException("test");

        var action = new PieceMoveAction<>(pawnPiece, position);
        try (var game = spy(new SimulationGame(board, journal, forkJoinPool, Colors.WHITE, action))) {
            doThrow(exception)
                .when(game).hasNext();

            doCallRealMethod()
                .when(game).getCurrentPlayer();

            doCallRealMethod()
                .when(game).notifyObservers(any());

            doCallRealMethod()
                .when(game).run();

            doCallRealMethod()
                .when(game).addObserver(any());

            game.addObserver(new AbstractEventObserver<GameExceptionEvent>() {

                @Override
                protected void process(GameExceptionEvent event) {
                    var exceptionEvent = event;
                    var throwable = exceptionEvent.getThrowable();

                    assertEquals(exception, throwable);
                    assertEquals("test", throwable.getMessage());
                }
            });

            var currentPlayer = game.getCurrentPlayer();

            var thrown = assertThrows(
                    RuntimeException.class,
                    () -> game.run()
            );

            assertEquals("test", thrown.getMessage());
            assertEquals(currentPlayer, game.getCurrentPlayer());
        }
    }

    @Test
    void testSimulationGameExecution() throws IOException {
        var board   = new StandardBoard();
        var journal = new JournalImpl();

        var pawnPiece = (PawnPiece<?>) board.getPiece("e2").get();
        var position  = board.getPosition("e4").get();

        var action = new PieceMoveAction<>(pawnPiece, position);
        try (var game = new SimulationGame(board, journal, forkJoinPool, Colors.WHITE, action)) {
            assertEquals(Colors.WHITE, game.getCurrentPlayer().getColor());

            game.run();

            assertEquals(Colors.BLACK, game.getCurrentPlayer().getColor());
            assertNotEquals(board,   game.getBoard());
            assertNotEquals(journal, game.getJournal());

            assertEquals(1, pawnPiece.getPositions().size());

            var simulationBoard = game.getBoard();

            assertTrue(simulationBoard.isEmpty(pawnPiece.getPosition()));
            assertFalse(simulationBoard.isEmpty(position));

            var simulatedPawn = simulationBoard.getPiece("e4").get();
            assertEquals(2, simulatedPawn.getPositions().size());

            var simulatedJournal = game.getJournal();

            assertFalse(simulatedJournal.isEmpty());
            assertEquals(1, simulatedJournal.size());

            var simulatedActionMemento = simulatedJournal.getFirst();

            assertEquals(Action.Type.BIG_MOVE, simulatedActionMemento.getActionType());
            assertEquals(Colors.WHITE, simulatedActionMemento.getColor());

            assertEquals("e2", String.valueOf(simulatedActionMemento.getSource()));
            assertEquals("e4", String.valueOf(simulatedActionMemento.getTarget()));
        }
    }
}