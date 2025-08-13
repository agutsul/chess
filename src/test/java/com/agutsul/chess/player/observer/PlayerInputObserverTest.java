package com.agutsul.chess.player.observer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.board.AbstractBoard;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.GameContext;
import com.agutsul.chess.mock.PlayerInputObserverMock;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.event.PlayerActionEvent;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionEvent;
import com.agutsul.chess.player.event.PlayerTerminateActionEvent;
import com.agutsul.chess.player.event.PromotionPieceTypeEvent;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;
import com.agutsul.chess.player.event.RequestPromotionPieceTypeEvent;
import com.agutsul.chess.position.Position;

@ExtendWith(MockitoExtension.class)
public class PlayerInputObserverTest {

    @AutoClose
    GameContext context = new GameContext();

    @Mock
    Position position;
    @Mock
    Player player;
    @Mock
    AbstractBoard board;
    @Mock
    AbstractPlayableGame game;
    @Mock
    Observable observable;
    @Mock
    PawnPiece<Color> pawnPiece;
    @Mock
    PieceMoveAction<Color,PawnPiece<Color>> moveAction;

    @Test
    void testObserveNonRequestEvent() {
        var observer = new PlayerInputObserverMock(player, game);
        observer.observe(mock(Event.class));

        verify(game, never()).notifyObservers(any());
    }

    @Test
    void testObserveOpponentPlayerEvent() {
        var whitePlayer = mock(Player.class);
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);

        var blackPlayer = mock(Player.class);
        when(blackPlayer.getColor())
            .thenReturn(Colors.BLACK);

        var observer = new PlayerInputObserverMock(whitePlayer, game);
        observer.observe(new RequestPlayerActionEvent(blackPlayer));

        verify(game, never()).notifyObservers(any());
    }

    @Test
    void testObservePlayerActionEvent() {
        when(game.getContext())
            .thenReturn(context);

        when(player.getColor())
            .thenReturn(Colors.WHITE);

        var observer = new PlayerInputObserverMock(player, game, "e2 e4");
        observer.observe(new RequestPlayerActionEvent(player));

        verify(game, times(1)).notifyObservers(any(PlayerActionEvent.class));
    }

    @Test
    void testObservePlayerActionEventInvalidAction() {
        when(game.getBoard())
            .thenReturn(board);
        when(game.getContext())
            .thenReturn(context);

        doAnswer(inv -> {
            var event = inv.getArgument(0, PlayerActionExceptionEvent.class);
            assertEquals("Invalid action format: 'e2 '", event.getMessage());

            return null;
        }).when(game).notifyObservers(any(PlayerActionExceptionEvent.class));

        when(player.getColor())
            .thenReturn(Colors.WHITE);

        var observer = new PlayerInputObserverMock(player, game, "e2 ");
        observer.observe(new RequestPlayerActionEvent(player));

        verify(game, atLeastOnce()).notifyObservers(any(PlayerActionExceptionEvent.class));
    }

    @Test
    void testObservePlayerActionEventUnknownAction() {
        when(game.getBoard())
            .thenReturn(board);
        when(game.getContext())
            .thenReturn(context);

        doAnswer(inv -> {
            var event = inv.getArgument(0, PlayerActionExceptionEvent.class);
            assertEquals("Unable to process: 'e2'", event.getMessage());

            return null;
        }).when(game).notifyObservers(any(PlayerActionExceptionEvent.class));

        when(player.getColor())
            .thenReturn(Colors.WHITE);

        var observer = new PlayerInputObserverMock(player, game, "e2");
        observer.observe(new RequestPlayerActionEvent(player));

        verify(game, atLeastOnce()).notifyObservers(any(PlayerActionExceptionEvent.class));
    }

    @Test
    void testObservePlayerActionEventUndoAction() {
        when(game.getContext())
            .thenReturn(context);

        when(player.getColor())
            .thenReturn(Colors.WHITE);

        var observer = new PlayerInputObserverMock(player, game, "undo");
        observer.observe(new RequestPlayerActionEvent(player));

        verify(game, times(1)).notifyObservers(any(PlayerCancelActionEvent.class));
    }

    @Test
    void testObservePlayerActionEventDrawAction() {
        when(game.getContext())
            .thenReturn(context);

        when(player.getColor())
            .thenReturn(Colors.WHITE);

        var observer = new PlayerInputObserverMock(player, game, "draw");
        observer.observe(new RequestPlayerActionEvent(player));

        verify(game, times(1)).notifyObservers(any(PlayerTerminateActionEvent.class));
    }

    @Test
    void testObservePlayerPromotionPieceTypeEvent() {
        doAnswer(inv -> {
            var promotionRequestEvent = inv.getArgument(0, RequestPromotionPieceTypeEvent.class);
            var observer = promotionRequestEvent.getObserver();

            observer.observe(new PromotionPieceTypeEvent(player, Piece.Type.QUEEN));

            return null;
        }).when(observable).notifyObservers(any());

        when(pawnPiece.getColor())
            .thenReturn(Colors.WHITE);

        when(moveAction.getSource())
            .thenReturn(pawnPiece);
        when(moveAction.getPosition())
            .thenReturn(position);

        var action = spy(new PiecePromoteAction<>(moveAction, observable));
        action.execute();

        verify(action, times(2)).getPiece();
        verify(pawnPiece,  times(1)).promote(any(), eq(Piece.Type.QUEEN));
    }

    @Test
    void testObservePlayerPromotionPieceTypeEventUnknowPromotionType() {
        when(game.getContext())
            .thenReturn(context);

        when(player.getColor())
            .thenReturn(Colors.WHITE);

        var observer = new PlayerInputObserverMock(player, game, null, "Z");
        var event = new RequestPromotionPieceTypeEvent(
                Colors.WHITE,
                observer
        );

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> observer.observe(event)
        );

        assertEquals("Unknown promotion piece type: 'Z'", thrown.getMessage());
    }
}