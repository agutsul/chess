package com.agutsul.chess.rule.board;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;

public final class BoardStateEvaluatorImpl
        implements BoardStateEvaluator<BoardState> {

    private static final Logger LOGGER = getLogger(BoardStateEvaluatorImpl.class);

    private final BoardStateEvaluator<BoardState> evaluator;

    public BoardStateEvaluatorImpl(Board board,
                                   Journal<ActionMemento<?,?>> journal) {

        this.evaluator = new CompositeBoardStateEvaluator(board,
                new CheckedBoardStateEvaluator(board),
                new CheckMatedBoardStateEvaluator(board),
                new StaleMatedBoardStateEvaluator(board),
                new FoldRepetitionBoardStateEvaluator(board, journal),
                new MovesBoardStateEvaluator(board, journal),
                new InsufficientMaterialBoardStateEvaluator(board)
        );
    }

    @Override
    public BoardState evaluate(Color color) {
        var boardState = evaluator.evaluate(color);
        LOGGER.info("{}: Board state: {}", color, boardState);
        return boardState;
    }
}