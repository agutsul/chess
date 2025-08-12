package com.agutsul.chess.activity.action.memento;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AbstractCheckActionMementoTest {

    @Mock
    ActionMemento<?,?> memento;

    @InjectMocks
    CheckedActionMemento<?,?> checkedProxy;
    @InjectMocks
    CheckMatedActionMemento<?,?> checkMatedProxy;

    @Test
    void testGetColor() {
        checkedProxy.getColor();
        verify(memento, times(1)).getColor();
    }

    @Test
    void testGetActionType() {
        checkedProxy.getActionType();
        verify(memento, times(1)).getActionType();
    }

    @Test
    void testGetPieceType() {
        checkedProxy.getPieceType();
        verify(memento, times(1)).getPieceType();
    }

    @Test
    void testGetSource() {
        checkedProxy.getSource();
        verify(memento, times(1)).getSource();
    }

    @Test
    void testGetTarget() {
        checkMatedProxy.getTarget();
        verify(memento, times(1)).getTarget();
    }
}