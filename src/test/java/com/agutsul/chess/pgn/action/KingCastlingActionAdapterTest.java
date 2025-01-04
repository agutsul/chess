package com.agutsul.chess.pgn.action;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCastlingAction;
import com.agutsul.chess.activity.action.PieceCastlingAction.CastlingMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.exception.UnknownPieceException;
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

        when(board.getActions(eq(king), eq(Action.Type.CASTLING)))
            .thenAnswer(inv -> {
                var king = inv.getArgument(0, KingPiece.class);
                return List.of(new PieceCastlingAction<>(
                        Castlingable.Side.KING,
                        new CastlingMoveAction<>(king, positionOf("g1")),
                        null
                ));
            });

        var adapter = new KingCastlingActionAdapter(board, Colors.WHITE);
        var action = adapter.adapt("O-O");

        assertEquals("e1 g1", action);
    }

    @Test
    void testAdaptWhiteKingQueenSideCastling() {
        when(king.getPosition())
            .thenReturn(positionOf("e1"));

        when(board.getKing(any()))
            .thenReturn(Optional.of(king));

        when(board.getActions(eq(king), eq(Action.Type.CASTLING)))
            .thenAnswer(inv -> {
                var king = inv.getArgument(0, KingPiece.class);
                return List.of(new PieceCastlingAction<>(
                        Castlingable.Side.QUEEN,
                        new CastlingMoveAction<>(king, positionOf("c1")),
                        null
                ));
            });

        var adapter = new KingCastlingActionAdapter(board, Colors.WHITE);
        var action = adapter.adapt("O-O-O");

        assertEquals("e1 c1", action);
    }

    @Test
    void testAdaptBlackKingKingSideCastling() {
        when(king.getPosition())
            .thenReturn(positionOf("e8"));

        when(board.getKing(any()))
            .thenReturn(Optional.of(king));

        when(board.getActions(eq(king), eq(Action.Type.CASTLING)))
            .thenAnswer(inv -> {
                var king = inv.getArgument(0, KingPiece.class);
                return List.of(new PieceCastlingAction<>(
                        Castlingable.Side.KING,
                        new CastlingMoveAction<>(king, positionOf("g8")),
                        null
                ));
            });

        var adapter = new KingCastlingActionAdapter(board, Colors.BLACK);
        var action = adapter.adapt("O-O");

        assertEquals("e8 g8", action);
    }

    @Test
    void testAdaptBlackKingQueenSideCastling() {
        when(king.getPosition())
            .thenReturn(positionOf("e8"));

        when(board.getKing(any()))
            .thenReturn(Optional.of(king));

        when(board.getActions(eq(king), eq(Action.Type.CASTLING)))
            .thenAnswer(inv -> {
                var king = inv.getArgument(0, KingPiece.class);
                return List.of(new PieceCastlingAction<>(
                        Castlingable.Side.QUEEN,
                        new CastlingMoveAction<>(king, positionOf("c8")),
                        null
                ));
            });

        var adapter = new KingCastlingActionAdapter(board, Colors.BLACK);
        var action = adapter.adapt("O-O-O");

        assertEquals("e8 c8", action);
    }

    @Test
    void testAdaptWithInvalidAction() {
        var adapter = new KingCastlingActionAdapter(board, Colors.WHITE);

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

        var adapter = new KingCastlingActionAdapter(board, Colors.BLACK);

        var thrown = assertThrows(
                UnknownPieceException.class,
                () -> adapter.adapt("O-O-O")
        );

        assertEquals("Unknown source piece for action: 'O-O-O'", thrown.getMessage());
    }
}