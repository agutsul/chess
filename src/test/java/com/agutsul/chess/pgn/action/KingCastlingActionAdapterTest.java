package com.agutsul.chess.pgn.action;

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

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.pgn.action.KingCastlingActionAdapter.CastlingSide;
import com.agutsul.chess.piece.KingPiece;

@ExtendWith(MockitoExtension.class)
public class KingCastlingActionAdapterTest {

    @Mock
    Board board;

    @Mock
    KingPiece<Color> king;

    @Test
    void testAdaptWhiteKingKingSideCastling() {
        when(king.getPosition())
            .thenReturn(positionOf("e1"));

        when(board.getKing(any()))
            .thenReturn(Optional.of(king));

        var adapter = new KingCastlingActionAdapter(board, Colors.WHITE, CastlingSide.KING_SIDE);
        var action = adapter.adapt("O-O");

        assertEquals("e1 g1", action);
    }

    @Test
    void testAdaptWhiteKingQueenSideCastling() {
        when(king.getPosition())
            .thenReturn(positionOf("e1"));

        when(board.getKing(any()))
            .thenReturn(Optional.of(king));

        var adapter = new KingCastlingActionAdapter(board, Colors.WHITE, CastlingSide.QUEEN_SIDE);
        var action = adapter.adapt("O-O-O");

        assertEquals("e1 c1", action);
    }

    @Test
    void testAdaptBlackKingKingSideCastling() {
        when(king.getPosition())
            .thenReturn(positionOf("e8"));

        when(board.getKing(any()))
            .thenReturn(Optional.of(king));

        var adapter = new KingCastlingActionAdapter(board, Colors.BLACK, CastlingSide.KING_SIDE);
        var action = adapter.adapt("O-O");

        assertEquals("e8 g8", action);
    }

    @Test
    void testAdaptBlackKingQueenSideCastling() {
        when(king.getPosition())
            .thenReturn(positionOf("e8"));

        when(board.getKing(any()))
            .thenReturn(Optional.of(king));

        var adapter = new KingCastlingActionAdapter(board, Colors.BLACK, CastlingSide.QUEEN_SIDE);
        var action = adapter.adapt("O-O-O");

        assertEquals("e8 c8", action);
    }

    @Test
    void testAdaptWithInvalidAction() {
        var adapter = new KingCastlingActionAdapter(board, Colors.WHITE, CastlingSide.KING_SIDE);

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> adapter.adapt("O")
        );

        assertEquals("Invalid action format: 'O'", thrown.getMessage());
    }

    @Test
    void testAdaptForUnknownKingAction() {
        when(board.getKing(any()))
            .thenReturn(Optional.empty());

        var adapter = new KingCastlingActionAdapter(board, Colors.BLACK, CastlingSide.QUEEN_SIDE);

        var thrown = assertThrows(
                IllegalActionException.class,
                () -> adapter.adapt("O-O-O")
        );

        assertEquals("Unknown source piece for action: 'O-O-O'", thrown.getMessage());
    }
}