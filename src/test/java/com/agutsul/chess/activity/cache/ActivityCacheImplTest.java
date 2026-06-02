package com.agutsul.chess.activity.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceMoveAction;

@ExtendWith(MockitoExtension.class)
public class ActivityCacheImplTest {

    private static final Action.Type KEY1 = Action.Type.MOVE;
    private static final Action.Type KEY2 = Action.Type.CAPTURE;

    @Mock
    PieceMoveAction<?,?> action1;
    @Mock
    PieceCaptureAction<?,?,?,?> action2;

    ActivityCache<Action.Type,Action<?>> actionCache;

    @BeforeEach
    void setUp() {
        this.actionCache = new ActivityCacheImpl<>();
    }

    @Test
    void testPutAll() {
        when(action1.getType())
            .thenReturn(KEY1);
        when(action2.getType())
            .thenReturn(KEY2);

        Collection<Action<?>> actions = List.of(action1, action2);
        assertTrue(this.actionCache.isEmpty());

        this.actionCache.putAll(actions);
        assertFalse(this.actionCache.isEmpty());

        var value1 = this.actionCache.get(KEY1);
        assertEquals(1, value1.size());
        assertTrue(value1.contains(action1));

        var value2 = this.actionCache.get(KEY2);
        assertEquals(1, value2.size());
        assertTrue(value2.contains(action2));
    }

    @Test
    void testGetAll() {
        Collection<Action<?>> actions = List.of(action1, action2);
        assertTrue(this.actionCache.isEmpty());

        this.actionCache.putAll(actions);

        var values = this.actionCache.getAll();

        assertEquals(actions.size(), values.size());
        assertTrue(values.containsAll(actions));
    }

    @Test
    void testPutGet() {
        this.actionCache.put(KEY1, action1);

        var value1 = this.actionCache.get(KEY1);
        assertTrue(value1.contains(action1));

        var value2 = this.actionCache.get(KEY2);
        assertTrue(value2.isEmpty());
    }

    @Test
    void testClear() {
        this.actionCache.put(KEY1, action1);

        assertFalse(this.actionCache.isEmpty());

        this.actionCache.clear();

        assertTrue(this.actionCache.isEmpty());
    }
}