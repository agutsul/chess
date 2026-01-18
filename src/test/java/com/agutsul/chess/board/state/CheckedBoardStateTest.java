package com.agutsul.chess.board.state;

import static com.agutsul.chess.board.state.BoardStateFactory.checkedBoardState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.color.Colors;

@ExtendWith(MockitoExtension.class)
public class CheckedBoardStateTest {

    @Test
    void testCheckedPieceActionsFiltered() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("c6")
                .withBlackPawn("a7")
                .withBlackRook("e8")
                .withWhiteBishop("e4")
                .withWhiteKing("e3")
                .build();

        var whiteBishop = board.getPiece("e4").get();
        board.setState(checkedBoardState(board, Colors.BLACK, whiteBishop));

        var blackPawn = board.getPiece("a7").get();
        assertTrue(board.getActions(blackPawn).isEmpty());

        var blackRook = board.getPiece("e8").get();
        var rookActions = board.getActions(blackRook);
        assertEquals(1, rookActions.size());
    }

    @Test
    void testCheckedPieceActionsBlocked() {
        var board = new LabeledBoardBuilder()
                .withBlackKing("h8")
                .withBlackPawn("h7")
                .withBlackRook("g8")
                .withWhiteBishop("b2")
                .withWhiteKing("e3")
                .build();

        var whiteBishop = board.getPiece("b2").get();
        board.setState(checkedBoardState(board, Colors.BLACK, whiteBishop));

        var blackPawn = board.getPiece("h7").get();
        assertTrue(board.getActions(blackPawn).isEmpty());

        var blackRook = board.getPiece("g8").get();
        var rookActions = board.getActions(blackRook);
        assertEquals(1, rookActions.size());
    }

    @Test
    void testCheckedPieceActionsMovable() {
        var board = new LabeledBoardBuilder()
                .withWhiteQueen("e8")
                .withWhiteKing("e1")
                .withBlackPawn("a7")
                .withBlackKing("a8")
                .build();

        var whiteQueen = board.getPiece("e8").get();
        board.setState(checkedBoardState(board, Colors.BLACK, whiteQueen));

        var blackKing = board.getKing(Colors.BLACK).get();
        assertFalse(board.getActions(blackKing).isEmpty());
    }
}