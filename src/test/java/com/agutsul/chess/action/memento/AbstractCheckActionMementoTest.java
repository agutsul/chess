package com.agutsul.chess.action.memento;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AbstractCheckActionMementoTest {

    @Test
    void testGetColor() {
        var memento = mock(ActionMemento.class);

        @SuppressWarnings("unchecked")
        var proxy = new CheckedActionMemento<>(memento);
        proxy.getColor();

        verify(memento, times(1)).getColor();
    }

    @Test
    void testGetActionType() {
        var memento = mock(ActionMemento.class);

        @SuppressWarnings("unchecked")
        var proxy = new CheckedActionMemento<>(memento);
        proxy.getActionType();

        verify(memento, times(1)).getActionType();
    }

    @Test
    void testGetPieceType() {
        var memento = mock(ActionMemento.class);

        @SuppressWarnings("unchecked")
        var proxy = new CheckedActionMemento<>(memento);
        proxy.getPieceType();

        verify(memento, times(1)).getPieceType();
    }

    @Test
    void testGetSource() {
        var memento = mock(ActionMemento.class);

        @SuppressWarnings("unchecked")
        var proxy = new CheckedActionMemento<>(memento);
        proxy.getSource();

        verify(memento, times(1)).getSource();
    }

    @Test
    void testGetTarget() {
        var memento = mock(ActionMemento.class);

        @SuppressWarnings("unchecked")
        var proxy = new CheckMatedActionMemento<>(memento);
        proxy.getTarget();

        verify(memento, times(1)).getTarget();
    }
}