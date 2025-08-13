package com.agutsul.chess.game.ai;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.ai.SelectionStrategy;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.GameContext;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.player.Player;

@ExtendWith(MockitoExtension.class)
public class SimulationActionInputObserverTest {

    @Mock
    GameContext context;
    @Mock
    Game game;
    @Mock
    Player player;
    @Mock
    Observable observable;
    @Mock
    SelectionStrategy<Action<?>> selectionStrategy;

    @Test
    void testGetActionCommandReturnDefeat() {
        when(selectionStrategy.select(any()))
            .thenReturn(Optional.empty());

        var botObserver = new SimulationActionInputObserver(player, game, selectionStrategy);
        assertEquals("defeat", botObserver.getActionCommand(Optional.empty()));
    }

    @Test
    void testGetActionCommandReturnAction() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e4")
                .build();

        var piece = (PawnPiece<Color>) board.getPiece("e4").get();

        when(selectionStrategy.select(any()))
            .thenReturn(Optional.of(new PieceMoveAction<>(piece, positionOf("e5"))));

        var botObserver = new SimulationActionInputObserver(player, game, selectionStrategy);
        assertEquals("e4 e5", botObserver.getActionCommand(Optional.empty()));
    }

    @Test
    void testPromoteActionReturn() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e7")
                .build();

        var piece = (PawnPiece<Color>) board.getPiece("e7").get();
        var promoteAction = new PiecePromoteAction<>(
                new PieceMoveAction<>(piece, positionOf("e8")),
                observable
        );

        when(selectionStrategy.select(any()))
            .thenReturn(Optional.of(promoteAction));

        var botObserver = new SimulationActionInputObserver(player, game, selectionStrategy);

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