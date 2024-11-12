package com.agutsul.chess.rule.board;

import static com.agutsul.chess.action.memento.ActionMementoFactory.createMemento;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceMoveAction;
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

    @Test
    void testMovesBoardStateEvaluatorWithEmptyJournal() {
        var board = mock(Board.class);

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
        var result = getBoardStateEvaluatorWithQueenMovesLimit(MovesBoardStateEvaluator.SEVENTY_FIVE_MOVES);
        var boardState = result.get();
        assertEquals(BoardState.Type.SEVENTY_FIVE_MOVES, boardState.getType());
    }

    @Test
    void testMovesBoardStateEvaluatorWith50QueenMoves() {
        var result = getBoardStateEvaluatorWithQueenMovesLimit(MovesBoardStateEvaluator.FIFTY_MOVES);
        var boardState = result.get();
        assertEquals(BoardState.Type.FIFTY_MOVES, boardState.getType());
    }

    @Test
    void testMovesBoardStateEvaluatorWith75PawnMoves() {
        getBoardStateEvaluatorWithPawnMovesLimit(MovesBoardStateEvaluator.SEVENTY_FIVE_MOVES);
    }

    @Test
    void testMovesBoardStateEvaluatorWith50PawnMoves() {
        getBoardStateEvaluatorWithPawnMovesLimit(MovesBoardStateEvaluator.FIFTY_MOVES);
    }

    private Optional<BoardState> getBoardStateEvaluatorWithQueenMovesLimit(int limit) {
        var board = mock(Board.class);

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
        var board = mock(Board.class);

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