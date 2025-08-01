package com.agutsul.chess.game.ai;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.ai.SelectionStrategy;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.GameContext;
import com.agutsul.chess.player.Player;

@ExtendWith(MockitoExtension.class)
public class SimulationActionInputObserverTest {

    @Mock
    GameContext context;

    @Mock
    Game game;

    @Mock
    Player player;

    @Test
    void testGetActionCommandReturnDefeat() {
        var actionStrategy = mock(SelectionStrategy.class);
        when(actionStrategy.select(any()))
            .thenReturn(Optional.empty());

        @SuppressWarnings("unchecked")
        var botObserver = new SimulationActionInputObserver(player, game, actionStrategy);
        assertEquals("defeat", botObserver.getActionCommand(Optional.empty()));
    }

    @Test
    void testGetActionCommandReturnAction() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e4")
                .build();

        var piece = board.getPiece("e4");
        @SuppressWarnings({ "unchecked", "rawtypes" })
        var action = new PieceMoveAction(piece.get(), positionOf("e5"));

        var actionStrategy = mock(SelectionStrategy.class);
        when(actionStrategy.select(any()))
            .thenReturn(Optional.of(action));

        @SuppressWarnings("unchecked")
        var botObserver = new SimulationActionInputObserver(player, game, actionStrategy);
        assertEquals("e4 e5", botObserver.getActionCommand(Optional.empty()));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testPromoteActionReturn() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e7")
                .build();

        var piece = board.getPiece("e7");

        var moveAction = new PieceMoveAction(piece.get(), positionOf("e8"));
        var promoteAction = new PiecePromoteAction(moveAction, mock(Observable.class));

        var actionStrategy = mock(SelectionStrategy.class);
        when(actionStrategy.select(any()))
            .thenReturn(Optional.of(promoteAction));

        var botObserver = new SimulationActionInputObserver(player, game, actionStrategy);

        assertEquals("e7 e8", botObserver.getActionCommand(Optional.empty()));
        assertEquals("null",  botObserver.getPromotionPieceType(Optional.empty()));
    }

    @Test
    void testGetPromotionPieceTypeThrowingException() {
        when(game.getContext())
            .thenReturn(context);

        var botObserver = new SimulationActionInputObserver(player, game);
        var thrown = assertThrows(
                IllegalStateException.class,
                () -> botObserver.getPromotionPieceType(Optional.empty())
        );

        assertEquals("Unknown promotion action", thrown.getMessage());
    }
}