package com.agutsul.chess.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.exception.CommandException;
import com.agutsul.chess.journal.Journal;

@ExtendWith(MockitoExtension.class)
public class SimulateGameActionCommandTest {

    @Mock
    Board board;
    @Mock
    Journal<ActionMemento<?,?>> journal;
    @Mock
    Action<?> action;
    @Mock
    ForkJoinPool forkJoinPool;

    @Test
    void testPreExecute() throws CommandException, IOException {
        try (var command = new SimulateGameActionCommand<>(board, journal,
                forkJoinPool, Colors.WHITE, action)) {

            var thrown = assertThrows(
                    IllegalStateException.class,
                    () -> command.preExecute()
            );

            assertEquals("Value evaluator is not set", thrown.getMessage());
        }
    }
}