package com.agutsul.chess.rule.board;

import static com.agutsul.chess.activity.action.memento.ActionMementoFactory.createMemento;
import static com.agutsul.chess.rule.board.MovesBoardStateEvaluator.FIFTY_MOVES;
import static com.agutsul.chess.rule.board.MovesBoardStateEvaluator.SEVENTY_FIVE_MOVES;
import static java.util.Collections.emptyList;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.position.Position;

@ExtendWith(MockitoExtension.class)
public class MovesBoardStateEvaluatorTest {

    ExecutorService executorService;

    @Mock
    Board board;

    void setUp() {
        this.executorService = Executors.newSingleThreadExecutor();
        when(board.getExecutorService())
            .thenReturn(executorService);
    }

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
    void testMovesBoardStateEvaluatorWithEmptyJournal() {
        var journal = mock(Journal.class);
        when(journal.get(any()))
            .thenReturn(emptyList());

        @SuppressWarnings("unchecked")
        var evaluator = new MovesBoardStateEvaluator(board, journal);
        var result = evaluator.evaluate(Colors.WHITE);

        assertTrue(result.isEmpty());
    }

    @Test
    void testMovesBoardStateEvaluatorWith75QueenMoves() {
        try {
            setUp();

            var result = getBoardStateEvaluatorWithQueenMovesLimit(SEVENTY_FIVE_MOVES);
            var boardState = result.get();

            assertEquals(BoardState.Type.SEVENTY_FIVE_MOVES, boardState.getType());
        } finally {
            tearDown();
        }
    }

    @Test
    void testMovesBoardStateEvaluatorWith50QueenMoves() {
        try {
            setUp();

            var result = getBoardStateEvaluatorWithQueenMovesLimit(FIFTY_MOVES);
            var boardState = result.get();

            assertEquals(BoardState.Type.FIFTY_MOVES, boardState.getType());
        } finally {
            tearDown();
        }
    }

    @Test
    void testMovesBoardStateEvaluatorWith75PawnMoves() {
        try {
            setUp();

            getBoardStateEvaluatorWithPawnMovesLimit(SEVENTY_FIVE_MOVES);
        } finally {
            tearDown();
        }
    }

    @Test
    void testMovesBoardStateEvaluatorWith50PawnMoves() {
        try {
            setUp();

            getBoardStateEvaluatorWithPawnMovesLimit(FIFTY_MOVES);
        } finally {
            tearDown();
        }
    }

    private Optional<BoardState> getBoardStateEvaluatorWithQueenMovesLimit(int limit) {
        var queen = mock(QueenPiece.class);
        when(queen.getType())
            .thenReturn(Piece.Type.QUEEN);
        when(queen.getColor())
            .thenReturn(Colors.WHITE);

        var position = mock(Position.class);
        var memento = createMemento(new PieceMoveAction<>(queen, position));

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
        var moveMemento = createMemento(new PieceMoveAction<>(pawn1, position));

        var pawn2 = mock(PawnPiece.class);
        when(pawn2.getPosition())
            .thenReturn(position);

        var captureMemento = createMemento(new PieceCaptureAction<>(pawn1, pawn2));

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