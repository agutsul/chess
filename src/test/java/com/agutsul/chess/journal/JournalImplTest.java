package com.agutsul.chess.journal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.memento.ActionMementoFactory;
import com.agutsul.chess.board.BoardBuilder;

@ExtendWith(MockitoExtension.class)
public class JournalImplTest {

    private final static ActionMementoFactory MEMENTO_FACTORY = ActionMementoFactory.INSTANCE;

    @Test
    void testAddMemento() {
        var memento = createMemento();

        var journal = new JournalImpl<Memento>();
        assertTrue(journal.size() == 0);

        journal.add(memento);

        assertEquals(1, journal.size());
    }

    @Test
    void testRemoveMemento() {
        var memento = createMemento();

        var journal = new JournalImpl<Memento>();
        journal.add(memento);

        assertEquals(1, journal.size());

        journal.remove(journal.size() - 1);

        assertTrue(journal.size() == 0);
    }

    @Test
    void testGetMemento() {
        var memento = createMemento();

        var journal = new JournalImpl<Memento>();
        journal.add(memento);

        assertEquals(memento, journal.get(0));
    }

    @Test
    void testGetMementoIndexOutOfBoundsException() {
        var journal = new JournalImpl<Memento>();

        var thrown = assertThrows(
                IndexOutOfBoundsException.class,
                () -> journal.get(0)
            );

        assertEquals("Index 0 out of bounds for length 0", thrown.getMessage());
    }

    private static Memento createMemento() {
        var board = new BoardBuilder()
                .withWhitePawn("a2")
                .build();

        var pawn = board.getPiece("a2").get();
        var actions = board.getActions(pawn);
        assertFalse(actions.isEmpty());

        var targetPosition = board.getPosition("a3").get();
        var moveAction = actions.stream()
                .filter(action -> Action.Type.MOVE.equals(action.getType()))
                .filter(action -> Objects.equals(action.getPosition(), targetPosition))
                .findFirst();

        assertFalse(moveAction.isEmpty());

        var memento = MEMENTO_FACTORY.create(moveAction.get());
        assertEquals("MOVE PAWN(a2 a3)", memento.toString());

        return memento;
    }
}
