package com.agutsul.chess.rule.board;

import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;
import static com.agutsul.chess.board.state.BoardStateFactory.defaultBoardState;
import static java.util.Comparator.comparing;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.CompositeBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.journal.Journal;

public final class BoardStateEvaluatorImpl
        implements BoardStateEvaluator<BoardState> {

    private static final Logger LOGGER = getLogger(BoardStateEvaluatorImpl.class);

    private final Board board;

    private final BoardStateEvaluator<List<BoardState>> compositeEvaluator;

    public BoardStateEvaluatorImpl(Board board, Journal<ActionMemento<?,?>> journal,
                                   ForkJoinPool forkJoinPool) {

        this(board, createEvaluator(board, journal, forkJoinPool));
    }

    public BoardStateEvaluatorImpl(Board board, Journal<ActionMemento<?,?>> journal) {
        this(board, journal, commonPool());
    }

    private BoardStateEvaluatorImpl(Board board,
                                    BoardStateEvaluator<List<BoardState>> compositeEvaluator) {
        this.board = board;
        this.compositeEvaluator = compositeEvaluator;
    }

    @Override
    public BoardState evaluate(Color color) {
        var boardStates = compositeEvaluator.evaluate(color);

        LOGGER.info("{}: Board state: {}", color, boardStates);

        if (boardStates.isEmpty()) {
            return defaultBoardState(board, color);
        }

        if (boardStates.size() == 1) {
            return boardStates.iterator().next();
        }

        var checkMatedState = boardStates.stream()
                .filter(boardState -> boardState.isType(CHECK_MATED))
                .findFirst();

        if (checkMatedState.isPresent()) {
            return checkMatedState.get();
        }

        var comparator = boardStates.stream().anyMatch(BoardState::isTerminal)
                ? comparing(BoardState::isTerminal).reversed() //  terminal states first
                : comparing(BoardState::getType);

        return new CompositeBoardState(boardStates.stream().sorted(comparator).toList());
    }

    @SuppressWarnings("unchecked")
    private static BoardStateEvaluator<List<BoardState>> createEvaluator(Board board,
                                                                         Journal<ActionMemento<?,?>> journal,
                                                                         ForkJoinPool forkJoinPool) {

        return new CompositeBoardStateEvaluator(board,
                new BoardStatisticStateEvaluator(new MovesBoardStateEvaluator(board, journal)),
                new BoardStatisticStateEvaluator(new FoldRepetitionBoardStateEvaluator(board, journal)),
                new CheckableBoardStateEvaluator(board),
                new StaleMatedBoardStateEvaluator(board),
                new InsufficientMaterialBoardStateEvaluator(board, journal, forkJoinPool)
        );
    }
}