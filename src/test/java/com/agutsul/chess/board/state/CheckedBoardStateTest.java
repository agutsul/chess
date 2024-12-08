package com.agutsul.chess.board.state;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.BoardBuilder;
import com.agutsul.chess.color.Colors;

@ExtendWith(MockitoExtension.class)
public class CheckedBoardStateTest {

    ExecutorService executorService;

    @BeforeEach
    void setUp() {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @AfterEach
    void tearDown() {
        try {
            this.executorService.shutdown();
            if (!this.executorService.awaitTermination(1, MICROSECONDS)) {
                this.executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            this.executorService.shutdownNow();
        }
    }

    @Test
    void testCheckedPieceActionsFiltered() {
        var board = new BoardBuilder()
                .withBlackKing("c6")
                .withBlackPawn("a7")
                .withBlackRook("e8")
                .withWhiteBishop("e4")
                .withWhiteKing("e3")
                .build();

        board.setExecutorService(executorService);
        board.setState(new CheckedBoardState(board, Colors.BLACK));

        var blackPawn = board.getPiece("a7").get();
        assertTrue(board.getActions(blackPawn).isEmpty());

        var blackRook = board.getPiece("e8").get();
        var rookActions = board.getActions(blackRook);
        assertEquals(1, rookActions.size());
    }

    @Test
    void testCheckedPieceActionsBlocked() {
        var board = new BoardBuilder()
                .withBlackKing("h8")
                .withBlackPawn("h7")
                .withBlackRook("g8")
                .withWhiteBishop("b2")
                .withWhiteKing("e3")
                .build();

        board.setExecutorService(executorService);
        board.setState(new CheckedBoardState(board, Colors.BLACK));

        var blackPawn = board.getPiece("h7").get();
        assertTrue(board.getActions(blackPawn).isEmpty());

        var blackRook = board.getPiece("g8").get();
        var rookActions = board.getActions(blackRook);
        assertEquals(1, rookActions.size());
    }

    @Test
    void testCheckedPieceActionsMovable() {
        var board = new BoardBuilder()
                .withWhiteQueen("e8")
                .withWhiteKing("e1")
                .withBlackPawn("a7")
                .withBlackKing("a8")
                .build();

        board.setExecutorService(executorService);
        board.setState(new CheckedBoardState(board, Colors.BLACK));

        var blackKing = board.getKing(Colors.BLACK).get();
        assertFalse(board.getActions(blackKing).isEmpty());
    }
}