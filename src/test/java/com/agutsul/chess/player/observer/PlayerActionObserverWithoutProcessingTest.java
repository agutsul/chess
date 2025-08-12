package com.agutsul.chess.player.observer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.game.AbstractPlayableGame;

@ExtendWith(MockitoExtension.class)
public class PlayerActionObserverWithoutProcessingTest {

    @Mock
    AbstractPlayableGame game;

    @InjectMocks
    PlayerActionObserver observer;

    @Test
    void testNoProcessingEvent() {
        observer.observe(mock(Event.class));

        verify(game, never()).notifyObservers(any());
    }
}