package com.agutsul.chess.position.cache;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.util.Collections.emptyList;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.position.Position;

@ExtendWith(MockitoExtension.class)
public class PositionCacheImplTest {

    private static final int DEFAULT_VALUE = 0;

    @AutoClose
    ExecutorService executorService = newSingleThreadExecutor();

    @Test
    void testRefresh() {
        var board = mock(Board.class);

        when(board.getExecutorService())
            .thenReturn(executorService);
        when(board.getImpacts(any(Color.class), any(Position.class)))
            .thenReturn(emptyList());

        var positionCache = new PositionCacheImpl<>(board);
        positionCache.refresh();

        verify(board, times(1)).getExecutorService();
        // 2 colors * 64 positions
        verify(board, times(128)).getImpacts(any(Color.class), any(Position.class));
    }

    @Test
    void testGet() {
        var board = new StandardBoard();

        var positionCache = new PositionCacheImpl<>(board);
        positionCache.refresh();

        for (var color : Colors.values()) {
            for (var x = Position.MIN; x < Position.MAX; x++) {
                for (var y = Position.MIN; y < Position.MAX; y++) {
                    var valuablePosition = positionCache.get(color, positionOf(x, y));

                    assertNotNull(valuablePosition);

                    assertEquals(x, valuablePosition.x());
                    assertEquals(y, valuablePosition.y());

                    assertNotNull(valuablePosition.getValue());
                    assertEquals(DEFAULT_VALUE, valuablePosition.getValue());
                }
            }
        }
    }
}