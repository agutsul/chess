package com.agutsul.chess.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.exception.CommandException;
import com.agutsul.chess.journal.Journal;

@ExtendWith(MockitoExtension.class)
public class SimulateGameActionCommandTest {

    @Test
    @SuppressWarnings("unchecked")
    void testPreExecute() throws CommandException, IOException {
        try (var command = new SimulateGameActionCommand<>(mock(Board.class), mock(Journal.class),
                mock(ForkJoinPool.class), Colors.WHITE, mock(Action.class))) {

            var thrown = assertThrows(
                    IllegalStateException.class,
                    () -> command.preExecute()
            );

            assertEquals("Value evaluator is not set", thrown.getMessage());
        }
    }
}