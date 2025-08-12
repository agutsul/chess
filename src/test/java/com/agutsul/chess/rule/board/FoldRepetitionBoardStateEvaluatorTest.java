package com.agutsul.chess.rule.board;

import static com.agutsul.chess.activity.action.memento.ActionMementoFactory.createMemento;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.LabeledBoardBuilder;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.RookPiece;

@ExtendWith(MockitoExtension.class)
public class FoldRepetitionBoardStateEvaluatorTest {

    @Mock
    Board board;
    @Mock
    Journal<ActionMemento<?,?>> journal;

    @InjectMocks
    FoldRepetitionBoardStateEvaluator evaluator;

    @Test
    void testFoldRepetitionForEmptyJournal() {
        when(journal.size(any(Color.class)))
            .thenReturn(0);

        var boardState = evaluator.evaluate(Colors.WHITE);
        assertTrue(boardState.isEmpty());
    }

    @Test
    void testFoldRepetitionForNonEmptyJournal() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e2")
                .withBlackPawn("e7")
                .withWhiteRook("a1")
                .withBlackRook("h8")
                .withWhiteKing("e1")
                .withBlackKing("e8")
                .build();

        var whiteRook = (RookPiece<Color>) board.getPiece("a1").get();
        var whitePawn = (PawnPiece<Color>) board.getPiece("e2").get();

        var moveAction1 = new PieceMoveAction<>(whiteRook, board.getPosition("b1").get());
        var moveAction2 = new PieceMoveAction<>(whiteRook, board.getPosition("a1").get());
        var moveAction3 = new PieceMoveAction<>(whitePawn, board.getPosition("e3").get());

        var memento1 = createMemento(board, moveAction1);
        var memento2 = createMemento(board, moveAction2);
        var memento3 = createMemento(board, moveAction3);

        var blackRook = (RookPiece<Color>) board.getPiece("h8").get();

        var moveAction4 = new PieceMoveAction<>(blackRook, board.getPosition("h7").get());
        var moveAction5 = new PieceMoveAction<>(blackRook, board.getPosition("h8").get());

        var memento4 = createMemento(board, moveAction4);
        var memento5 = createMemento(board, moveAction5);

        var journal = new JournalImpl();
        journal.add(memento1);
        journal.add(memento2);
        journal.add(memento1);
        journal.add(memento2);
        journal.add(memento4);
        journal.add(memento5);
        journal.add(memento3);

        var evaluator = new FoldRepetitionBoardStateEvaluator(board, journal);
        var boardState = evaluator.evaluate(Colors.WHITE);

        assertTrue(boardState.isEmpty());
    }

    @Test
    void testFoldRepetitionForWithThreeRepetitions() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e2")
                .withBlackPawn("e7")
                .withWhiteRook("a1")
                .withBlackRook("h8")
                .withWhiteKing("e1")
                .withBlackKing("e8")
                .build();

        var whiteRook = (RookPiece<Color>) board.getPiece("a1").get();
        var whitePawn = (PawnPiece<Color>) board.getPiece("e2").get();

        var moveAction1 = new PieceMoveAction<>(whiteRook, board.getPosition("b1").get());
        var moveAction2 = new PieceMoveAction<>(whiteRook, board.getPosition("c1").get());
        var moveAction3 = new PieceMoveAction<>(whitePawn, board.getPosition("e3").get());

        var memento1 = createMemento(board, moveAction1);
        var memento2 = createMemento(board, moveAction2);
        var memento3 = createMemento(board, moveAction3);

        var blackRook = (RookPiece<Color>) board.getPiece("h8").get();

        var moveAction4 = new PieceMoveAction<>(blackRook, board.getPosition("h7").get());
        var moveAction5 = new PieceMoveAction<>(blackRook, board.getPosition("h6").get());

        var memento4 = createMemento(board, moveAction4);
        var memento5 = createMemento(board, moveAction5);

        var journal = new JournalImpl();
        journal.add(memento1);
        journal.add(memento2);
        journal.add(memento1);
        journal.add(memento2);
        journal.add(memento4);
        journal.add(memento5);
        journal.add(memento3);
        journal.add(memento1);
        journal.add(memento2);

        var evaluator = new FoldRepetitionBoardStateEvaluator(board, journal);
        var boardState = evaluator.evaluate(Colors.WHITE);

        assertTrue(boardState.isPresent());

        var state = boardState.get();
        assertEquals(BoardState.Type.THREE_FOLD_REPETITION, state.getType());
    }

    @Test
    void testFoldRepetitionForWithFiveRepetitions() {
        var board = new LabeledBoardBuilder()
                .withWhitePawn("e2")
                .withBlackPawn("e7")
                .withWhiteRook("a1")
                .withBlackRook("h8")
                .withWhiteKing("e1")
                .withBlackKing("e8")
                .build();

        var whiteRook = (RookPiece<Color>) board.getPiece("a1").get();
        var whitePawn = (PawnPiece<Color>) board.getPiece("e2").get();

        var moveAction1 = new PieceMoveAction<>(whiteRook, board.getPosition("b1").get());
        var moveAction2 = new PieceMoveAction<>(whiteRook, board.getPosition("c1").get());
        var moveAction3 = new PieceMoveAction<>(whitePawn, board.getPosition("e3").get());

        var memento1 = createMemento(board, moveAction1);
        var memento2 = createMemento(board, moveAction2);
        var memento3 = createMemento(board, moveAction3);

        var blackRook = (RookPiece<Color>) board.getPiece("h8").get();

        var moveAction4 = new PieceMoveAction<>(blackRook, board.getPosition("h7").get());
        var moveAction5 = new PieceMoveAction<>(blackRook, board.getPosition("h6").get());

        var memento4 = createMemento(board, moveAction4);
        var memento5 = createMemento(board, moveAction5);

        var journal = new JournalImpl();
        journal.add(memento1);
        journal.add(memento2);
        journal.add(memento1);
        journal.add(memento2);
        journal.add(memento4);
        journal.add(memento5);
        journal.add(memento3);
        journal.add(memento1);
        journal.add(memento2);
        journal.add(memento1);
        journal.add(memento2);
        journal.add(memento1);
        journal.add(memento2);

        var evaluator = new FoldRepetitionBoardStateEvaluator(board, journal);
        var boardState = evaluator.evaluate(Colors.WHITE);

        assertTrue(boardState.isPresent());

        var state = boardState.get();
        assertEquals(BoardState.Type.FIVE_FOLD_REPETITION, state.getType());
    }
}