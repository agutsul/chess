package com.agutsul.chess.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.piece.PawnPiece;

@ExtendWith(MockitoExtension.class)
public class SimulationGameTest {

    @Test
    void testSimulationGameExecution() throws IOException {
        var board   = new StandardBoard();
        var journal = new JournalImpl();

        var pawnPiece = (PawnPiece<?>) board.getPiece("e2").get();
        var position  = board.getPosition("e4").get();

        @SuppressWarnings({ "unchecked", "rawtypes" })
        var action = new PieceMoveAction(pawnPiece, position);
        try (var game = new SimulationGame(Colors.WHITE, board, journal, action)) {
            assertEquals(Colors.WHITE, game.getCurrentPlayer().getColor());

            game.run();

            assertEquals(Colors.BLACK, game.getCurrentPlayer().getColor());
            assertNotEquals(board,   game.getBoard());
            assertNotEquals(journal, game.getJournal());

            assertEquals(pawnPiece.getPositions().size(), 1);

            var simulationBoard = game.getBoard();

            assertTrue(simulationBoard.isEmpty(pawnPiece.getPosition()));
            assertFalse(simulationBoard.isEmpty(position));

            var simulatedPawn = simulationBoard.getPiece("e4").get();
            assertEquals(simulatedPawn.getPositions().size(), 2);

            var simulatedJournal = game.getJournal();

            assertFalse(simulatedJournal.isEmpty());
            assertEquals(simulatedJournal.size(), 1);

            var simulatedActionMemento = simulatedJournal.get(0);

            assertEquals(Action.Type.BIG_MOVE, simulatedActionMemento.getActionType());
            assertEquals(Colors.WHITE, simulatedActionMemento.getColor());
            assertEquals("e2", String.valueOf(simulatedActionMemento.getSource()));
            assertEquals("e4", String.valueOf(simulatedActionMemento.getTarget()));
        }
    }
}
