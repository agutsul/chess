package com.agutsul.chess.game;

import static com.agutsul.chess.board.state.BoardStateFactory.defaultBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.timeoutBoardState;
import static com.agutsul.chess.timeout.TimeoutFactory.createActionTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createGameTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createMixedTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createUnknownTimeout;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.timeout.Timeout;

@ExtendWith(MockitoExtension.class)
public class CompositeGameTest {

    @Mock
    AbstractPlayableGame game;

    @Mock
    Board board;

    @Mock
    GameContext gameContext;

    @Test
    void testDefaultGameWithActionTimeout() {
        when(board.getState())
            .thenReturn(defaultBoardState(board, Colors.WHITE));
        when(game.getBoard())
            .thenReturn(board);
        when(game.getContext())
            .thenReturn(gameContext);

        List<Timeout> timeouts = List.of(createActionTimeout(100L));

        var compositeGame = new CompositeGame<>(game, timeouts.iterator());
        compositeGame.run();

        verify(game, times(1)).run();
    }

    @Test
    void testDefaultGameWithUnknownTimeout() {
        when(board.getState())
            .thenReturn(defaultBoardState(board, Colors.WHITE));
        when(game.getBoard())
            .thenReturn(board);
        when(game.getContext())
            .thenReturn(gameContext);

        List<Timeout> timeouts = List.of(createUnknownTimeout());

        var compositeGame = new CompositeGame<>(game, timeouts.iterator());
        compositeGame.run();

        verify(game, times(1)).run();
    }

    @Test
    void testDefaultGameWithoutAnyTimeout() {
        List<Timeout> timeouts = emptyList();

        var compositeGame = new CompositeGame<>(game, timeouts.iterator());
        compositeGame.run();

        verify(game, times(1)).run();
    }

    @Test
    void testTimeoutGameWithGenericTimeout() {
        when(board.getState())
            .thenReturn(timeoutBoardState(board, Colors.WHITE));
        when(game.getBoard())
            .thenReturn(board);
        when(game.getContext())
            .thenReturn(gameContext);

        List<Timeout> timeouts = List.of(createGameTimeout(100));

        var compositeGame = new CompositeGame<>(game, timeouts.iterator());
        compositeGame.run();

        verify(game, times(1)).run();
    }

    @Test
    void testTimeoutGameWithMixedTimeout() {
        when(board.getState())
            .thenReturn(defaultBoardState(board, Colors.WHITE));
        when(game.getBoard())
            .thenReturn(board);
        when(game.getContext())
            .thenReturn(gameContext);

        List<Timeout> timeouts = List.of(createMixedTimeout(100L, 2));

        var compositeGame = new CompositeGame<>(game, timeouts.iterator());
        compositeGame.run();

        verify(game, times(1)).run();
    }
}