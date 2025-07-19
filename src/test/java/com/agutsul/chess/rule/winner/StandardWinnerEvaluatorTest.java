package com.agutsul.chess.rule.winner;

import static com.agutsul.chess.board.state.BoardStateFactory.agreedDefeatBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.agreedDrawBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.agreedWinBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.checkMatedBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.fiveFoldRepetitionBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.seventyFiveMovesBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.staleMatedBoardState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;

@ExtendWith(MockitoExtension.class)
public class StandardWinnerEvaluatorTest {

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
    void testAgreedDefeatGameState() {
        when(game.getOpponentPlayer())
            .thenReturn(blackPlayer);
        when(board.getState())
            .thenReturn(agreedDefeatBoardState(board, Colors.WHITE));

        var evaluator = new StandardWinnerEvaluator(mock(WinnerEvaluator.class));
        var winner = evaluator.evaluate(game);

        assertEquals(blackPlayer, winner);
    }

    @Test
    void testAgreedWinGameState() {
        when(game.getCurrentPlayer())
            .thenReturn(blackPlayer);
        when(board.getState())
            .thenReturn(agreedWinBoardState(board, Colors.BLACK));

        var evaluator = new StandardWinnerEvaluator(mock(WinnerEvaluator.class));
        var winner = evaluator.evaluate(game);

        assertEquals(blackPlayer, winner);
    }

    @Test
    void testCheckMatedGameState() {
        when(game.getCurrentPlayer())
            .thenReturn(whitePlayer);
        when(board.getState())
            .thenReturn(checkMatedBoardState(board, Colors.WHITE));

        var evaluator = new StandardWinnerEvaluator(mock(WinnerEvaluator.class));
        var winner = evaluator.evaluate(game);

        assertEquals(whitePlayer, winner);
    }

    @Test
    void testAgreedDrawGameState() {
        when(board.getState())
            .thenReturn(agreedDrawBoardState(board, Colors.BLACK));

        var evaluator = new StandardWinnerEvaluator(mock(WinnerEvaluator.class));
        assertNull(evaluator.evaluate(game));
    }

    @Test
    void testFiveFoldRepetitionGameState() {
        var boardState = fiveFoldRepetitionBoardState(board, mock(ActionMemento.class));
        when(board.getState())
            .thenReturn(boardState);

        var evaluator = new StandardWinnerEvaluator(mock(WinnerEvaluator.class));
        assertNull(evaluator.evaluate(game));
    }

    @Test
    void testSeventyFiveMovesGameState() {
        when(board.getState())
            .thenReturn(seventyFiveMovesBoardState(board, Colors.WHITE));

        var evaluator = new StandardWinnerEvaluator(mock(WinnerEvaluator.class));
        assertNull(evaluator.evaluate(game));
    }

    @Test
    void testStaleMatedGameState() {
        when(board.getState())
            .thenReturn(staleMatedBoardState(board, Colors.WHITE));

        var evaluator = new StandardWinnerEvaluator(mock(WinnerEvaluator.class));
        assertNull(evaluator.evaluate(game));
    }
}