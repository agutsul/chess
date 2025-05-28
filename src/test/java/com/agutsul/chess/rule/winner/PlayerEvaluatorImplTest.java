package com.agutsul.chess.rule.winner;

import static com.agutsul.chess.board.state.BoardStateFactory.agreedDefeatBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.agreedDrawBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.agreedWinBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.checkMatedBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.defaultBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.fiveFoldRepetitionBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.insufficientMaterialBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.seventyFiveMovesBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.staleMatedBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.timeoutBoardState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.InsufficientMaterialBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;

@ExtendWith(MockitoExtension.class)
public class PlayerEvaluatorImplTest {

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
    void testTimeoutGameStateWithoutOpponentState() {
        when(blackPlayer.getColor())
            .thenReturn(Colors.BLACK);

        when(game.getOpponentPlayer())
            .thenReturn(blackPlayer);

        when(board.getState())
            .thenReturn(timeoutBoardState(board, Colors.WHITE));
        when(board.getState(any(Color.class)))
            .thenAnswer(inv -> {
                assertEquals(Colors.BLACK, inv.getArgument(0));
                return null;
            });

        var evaluator = new PlayerEvaluatorImpl(mock(PlayerEvaluator.class));
        assertNull(evaluator.evaluate(game));
    }

    @Test
    void testTimeoutGameStateWithDefaultOpponentState() {
        when(blackPlayer.getColor())
            .thenReturn(Colors.BLACK);

        when(game.getOpponentPlayer())
            .thenReturn(blackPlayer);

        when(board.getState())
            .thenReturn(timeoutBoardState(board, Colors.WHITE));
        when(board.getState(any(Color.class)))
            .thenAnswer(inv -> {
                assertEquals(Colors.BLACK, inv.getArgument(0));
                return defaultBoardState(board, inv.getArgument(0));
            });

        var evaluator = new PlayerEvaluatorImpl(mock(PlayerEvaluator.class));
        var winner = evaluator.evaluate(game);

        assertEquals(blackPlayer, winner);
    }

    @Test
    void testTimeoutGameStateWithUnsupportedInsufficientMaterialOpponentState() {
        when(blackPlayer.getColor())
            .thenReturn(Colors.BLACK);

        when(game.getOpponentPlayer())
            .thenReturn(blackPlayer);

        when(board.getState())
            .thenReturn(timeoutBoardState(board, Colors.WHITE));
        when(board.getState(any(Color.class)))
            .thenAnswer(inv -> {
                assertEquals(Colors.BLACK, inv.getArgument(0));
                return insufficientMaterialBoardState(board,
                        inv.getArgument(0),
                        InsufficientMaterialBoardState.Pattern.KING_VS_KING
                );
            });

        var evaluator = new PlayerEvaluatorImpl(mock(PlayerEvaluator.class));
        var winner = evaluator.evaluate(game);

        assertEquals(blackPlayer, winner);
    }

    @Test
    void testTimeoutGameStateWithInsufficientMaterialOpponentState() {
        when(blackPlayer.getColor())
            .thenReturn(Colors.BLACK);

        when(game.getOpponentPlayer())
            .thenReturn(blackPlayer);

        when(board.getState())
            .thenReturn(timeoutBoardState(board, Colors.WHITE));
        when(board.getState(any(Color.class)))
            .thenAnswer(inv -> {
                assertEquals(Colors.BLACK, inv.getArgument(0));
                return insufficientMaterialBoardState(board,
                        inv.getArgument(0),
                        InsufficientMaterialBoardState.Pattern.SINGLE_KING
                );
            });

        var evaluator = new PlayerEvaluatorImpl(mock(PlayerEvaluator.class));
        assertNull(evaluator.evaluate(game));
    }

    @Test
    void testAgreedDefeatGameState() {
        when(game.getOpponentPlayer())
            .thenReturn(blackPlayer);
        when(board.getState())
            .thenReturn(agreedDefeatBoardState(board, Colors.WHITE));

        var evaluator = new PlayerEvaluatorImpl(mock(PlayerEvaluator.class));
        var winner = evaluator.evaluate(game);

        assertEquals(blackPlayer, winner);
    }

    @Test
    void testAgreedWinGameState() {
        when(game.getCurrentPlayer())
            .thenReturn(blackPlayer);
        when(board.getState())
            .thenReturn(agreedWinBoardState(board, Colors.BLACK));

        var evaluator = new PlayerEvaluatorImpl(mock(PlayerEvaluator.class));
        var winner = evaluator.evaluate(game);

        assertEquals(blackPlayer, winner);
    }

    @Test
    void testCheckMatedGameState() {
        when(game.getCurrentPlayer())
            .thenReturn(whitePlayer);
        when(board.getState())
            .thenReturn(checkMatedBoardState(board, Colors.WHITE));

        var evaluator = new PlayerEvaluatorImpl(mock(PlayerEvaluator.class));
        var winner = evaluator.evaluate(game);

        assertEquals(whitePlayer, winner);
    }

    @Test
    void testAgreedDrawGameState() {
        when(board.getState())
            .thenReturn(agreedDrawBoardState(board, Colors.BLACK));

        var evaluator = new PlayerEvaluatorImpl(mock(PlayerEvaluator.class));
        assertNull(evaluator.evaluate(game));
    }

    @Test
    void testFiveFoldRepetitionGameState() {
        var boardState = fiveFoldRepetitionBoardState(board, mock(ActionMemento.class));
        when(board.getState())
            .thenReturn(boardState);

        var evaluator = new PlayerEvaluatorImpl(mock(PlayerEvaluator.class));
        assertNull(evaluator.evaluate(game));
    }

    @Test
    void testSeventyFiveMovesGameState() {
        when(board.getState())
            .thenReturn(seventyFiveMovesBoardState(board, Colors.WHITE));

        var evaluator = new PlayerEvaluatorImpl(mock(PlayerEvaluator.class));
        assertNull(evaluator.evaluate(game));
    }

    @Test
    void testStaleMatedGameState() {
        when(board.getState())
            .thenReturn(staleMatedBoardState(board, Colors.WHITE));

        var evaluator = new PlayerEvaluatorImpl(mock(PlayerEvaluator.class));
        assertNull(evaluator.evaluate(game));
    }
}