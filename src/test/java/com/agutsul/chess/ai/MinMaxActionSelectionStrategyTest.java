package com.agutsul.chess.ai;

import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.AbstractBoard;
import com.agutsul.chess.board.StringBoardBuilder;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.mock.GameMock;
import com.agutsul.chess.player.UserPlayer;

@ExtendWith(MockitoExtension.class)
public class MinMaxActionSelectionStrategyTest {

    @Test
    void testActionSelection() {
        var board = new StringBoardBuilder()
                .withWhiteKing("e1")
                .withWhitePawn("e4")
                .withBlackKing("e8")
                .withBlackPawn("f7")
                .build();

        var whitePlayer = new UserPlayer(randomUUID().toString(), Colors.WHITE);
        var blackPlayer = new UserPlayer(randomUUID().toString(), Colors.BLACK);

        var game = new GameMock(whitePlayer, blackPlayer, board);
        var strategy = new MinMaxActionSelectionStrategy(game, 3);

        var action = strategy.select(Colors.WHITE);
        assertTrue(action.isPresent());
    }

    @Test
    void testNoActionFound() {
        var whitePlayer = new UserPlayer(randomUUID().toString(), Colors.WHITE);
        var blackPlayer = new UserPlayer(randomUUID().toString(), Colors.BLACK);

        var board = mock(AbstractBoard.class);
        when(board.getPieces(any(Color.class)))
            .thenReturn(emptyList());

        var game = new GameMock(whitePlayer, blackPlayer, board);
        var strategy = new MinMaxActionSelectionStrategy(game, 3);

        var action = strategy.select(Colors.WHITE);
        assertTrue(action.isEmpty());
    }

/*
 * TODO confirm following by tests
 * - action sorting => multiple tests for all cases
 * - scholar mate => simulate for both sides:
 *      - confirm that 'black' unable to find any action
 *      - confirm that 'white' able to find checkmate action
*/
//    @Test
//    void testActionSorting() {
//
//    }

}