package com.agutsul.chess.board.state;

import static com.agutsul.chess.board.state.BoardStateFactory.insufficientMaterialBoardState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.state.InsufficientMaterialBoardState.Pattern;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.rule.board.BoardStateEvaluatorImpl;

@ExtendWith(MockitoExtension.class)
public class InsufficientMaterialBoardStateTest {

    @Test
    void testGetActions() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("a2")
                .build();

        board.setState(insufficientMaterialBoardState(board, Colors.WHITE, Pattern.NO_ACTIONS_LEAD_TO_CHECKMATE));

        var whitePawn = board.getPiece("a2").get();
        assertFalse(board.getActions(whitePawn).isEmpty());
    }

    @Test
    void testGetImpacts() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("a2")
                .build();

        board.setState(insufficientMaterialBoardState(board, Colors.WHITE, Pattern.NO_ACTIONS_LEAD_TO_CHECKMATE));

        var whitePawn = board.getPiece("a2").get();
        assertFalse(board.getImpacts(whitePawn).isEmpty());
    }

    @Test
    void testGetPattern() {
        var board = new LabeledBoardBuilder()
                .withWhiteKing("a2")
                .withBlackKing("a8")
                .withBlackPawn("a7")
                .build();

        var evaluator = new BoardStateEvaluatorImpl(board, new JournalImpl());

        var whiteBoardState = evaluator.evaluate(Colors.WHITE);

        assertEquals(BoardState.Type.INSUFFICIENT_MATERIAL, whiteBoardState.getType());
        assertEquals(InsufficientMaterialBoardState.Pattern.SINGLE_KING,
                ((InsufficientMaterialBoardState) whiteBoardState).getPattern()
        );

        assertEquals("INSUFFICIENT_MATERIAL(WHITE: SINGLE_KING)", whiteBoardState.toString());

        var blackBoardState = evaluator.evaluate(Colors.BLACK);
        assertEquals(BoardState.Type.INSUFFICIENT_MATERIAL, blackBoardState.getType());
        assertEquals(InsufficientMaterialBoardState.Pattern.KING_AND_BLOCKED_PAWNS,
                ((InsufficientMaterialBoardState) blackBoardState).getPattern()
        );

        assertEquals("INSUFFICIENT_MATERIAL(BLACK: KING_AND_BLOCKED_PAWNS)", blackBoardState.toString());
    }
}