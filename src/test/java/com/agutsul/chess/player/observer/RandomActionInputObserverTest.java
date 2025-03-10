package com.agutsul.chess.player.observer;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.PieceEnPassantAction;
import com.agutsul.chess.board.StringBoardBuilder;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.Player;

@ExtendWith(MockitoExtension.class)
public class RandomActionInputObserverTest {

    @Mock
    AbstractPlayableGame game;
    @Mock
    Player player;
    @Mock
    Random random;

    @InjectMocks
    RandomActionInputObserver inputObserver;

    @Test
    void testPawnMoveAction() {
        var board = new StringBoardBuilder()
                .withWhitePawn("e3")
                .build();

        when(game.getBoard())
            .thenReturn(board);

        when(player.getColor())
            .thenReturn(Colors.WHITE);

        var command = inputObserver.getActionCommand();
        assertEquals("e3 e4", command);
    }

    @Test
    void testPawnPromotionAction() {
        var board = new StringBoardBuilder()
                .withWhitePawn("e7")
                .build();

        when(game.getBoard())
            .thenReturn(board);

        when(player.getColor())
            .thenReturn(Colors.WHITE);

        var command = inputObserver.getActionCommand();
        assertEquals("e7 e8", command);
    }

    @Test
    void testPawnCaptureAction() {
        var board = new StringBoardBuilder()
                .withWhitePawn("e4")
                .withBlackPawn("f5")
                .build();

        when(game.getBoard())
            .thenReturn(board);

        when(player.getColor())
            .thenReturn(Colors.WHITE);

        when(random.nextInt(anyInt(), anyInt()))
            .thenReturn(0);

        var command = inputObserver.getActionCommand();
        assertEquals("e4 f5", command);
    }

    @Test
    void testPawnEnPassantAction() {
        var board = spy(new StringBoardBuilder()
                .withWhitePawn("e4")
                .withBlackPawn("f4")
                .build()
        );

        when(player.getColor())
            .thenReturn(Colors.BLACK);

        doCallRealMethod()
            .when(board).getPiece(anyString());

        var predator = board.getPiece("f4").get();
        var victim   = board.getPiece("e4").get();

        var action = new PieceEnPassantAction<>(
                (PawnPiece<?>) predator,
                (PawnPiece<?>) victim,
                positionOf("e3")
        );

        doReturn(List.of(action))
            .when(board).getActions(any());

        when(game.getBoard())
            .thenReturn(board);

        var command = inputObserver.getActionCommand();
        assertEquals("f4 e3", command);
    }

    @Test
    void testCastlingAction() {
        var board = new StringBoardBuilder()
                .withWhiteKing("e1")
                .withWhiteRook("h1")
                .build();

        when(game.getBoard())
            .thenReturn(board);

        when(player.getColor())
            .thenReturn(Colors.WHITE);

        when(random.nextInt(anyInt(), anyInt()))
            .thenReturn(0);

        var command = inputObserver.getActionCommand();
        assertEquals("e1 g1", command);
    }

    @Test
    void testDefeatAction() {
        when(game.getBoard())
            .thenReturn(new StringBoardBuilder().build());

        when(player.getColor())
            .thenReturn(Colors.WHITE);

        var command = inputObserver.getActionCommand();
        assertEquals(AbstractPlayerInputObserver.DEFEAT_COMMAND, command);
    }

    @Test
    void testGetRookPromotionType() {
        when(random.nextDouble())
            .thenReturn(0.0);

        var command = inputObserver.getPromotionPieceType();
        assertEquals(Piece.Type.ROOK.code(), command);
    }

    @Test
    void testGetKnightPromotionType() {
        when(random.nextDouble())
            .thenReturn(0.3);

        var command = inputObserver.getPromotionPieceType();
        assertEquals(Piece.Type.KNIGHT.code(), command);
    }

    @Test
    void testGetBishopPromotionType() {
        when(random.nextDouble())
            .thenReturn(0.6);

        var command = inputObserver.getPromotionPieceType();
        assertEquals(Piece.Type.BISHOP.code(), command);
    }

    @Test
    void testGetQueenPromotionType() {
        when(random.nextDouble())
            .thenReturn(0.8);

        var command = inputObserver.getPromotionPieceType();
        assertEquals(Piece.Type.QUEEN.code(), command);
    }
}