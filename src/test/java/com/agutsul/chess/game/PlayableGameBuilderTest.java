package com.agutsul.chess.game;

import static com.agutsul.chess.board.state.BoardStateFactory.staleMatedBoardState;
import static com.agutsul.chess.timeout.TimeoutFactory.createActionTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createGameTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createIncrementalTimeout;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.ForkJoinPool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.player.UserPlayer;
import com.agutsul.chess.rule.board.BoardStateEvaluator;
import com.agutsul.chess.timeout.CompositeTimeout;

@ExtendWith(MockitoExtension.class)
public class PlayableGameBuilderTest {

    @Mock
    UserPlayer whitePlayer;
    @Mock
    UserPlayer blackPlayer;

    @BeforeEach
    void setUp() {
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);
        when(blackPlayer.getColor())
            .thenReturn(Colors.BLACK);
    }

    @Test
    void testDefaultGameImplBuild() {
        var gameBuilder = new PlayableGameBuilder<>(whitePlayer, blackPlayer);
        var game = gameBuilder.build();

        assertNotNull(game);
        assertTrue(game instanceof GameImpl);

        var board = game.getBoard();

        assertNotNull(board);
        assertTrue(board instanceof StandardBoard);

        var journal = game.getJournal();

        assertNotNull(journal);
        assertTrue(journal instanceof JournalImpl);

        assertNotNull(game.getContext());

        assertEquals(whitePlayer, game.getCurrentPlayer());
        assertEquals(blackPlayer, game.getOpponentPlayer());
    }

    @Test
    void testTimeoutGameBuild() {
        var context = new GameContext(mock(ForkJoinPool.class));
        context.setTimeout(createGameTimeout(20000L));

        var game = new PlayableGameBuilder<>(whitePlayer, blackPlayer)
                .withContext(context)
                .build();

        assertNotNull(game);
        assertTrue(game instanceof TimeoutGame);

        var board = game.getBoard();

        assertNotNull(board);
        assertTrue(board instanceof StandardBoard);

        var journal = game.getJournal();

        assertNotNull(journal);
        assertTrue(journal instanceof JournalImpl);

        assertNotNull(game.getContext());
        assertEquals(context, game.getContext());

        assertEquals(whitePlayer, game.getCurrentPlayer());
        assertEquals(blackPlayer, game.getOpponentPlayer());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGameBuildWithCustomParams() {
        var gameBuilder = new PlayableGameBuilder<>(whitePlayer, blackPlayer);

        var board = new StandardBoard();
        var journal = mock(Journal.class);

        var boardStateEvaluator = mock(BoardStateEvaluator.class);
        when(boardStateEvaluator.evaluate(any()))
            .thenReturn(staleMatedBoardState(board, Colors.BLACK));

        var game = gameBuilder.withBoard(board)
                .withJournal(journal)
                .withBoardStateEvaluator(boardStateEvaluator)
                .withActiveColor(Colors.BLACK)
                .build();

        assertNotNull(game);
        assertTrue(game instanceof GameImpl);

        assertNotNull(game.getBoard());
        assertEquals(board, game.getBoard());

        assertNotNull(game.getJournal());
        assertEquals(journal, game.getJournal());

        assertNotNull(game.getContext());

        assertEquals(blackPlayer, game.getCurrentPlayer());
        assertEquals(whitePlayer, game.getOpponentPlayer());
    }

    @Test
    void testGameBuildWithCompositeTimeout() {
        var context = new GameContext();
        context.setTimeout(new CompositeTimeout(
                createGameTimeout(100L),
                createActionTimeout(100L),
                createIncrementalTimeout(createGameTimeout(100L), 60L)
        ));

        var gameBuilder = new PlayableGameBuilder<>(whitePlayer, blackPlayer);
        gameBuilder.withContext(context);

        var game = gameBuilder.build();

        assertNotNull(game);
        assertTrue(game instanceof CompositeGame<?>);

        assertEquals(whitePlayer, game.getCurrentPlayer());
        assertEquals(blackPlayer, game.getOpponentPlayer());
    }
}