package com.agutsul.chess.rule.winner;

import static com.agutsul.chess.board.state.BoardStateFactory.defaultBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.insufficientMaterialBoardState;
import static com.agutsul.chess.board.state.BoardStateFactory.timeoutBoardState;
import static com.agutsul.chess.board.state.InsufficientMaterialBoardState.Pattern.KING_VS_KING;
import static com.agutsul.chess.board.state.InsufficientMaterialBoardState.Pattern.SINGLE_KING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;

@ExtendWith(MockitoExtension.class)
public class ActionTimeoutWinnerEvaluatorTest {

    @Mock
    WinnerEvaluator winnerEvaluator;
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
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);

        when(blackPlayer.getColor())
            .thenReturn(Colors.BLACK);

        when(game.getBoard())
            .thenReturn(board);

        when(game.getPlayer(any(Color.class)))
            .thenAnswer(inv -> Colors.WHITE.equals(inv.getArgument(0))
                    ? whitePlayer
                    : blackPlayer
            );
    }

    @Test
    void testTimeoutGameStateWithoutOpponentState() {
        when(board.getState())
            .thenReturn(timeoutBoardState(board, Colors.WHITE));
        when(board.getState(any(Color.class)))
            .thenAnswer(inv -> {
                assertEquals(Colors.BLACK, inv.getArgument(0, Color.class));
                return null;
            });

        var evaluator = new ActionTimeoutWinnerEvaluator(winnerEvaluator, whitePlayer);
        assertNull(evaluator.evaluate(game));
    }

    @Test
    void testTimeoutGameStateWithDefaultOpponentState() {
        when(board.getState())
            .thenReturn(timeoutBoardState(board, Colors.WHITE));
        when(board.getState(any(Color.class)))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);

                assertEquals(Colors.BLACK, color);

                return defaultBoardState(board, color);
            });

        var evaluator = new ActionTimeoutWinnerEvaluator(winnerEvaluator, whitePlayer);
        assertNull(evaluator.evaluate(game));
    }

    @Test
    void testTimeoutGameStateWithUnsupportedInsufficientMaterialOpponentState() {
        when(board.getState())
            .thenReturn(timeoutBoardState(board, Colors.WHITE));
        when(board.getState(any(Color.class)))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);

                assertEquals(Colors.BLACK, color);

                return insufficientMaterialBoardState(board, color, KING_VS_KING);
            });

        var evaluator = new ActionTimeoutWinnerEvaluator(winnerEvaluator, whitePlayer);
        assertNull(evaluator.evaluate(game));
    }

    @Test
    void testTimeoutGameStateWithInsufficientMaterialOpponentState() {
        when(board.getState())
            .thenReturn(timeoutBoardState(board, Colors.WHITE));
        when(board.getState(any(Color.class)))
            .thenAnswer(inv -> {
                var color = inv.getArgument(0, Color.class);

                assertEquals(Colors.BLACK, color);

                return insufficientMaterialBoardState(board, color, SINGLE_KING);
            });

        var evaluator = new ActionTimeoutWinnerEvaluator(winnerEvaluator, whitePlayer);
        assertNull(evaluator.evaluate(game));
    }
}