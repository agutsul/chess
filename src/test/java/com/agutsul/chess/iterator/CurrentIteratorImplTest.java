package com.agutsul.chess.iterator;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CurrentIteratorImplTest {

    private static final List<Object>  EMPTY_LIST = emptyList();
    private static final List<Integer> INT_LIST   = List.of(1);

    @Test
    void testHasNextWithEmptyIterator() {
        var iterator = new CurrentIteratorImpl<Object>(EMPTY_LIST.iterator());
        assertFalse(iterator.hasNext());
    }

    @Test
    void testHasNextWithNonEmptyIterator() {
        var iterator = new CurrentIteratorImpl<Integer>(INT_LIST.iterator());
        assertTrue(iterator.hasNext());
    }

    @Test
    void testNextWithEmptyIterator() {
        var iterator = new CurrentIteratorImpl<Object>(EMPTY_LIST.iterator());
        assertNull(iterator.next());
    }

    @Test
    void testNextWithNonEmptyIterator() {
        var iterator = new CurrentIteratorImpl<Integer>(INT_LIST.iterator());

        var next = iterator.next();

        assertNotNull(next);
        assertEquals(1, next.intValue());
    }

    @Test
    void testCurrentWithEmptyIterator() {
        var iterator = new CurrentIteratorImpl<Object>(EMPTY_LIST.iterator());
        assertNull(iterator.current());
    }

    @Test
    void testCurrentWithNonEmptyIterator() {
        var iterator = new CurrentIteratorImpl<Integer>(INT_LIST.iterator());
        assertNull(iterator.current());

        var next = iterator.next();

        assertNotNull(next);
        assertEquals(1, next.intValue());

        var current = iterator.current();

        assertNotNull(current);
        assertEquals(1, current.intValue());

        assertNull(iterator.next());
    }
}