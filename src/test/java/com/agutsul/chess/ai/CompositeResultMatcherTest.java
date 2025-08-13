package com.agutsul.chess.ai;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;

@ExtendWith(MockitoExtension.class)
public class CompositeResultMatcherTest {

    @Mock
    TaskResult<Action<?>,Integer> taskResult;
    @Mock
    ResultMatcher<Action<?>,Integer,TaskResult<Action<?>,Integer>> matcher1;
    @Mock
    ResultMatcher<Action<?>,Integer,TaskResult<Action<?>,Integer>> matcher2;

    CompositeResultMatcher<Action<?>,Integer,TaskResult<Action<?>,Integer>> matcher;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        matcher = new CompositeResultMatcher<>(matcher1, matcher2);
    }

    @Test
    void testFirstMatcherMatch() {
        when(matcher1.match(any()))
            .thenReturn(true);

        assertTrue(matcher.match(taskResult));

        verify(matcher1, times(1)).match(any());
        verify(matcher2, never()).match(any());
    }

    @Test
    void testSecondMatcherMatch() {
        when(matcher1.match(any()))
            .thenReturn(false);

        when(matcher2.match(any()))
            .thenReturn(true);

        assertTrue(matcher.match(taskResult));

        verify(matcher1, times(1)).match(any());
        verify(matcher2, times(1)).match(any());
    }

    @Test
    void testNoMatcherMatch() {
        when(matcher1.match(any()))
            .thenReturn(false);

        when(matcher2.match(any()))
            .thenReturn(false);

        assertFalse(matcher.match(taskResult));

        verify(matcher1, times(1)).match(any());
        verify(matcher2, times(1)).match(any());
    }
}