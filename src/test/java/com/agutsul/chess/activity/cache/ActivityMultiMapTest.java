package com.agutsul.chess.activity.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceMoveAction;

@ExtendWith(MockitoExtension.class)
public class ActivityMultiMapTest {

    private static final Action.Type KEY1 = Action.Type.MOVE;
    private static final Action.Type KEY2 = Action.Type.CAPTURE;

    @Mock
    PieceMoveAction<?,?> action1;
    @Mock
    PieceCaptureAction<?,?,?,?> action2;

    ActivityMultiMap<Action.Type,Action<?>> multiMap;

    @BeforeEach
    void setUp() {
        this.multiMap = new ActivityMultiMap<Action.Type,Action<?>>();
    }

    @Test
    void testSize() {
        assertEquals(0, this.multiMap.size());
    }

    @Test
    void testIsEmpty() {
        assertTrue(this.multiMap.isEmpty());
    }

    @Test
    void testContainsKey() {
        this.multiMap.put(KEY1, action1);

        assertTrue(this.multiMap.containsKey(KEY1));
        assertFalse(this.multiMap.containsKey(KEY2));
    }

    @Test
    @SuppressWarnings("unlikely-arg-type")
    void testContainsValue() {
        this.multiMap.put(KEY1, action1);

        assertTrue(this.multiMap.containsValue(action1));
        assertFalse(this.multiMap.containsValue(action2));
    }

    @Test
    void testGet() {
        this.multiMap.put(KEY1, action1);

        var value = this.multiMap.get(KEY1);

        assertNotNull(value);
        assertTrue(value.contains(action1));
        assertFalse(value.contains(action2));
    }

    @Test
    void testRemove() {
        this.multiMap.put(KEY1, action1);
        assertTrue(this.multiMap.containsKey(KEY1));

        this.multiMap.remove(KEY1);
        assertFalse(this.multiMap.containsKey(KEY1));
    }

    @Test
    void testPut() {
        this.multiMap.put(KEY1, action1);
        this.multiMap.put(KEY1, action1);

        var values1 = this.multiMap.get(KEY1);
        assertEquals(2, values1.size());

        this.multiMap.put(KEY2, action2);

        var values2 = this.multiMap.get(KEY2);
        assertEquals(1, values2.size());
    }

    @Test
    void testPut2() {
        Collection<Action<?>> actions = List.of(action1, action2);

        this.multiMap.put(KEY1, actions);

        var values = this.multiMap.get(KEY1);
        assertEquals(actions.size(), values.size());
    }

    @Test
    void testPutAll() {
        this.multiMap.putAll(Map.of(KEY1, List.of(action1)));

        var values = this.multiMap.get(KEY1);
        assertEquals(1, values.size());
    }

    @Test
    void testClear() {
        this.multiMap.put(KEY1, action1);
        assertEquals(1, this.multiMap.size());

        this.multiMap.clear();
        assertEquals(0, this.multiMap.size());
    }

    @Test
    void testKeySet() {
        this.multiMap.put(KEY1, action1);
        this.multiMap.put(KEY2, action2);

        var keys = this.multiMap.keySet();

        assertTrue(keys.containsAll(List.of(KEY1, KEY2)));
    }

    @Test
    void testValues() {
        this.multiMap.put(KEY1, action1);
        this.multiMap.put(KEY2, action2);

        for (var values : this.multiMap.values()) {
            assertTrue(values.contains(action1) || values.contains(action2));
        }
    }

    @Test
    void testEntrySet() {
        this.multiMap.put(KEY1, action1);
        this.multiMap.put(KEY2, action2);

        for (var entry : this.multiMap.entrySet()) {
            assertTrue(Objects.equals(entry.getKey(), KEY1) || Objects.equals(entry.getKey(), KEY2));
            assertTrue(entry.getValue().contains(action1) || entry.getValue().contains(action2));
        }
    }
}