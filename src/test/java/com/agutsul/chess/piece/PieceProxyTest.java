package com.agutsul.chess.piece;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.position.Position;

@ExtendWith(MockitoExtension.class)
public class PieceProxyTest {

    @Mock
    private Piece<Color> piece;

    @InjectMocks
    private PieceProxy proxy;

    @Test
    void testGetType() {
        var pieceType = Piece.Type.KING;
        when(piece.getType()).thenReturn(pieceType);

        assertEquals(proxy.getType(), pieceType);
        verify(piece, times(1)).getType();
    }

    @Test
    void testGetColor() {
        var color = Colors.WHITE;
        when(piece.getColor()).thenReturn(color);

        assertEquals(proxy.getColor(), color);
        verify(piece, times(1)).getColor();
    }

    @Test
    void testGetUnicode() {
         var unicode = EMPTY;
         when(piece.getUnicode()).thenReturn(unicode);

         assertEquals(proxy.getUnicode(), unicode);
         verify(piece, times(1)).getUnicode();
    }

    @Test
    void testGetPosition() {
        var position = mock(Position.class);
        when(piece.getPosition()).thenReturn(position);

        assertEquals(proxy.getPosition(), position);
        verify(piece, times(1)).getPosition();
    }

    @Test
    void testIsActive() {
        var active = false;
        when(piece.isActive()).thenReturn(active);

        assertEquals(proxy.isActive(), active);
        verify(piece, times(1)).isActive();
    }

    @Test
    void testGetActions() {
        Collection<Action<?>> actions = emptyList();
        when(piece.getActions()).thenReturn(actions);

        assertEquals(proxy.getActions(), actions);
        verify(piece, times(1)).getActions();
    }
}
