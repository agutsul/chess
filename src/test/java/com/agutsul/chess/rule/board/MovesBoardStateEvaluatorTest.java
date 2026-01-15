package com.agutsul.chess.rule.board;

import static com.agutsul.chess.activity.action.memento.ActionMementoFactory.createMemento;
import static com.agutsul.chess.board.state.BoardState.Type.FIFTY_MOVES;
import static com.agutsul.chess.board.state.BoardState.Type.SEVENTY_FIVE_MOVES;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.board.StandardBoard;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.FiftyMovesBoardState;
import com.agutsul.chess.board.state.SeventyFiveMovesBoardState;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.position.Position;

@ExtendWith(MockitoExtension.class)
public class MovesBoardStateEvaluatorTest {

    @AutoClose
    ExecutorService executorService = newSingleThreadExecutor();

    @Mock
    StandardBoard board;

    @BeforeEach
    void setUp() {
        when(board.getExecutorService())
            .thenReturn(executorService);
    }

    @Test
    void testMovesBoardStateEvaluatorWith75QueenMoves() {
        var result = getBoardStateEvaluatorWithQueenMovesLimit(SeventyFiveMovesBoardState.MOVES);
        var boardState = result.get();

        assertEquals(SEVENTY_FIVE_MOVES, boardState.getType());
    }

    @Test
    void testMovesBoardStateEvaluatorWith50QueenMoves() {
        var result = getBoardStateEvaluatorWithQueenMovesLimit(FiftyMovesBoardState.MOVES);
        var boardState = result.get();

        assertEquals(FIFTY_MOVES, boardState.getType());
    }

    @Test
    void testMovesBoardStateEvaluatorWith75PawnMoves() {
        getBoardStateEvaluatorWithPawnMovesLimit(SeventyFiveMovesBoardState.MOVES);
    }

    @Test
    void testMovesBoardStateEvaluatorWith50PawnMoves() {
        getBoardStateEvaluatorWithPawnMovesLimit(FiftyMovesBoardState.MOVES);
    }

    private Optional<BoardState> getBoardStateEvaluatorWithQueenMovesLimit(int limit) {
        var queen = mock(QueenPiece.class);
        when(queen.getType())
            .thenReturn(Piece.Type.QUEEN);
        when(queen.getColor())
            .thenReturn(Colors.WHITE);

        var position = mock(Position.class);
        var memento = createMemento(board, new PieceMoveAction<>(queen, position));

        var journal = new JournalImpl();
        for (int i = 0; i <= limit; i++) {
            journal.add(memento);
        }

        var evaluator = new MovesBoardStateEvaluator(board, journal);
        var result = evaluator.evaluate(Colors.WHITE);

        assertTrue(result.isPresent());
        return result;
    }

    private Optional<BoardState> getBoardStateEvaluatorWithPawnMovesLimit(int limit) {
        var pawn1 = mock(PawnPiece.class);
        when(pawn1.getType())
            .thenReturn(Piece.Type.PAWN);
        when(pawn1.getColor())
            .thenReturn(Colors.WHITE);

        var position = mock(Position.class);
        var moveMemento = createMemento(board, new PieceMoveAction<>(pawn1, position));

        var pawn2 = mock(PawnPiece.class);
        when(pawn2.getPosition())
            .thenReturn(position);

        var captureMemento = createMemento(board, new PieceCaptureAction<>(pawn1, pawn2));

        var journal = new JournalImpl();
        for (int i = 0; i <= limit; i++) {
            journal.add(moveMemento);
            journal.add(captureMemento);
        }

        var evaluator = new MovesBoardStateEvaluator(board, journal);
        var result = evaluator.evaluate(Colors.WHITE);

        assertTrue(result.isEmpty());
        return result;
    }
}