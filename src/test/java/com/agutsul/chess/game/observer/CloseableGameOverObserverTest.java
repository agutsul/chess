package com.agutsul.chess.game.observer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.game.GameContext;
import com.agutsul.chess.game.event.GameOverEvent;

@ExtendWith(MockitoExtension.class)
public class CloseableGameOverObserverTest {

    @Test
    void testObserveClosingForkJoinPool() throws IOException {
        var forkJoinPool = spy(new ForkJoinPool());
        doCallRealMethod()
            .when(forkJoinPool).shutdown();
        doCallRealMethod()
            .when(forkJoinPool).isShutdown();
        doCallRealMethod()
            .when(forkJoinPool).isTerminated();

        var context = spy(new GameContext(forkJoinPool));
        doCallRealMethod()
            .when(context).getForkJoinPool();
        doCallRealMethod()
            .when(context).close();

        var observer = new CloseableGameOverObserver(context);
        observer.observe(mock(GameOverEvent.class));

        assertTrue(forkJoinPool.isShutdown());
        assertTrue(forkJoinPool.isTerminated());

        verify(context, times(1)).close();
        verify(forkJoinPool, times(1)).shutdown();
    }
}