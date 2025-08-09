package com.agutsul.chess.rule.winner;

import static com.agutsul.chess.board.state.BoardStateFactory.exitedBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.timeoutBoardState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;

@ExtendWith(MockitoExtension.class)
public class GameTimeoutWinnerEvaluatorTest {

    @Mock
    Game game;
    @Mock
    Board board;
    @Mock
    Player whitePlayer;
    @Mock
    Player blackPlayer;

    @BeforeEach
    void setUp() {
        when(game.getBoard())
            .thenReturn(board);
    }

    @Test
    void testTimeoutGameStateWithOpponentWinner() {
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);

        when(game.getPlayer(eq(Colors.BLACK)))
            .thenReturn(blackPlayer);

        when(game.getCurrentPlayer())
            .thenReturn(whitePlayer);

        when(board.getState())
            .thenReturn(timeoutBoardState(board, Colors.WHITE));

        var evaluator = new GameTimeoutWinnerEvaluator(mock(WinnerEvaluator.class), whitePlayer);
        var winner = evaluator.evaluate(game);

        assertNotNull(winner);
        assertEquals(blackPlayer, winner);
    }

    @Test
    void testTimeoutGameStateWithScoreWinnerInvocation() {
        when(game.getCurrentPlayer())
            .thenReturn(whitePlayer);

        when(board.getState())
            .thenReturn(exitedBoardState(board, Colors.WHITE));

        var scoreEvaluator = mock(WinnerEvaluator.class);
        var evaluator = new GameTimeoutWinnerEvaluator(scoreEvaluator, whitePlayer);

        assertNull(evaluator.evaluate(game));
        verify(scoreEvaluator, times(1)).evaluate(any());
    }
}