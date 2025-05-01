package com.agutsul.chess.ai;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CompositeResultMatcherTest {

    @Test
    @SuppressWarnings("unchecked")
    void testFirstMatcherMatch() {
        var matcher1 = mock(ResultMatcher.class);
        when(matcher1.match(any()))
            .thenReturn(true);

        var matcher2 = mock(ResultMatcher.class);

        var matcher = new CompositeResultMatcher<>(matcher1, matcher2);
        var result = matcher.match(mock(TaskResult.class));
        assertTrue(result);

        verify(matcher1, times(1)).match(any());
        verify(matcher2, never()).match(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSecondMatcherMatch() {
        var matcher1 = mock(ResultMatcher.class);
        when(matcher1.match(any()))
            .thenReturn(false);

        var matcher2 = mock(ResultMatcher.class);
        when(matcher2.match(any()))
            .thenReturn(true);

        var matcher = new CompositeResultMatcher<>(matcher1, matcher2);
        var result = matcher.match(mock(TaskResult.class));
        assertTrue(result);

        verify(matcher1, times(1)).match(any());
        verify(matcher2, times(1)).match(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testNoMatcherMatch() {
        var matcher1 = mock(ResultMatcher.class);
        when(matcher1.match(any()))
            .thenReturn(false);

        var matcher2 = mock(ResultMatcher.class);
        when(matcher2.match(any()))
            .thenReturn(false);

        var matcher = new CompositeResultMatcher<>(matcher1, matcher2);
        var result = matcher.match(mock(TaskResult.class));
        assertFalse(result);

        verify(matcher1, times(1)).match(any());
        verify(matcher2, times(1)).match(any());
    }
}