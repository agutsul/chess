package com.agutsul.chess.player.observer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.AbstractBoard;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.player.UserPlayer;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;
import com.agutsul.chess.player.state.ActivePlayerState;
import com.agutsul.chess.player.state.LockedPlayerState;

@ExtendWith(MockitoExtension.class)
public class PlayableObserverTest {

    @Mock
    AbstractBoard board;
    @Mock
    UserPlayer player;

    @InjectMocks
    PlayableObserver playableObserver;

    @BeforeEach
    void setUp() {
        when(player.getColor())
            .thenReturn(Colors.WHITE);
    }

    @Test
    void testObserveActivePlayerEvent() {
        when(player.getState())
            .thenReturn(new ActivePlayerState());

        var event = new RequestPlayerActionEvent(player);
        playableObserver.observe(event);

        verify(player, times(1)).getState();
        verify(player, times(1)).getColor();

        verify(board, times(1)).notifyObservers(eq(event));
    }

    @Test
    void testObserveLockedPlayerEvent() {
        when(player.getState())
            .thenReturn(new LockedPlayerState());

        playableObserver.observe(new RequestPlayerActionEvent(player));

        verify(player, times(1)).getState();
        verify(player, times(1)).getColor();

        verify(board, never()).notifyObservers(any());
    }
}