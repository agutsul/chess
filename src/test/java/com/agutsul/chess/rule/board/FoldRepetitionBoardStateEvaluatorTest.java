package com.agutsul.chess.rule.board;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.action.memento.ActionMementoFactory;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.BoardBuilder;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.JournalImpl;

@ExtendWith(MockitoExtension.class)
public class FoldRepetitionBoardStateEvaluatorTest {

    @Test
    @SuppressWarnings("unchecked")
    void testFoldRepetitionForEmptyJournal() {
        var board = mock(Board.class);

        var journal = mock(Journal.class);
        when(journal.get(any(Color.class)))
            .thenReturn(emptyList());

        var evaluator = new FoldRepetitionBoardStateEvaluator(board, journal);
        var boardState = evaluator.evaluate(Colors.WHITE);

        assertTrue(boardState.isEmpty());
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testFoldRepetitionForNonEmptyJournal() {
        var board = new BoardBuilder()
                .withWhitePawn("e2")
                .withBlackPawn("e7")
                .withWhiteRook("a1")
                .withBlackRook("h8")
                .withWhiteKing("e1")
                .withBlackKing("e8")
                .build();

        var whiteRook = board.getPiece("a1").get();
        var whitePawn = board.getPiece("e2").get();

        var moveAction1 = new PieceMoveAction(whiteRook, board.getPosition("b1").get());
        var moveAction2 = new PieceMoveAction(whiteRook, board.getPosition("a1").get());
        var moveAction3 = new PieceMoveAction(whitePawn, board.getPosition("e3").get());

        var memento1 = ActionMementoFactory.createMemento(moveAction1);
        var memento2 = ActionMementoFactory.createMemento(moveAction2);
        var memento3 = ActionMementoFactory.createMemento(moveAction3);

        var blackRook = board.getPiece("h8").get();

        var moveAction4 = new PieceMoveAction(blackRook, board.getPosition("h7").get());
        var moveAction5 = new PieceMoveAction(blackRook, board.getPosition("h8").get());

        var memento4 = ActionMementoFactory.createMemento(moveAction4);
        var memento5 = ActionMementoFactory.createMemento(moveAction5);

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
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testFoldRepetitionForWithThreeRepetitions() {
        var board = new BoardBuilder()
                .withWhitePawn("e2")
                .withBlackPawn("e7")
                .withWhiteRook("a1")
                .withBlackRook("h8")
                .withWhiteKing("e1")
                .withBlackKing("e8")
                .build();

        var whiteRook = board.getPiece("a1").get();
        var whitePawn = board.getPiece("e2").get();

        var moveAction1 = new PieceMoveAction(whiteRook, board.getPosition("b1").get());
        var moveAction2 = new PieceMoveAction(whiteRook, board.getPosition("c1").get());
        var moveAction3 = new PieceMoveAction(whitePawn, board.getPosition("e3").get());

        var memento1 = ActionMementoFactory.createMemento(moveAction1);
        var memento2 = ActionMementoFactory.createMemento(moveAction2);
        var memento3 = ActionMementoFactory.createMemento(moveAction3);

        var blackRook = board.getPiece("h8").get();

        var moveAction4 = new PieceMoveAction(blackRook, board.getPosition("h7").get());
        var moveAction5 = new PieceMoveAction(blackRook, board.getPosition("h6").get());

        var memento4 = ActionMementoFactory.createMemento(moveAction4);
        var memento5 = ActionMementoFactory.createMemento(moveAction5);

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
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testFoldRepetitionForWithFiveRepetitions() {
        var board = new BoardBuilder()
                .withWhitePawn("e2")
                .withBlackPawn("e7")
                .withWhiteRook("a1")
                .withBlackRook("h8")
                .withWhiteKing("e1")
                .withBlackKing("e8")
                .build();

        var whiteRook = board.getPiece("a1").get();
        var whitePawn = board.getPiece("e2").get();

        var moveAction1 = new PieceMoveAction(whiteRook, board.getPosition("b1").get());
        var moveAction2 = new PieceMoveAction(whiteRook, board.getPosition("c1").get());
        var moveAction3 = new PieceMoveAction(whitePawn, board.getPosition("e3").get());

        var memento1 = ActionMementoFactory.createMemento(moveAction1);
        var memento2 = ActionMementoFactory.createMemento(moveAction2);
        var memento3 = ActionMementoFactory.createMemento(moveAction3);

        var blackRook = board.getPiece("h8").get();

        var moveAction4 = new PieceMoveAction(blackRook, board.getPosition("h7").get());
        var moveAction5 = new PieceMoveAction(blackRook, board.getPosition("h6").get());

        var memento4 = ActionMementoFactory.createMemento(moveAction4);
        var memento5 = ActionMementoFactory.createMemento(moveAction5);

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
