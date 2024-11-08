package com.agutsul.chess.player.observer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.action.PiecePromoteAction;
import com.agutsul.chess.board.AbstractBoard;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.mock.PlayerInputObserverMock;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.event.PlayerActionEvent;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionEvent;
import com.agutsul.chess.player.event.PlayerDrawActionEvent;
import com.agutsul.chess.player.event.PromotionPieceTypeEvent;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;
import com.agutsul.chess.player.event.RequestPromotionPieceTypeEvent;

@ExtendWith(MockitoExtension.class)
public class PlayerInputObserverTest {

    @Test
    void testObserveNonRequestEvent() {
        var game = mock(AbstractPlayableGame.class);

        var observer = new PlayerInputObserverMock(mock(Player.class), game);
        observer.observe(mock(Event.class));

        verify(game, never()).notifyObservers(any());
    }

    @Test
    void testObserveOpponentPlayerEvent() {
        var game = mock(AbstractPlayableGame.class);

        var whitePlayer = mock(Player.class);
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);

        var blackPlayer = mock(Player.class);
        when(blackPlayer.getColor())
            .thenReturn(Colors.BLACK);

        var event = new RequestPlayerActionEvent(blackPlayer);

        var observer = new PlayerInputObserverMock(whitePlayer, game);
        observer.observe(event);

        verify(game, never()).notifyObservers(any());
    }

    @Test
    void testObservePlayerActionEvent() {
        var game = mock(AbstractPlayableGame.class);

        var whitePlayer = mock(Player.class);
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);

        var event = new RequestPlayerActionEvent(whitePlayer);

        var observer = new PlayerInputObserverMock(whitePlayer, game, "e2 e4");
        observer.observe(event);

        verify(game, times(1)).notifyObservers(any(PlayerActionEvent.class));
    }

    @Test
    void testObservePlayerActionEventInvalidAction() {
        var game = mock(AbstractPlayableGame.class);
        when(game.getBoard())
            .thenReturn(mock(AbstractBoard.class));

        doAnswer(inv -> {
            var event = inv.getArgument(0, PlayerActionExceptionEvent.class);
            assertEquals("Invalid action format: 'e2 '", event.getMessage());

            return null;
        }).when(game).notifyObservers(any(PlayerActionExceptionEvent.class));

        var whitePlayer = mock(Player.class);
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);

        var observer = new PlayerInputObserverMock(whitePlayer, game, "e2 ");
        observer.observe(new RequestPlayerActionEvent(whitePlayer));

        verify(game, atLeastOnce()).notifyObservers(any(PlayerActionExceptionEvent.class));
    }

    @Test
    void testObservePlayerActionEventUnknownAction() {
        var game = mock(AbstractPlayableGame.class);
        when(game.getBoard())
            .thenReturn(mock(AbstractBoard.class));

        doAnswer(inv -> {
            var event = inv.getArgument(0, PlayerActionExceptionEvent.class);
            assertEquals("Unable to process: 'e2'", event.getMessage());

            return null;
        }).when(game).notifyObservers(any(PlayerActionExceptionEvent.class));

        var whitePlayer = mock(Player.class);
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);

        var observer = new PlayerInputObserverMock(whitePlayer, game, "e2");
        observer.observe(new RequestPlayerActionEvent(whitePlayer));

        verify(game, atLeastOnce()).notifyObservers(any(PlayerActionExceptionEvent.class));
    }

    @Test
    void testObservePlayerActionEventUndoAction() {
        var game = mock(AbstractPlayableGame.class);

        var whitePlayer = mock(Player.class);
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);

        var event = new RequestPlayerActionEvent(whitePlayer);

        var observer = new PlayerInputObserverMock(whitePlayer, game, "undo");
        observer.observe(event);

        verify(game, times(1)).notifyObservers(any(PlayerCancelActionEvent.class));
    }

    @Test
    void testObservePlayerActionEventDrawAction() {
        var game = mock(AbstractPlayableGame.class);

        var whitePlayer = mock(Player.class);
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);

        var event = new RequestPlayerActionEvent(whitePlayer);

        var observer = new PlayerInputObserverMock(whitePlayer, game, "draw");
        observer.observe(event);

        verify(game, times(1)).notifyObservers(any(PlayerDrawActionEvent.class));
    }

    @Test
    void testObservePlaverPromotionPieceTypeEvent() {
        var game = mock(AbstractPlayableGame.class);

        var whitePlayer = mock(Player.class);
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);

        var action = mock(PiecePromoteAction.class);

        var event = new RequestPromotionPieceTypeEvent(Colors.WHITE, action);

        var observer = new PlayerInputObserverMock(whitePlayer, game, null, "Q");
        observer.observe(event);

        verify(action, times(1)).observe(any(PromotionPieceTypeEvent.class));
    }

    @Test
    void testObservePlaverPromotionPieceTypeEventUnknowPromotionType() {
        var game = mock(AbstractPlayableGame.class);

        var whitePlayer = mock(Player.class);
        when(whitePlayer.getColor())
            .thenReturn(Colors.WHITE);

        var event = new RequestPromotionPieceTypeEvent(
                Colors.WHITE,
                mock(PiecePromoteAction.class)
        );

        var observer = new PlayerInputObserverMock(whitePlayer, game, null, "Z");
        var thrown = assertThrows(
                IllegalActionException.class,
                () -> observer.observe(event)
        );

        assertEquals("Unknown promotion piece type: 'Z'", thrown.getMessage());
    }
}