package com.agutsul.chess.rule.check;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;

@ExtendWith(MockitoExtension.class)
public class CheckActionEvaluatorImplTest {

    @Mock
    Board board;
    @Mock
    KingPiece<?> kingPiece;

    @Test
    void testCheckActionEvaluation() {
        var evaluatorMock = mock(CheckActionEvaluator.class);

        var evaluator = new CheckActionEvaluatorImpl(evaluatorMock);
        evaluator.evaluate(kingPiece);

        verify(evaluatorMock, times(1)).evaluate(any());
    }
}